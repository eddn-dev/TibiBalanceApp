/**
 * @file    FirebaseAuthRepository.kt
 * @ingroup data_repository
 * @brief   Implementación de [AuthRepository] que utiliza Firebase Authentication y Firestore.
 *
 * @details Esta clase concreta implementa el contrato [AuthRepository], delegando las
 * operaciones de autenticación al [AuthService] (que a su vez usa [FirebaseAuth])
 * y asegurando la creación/actualización inicial del perfil de usuario en Firestore.
 *
 * - Utiliza el [AuthService] inyectado para manejar las interacciones con FirebaseAuth
 * (login, registro, reset, etc.).
 * - Llama a la función privada [createProfileIfAbsent] después de cada operación
 * exitosa de `signIn`, `signUp` o `signInGoogle` para garantizar que exista un
 * documento básico en la colección `profiles/{uid}` de Firestore.
 * - Implementa la lógica para [syncVerification], actualizando el campo `verified`
 * en Firestore si el correo ha sido verificado en FirebaseAuth.
 * - Ejecuta operaciones potencialmente bloqueantes (llamadas de red a Firebase)
 * en el [CoroutineDispatcher] de IO inyectado (`@IoDispatcher`).
 */
package com.app.tibibalance.data.repository

import android.util.Log // Importar Log si se añade logging en createProfileIfAbsent
import com.app.tibibalance.data.remote.firebase.AuthService
import com.app.tibibalance.di.IoDispatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

/**
 * @class  FirebaseAuthRepository
 * @brief  Repositorio singleton que implementa [AuthRepository] usando Firebase.
 * @see AuthRepository Contrato que define las operaciones de autenticación.
 * @see AuthService Servicio que abstrae las llamadas directas a FirebaseAuth.
 *
 * @constructor Inyecta las dependencias [AuthService], [FirebaseFirestore] y el dispatcher IO
 * proporcionadas por Hilt.
 * @param service Instancia de [AuthService] para delegar operaciones de autenticación.
 * @param db Instancia de [FirebaseFirestore] para interactuar con la base de datos (perfiles).
 * @param io Dispatcher de Coroutines para ejecutar operaciones de red/disco.
 */
@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val service : AuthService,
    private val db      : FirebaseFirestore,
    @IoDispatcher private val io: CoroutineDispatcher
) : AuthRepository {

    /* ───────── Estado de sesión ───────── */

    /** @copydoc AuthRepository.isLoggedIn */
    override val isLoggedIn : Flow<Boolean> = service.authState

    /** @copydoc AuthRepository.currentUser */
    override val currentUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser // Acceso directo y síncrono

    /* ───────── Correo / contraseña ───────── */

    /**
     * @copydoc AuthRepository.signUp
     * @details Implementación específica: Llama a `service.signUp` y luego
     * asegura la creación del documento de perfil en Firestore
     * mediante [createProfileIfAbsent].
     * @note Aunque la interfaz [AuthRepository] documenta un retorno [FirebaseUser],
     * esta implementación actual no lo devuelve explícitamente.
     */
    override suspend fun signUp(email: String, pass: String) {
        val user = service.signUp(email, pass) // Obtiene el usuario del servicio
        createProfileIfAbsent(user) // Crea el perfil si no existe
    }

    /**
     * @copydoc AuthRepository.signIn
     * @details Implementación específica: Llama a `service.signIn` y luego
     * asegura la creación del documento de perfil en Firestore
     * mediante [createProfileIfAbsent].
     * @note Aunque la interfaz [AuthRepository] documenta un retorno [FirebaseUser],
     * esta implementación actual no lo devuelve explícitamente.
     */
    override suspend fun signIn(email: String, pass: String) {
        val user = service.signIn(email, pass) // Obtiene el usuario del servicio
        createProfileIfAbsent(user) // Crea el perfil si no existe
    }

    /* ─────────── Google One-Tap ─────────── */

    /**
     * @copydoc AuthRepository.signInGoogle
     * @details Implementación específica: Llama a `service.signInGoogle` y luego
     * asegura la creación del documento de perfil en Firestore
     * mediante [createProfileIfAbsent].
     * @note Aunque la interfaz [AuthRepository] documenta un retorno [FirebaseUser],
     * esta implementación actual no lo devuelve explícitamente.
     */
    override suspend fun signInGoogle(idToken: String) {
        val user = service.signInGoogle(idToken) // Obtiene el usuario del servicio
        createProfileIfAbsent(user) // Crea el perfil si no existe
    }

    /* ───────────── Utilidades ───────────── */

    /**
     * @copydoc AuthRepository.resetPass
     * @details Delega directamente la llamada a `service.sendPasswordReset`.
     */
    override suspend fun resetPass(email: String): Unit = // Retorna Unit explícitamente
        service.sendPasswordReset(email)

    /**
     * @copydoc AuthRepository.signOut
     * @details Ejecuta la operación `service.signOut()` en el contexto del dispatcher IO.
     */
    override suspend fun signOut(): Unit = withContext(io) { service.signOut() }

    /**
     * @copydoc AuthRepository.syncVerification
     * @details Implementación específica: Primero obtiene el `currentUser`. Si no existe o
     * su correo no está verificado según Firebase Auth, retorna `false`.
     * Si está verificado, consulta el documento de perfil en Firestore. Si el campo
     * `verified` en Firestore es `false` o no existe, lo actualiza a `true`.
     * Se ejecuta en el dispatcher IO.
     * @return `true` si el correo del usuario actual está marcado como verificado en Firebase Auth
     * (y se asegura que Firestore también lo refleje), `false` en caso contrario.
     */
    override suspend fun syncVerification(): Boolean = withContext(io) {
        val user = FirebaseAuth.getInstance().currentUser ?: return@withContext false
        // Opcional: user.reload().await() // Para asegurar que el estado isEmailVerified es el más reciente
        if (!user.isEmailVerified) {
            return@withContext false // Si Firebase dice que no está verificado, terminamos
        }

        // Si Firebase dice que SÍ está verificado, comprobamos Firestore
        val profileRef = db.collection("profiles").document(user.uid)
        try {
            val profileSnapshot = profileRef.get().await()
            val isFirestoreVerified = profileSnapshot.getBoolean("verified") ?: false // Default a false si no existe
            if (!isFirestoreVerified) {
                // Si Firestore no lo tiene como verificado, lo actualizamos
                profileRef.update("verified", true).await()
                Log.i("AuthRepo", "Campo 'verified' actualizado a true en Firestore para ${user.uid}")
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Error al sincronizar verificación en Firestore para ${user.uid}", e)
            // Considerar si se debe retornar false aquí o propagar el error
            return@withContext false // Retorna false si hubo error leyendo/escribiendo Firestore
        }

        true // Retorna true porque user.isEmailVerified era true
    }

    /* ───── Registro con datos extra ───── */

    /**
     * @copydoc AuthRepository.signUpEmail
     * @details Implementación específica: Llama a `service.signUpAndVerify` (que se encarga
     * del registro y envío del correo de verificación) y luego invoca
     * [createProfileIfAbsent] pasando los datos adicionales (`userName`, `birthDate`)
     * para la creación inicial del documento de perfil en Firestore.
     * @note Aunque la interfaz [AuthRepository] documenta un retorno [FirebaseUser],
     * esta implementación actual no lo devuelve explícitamente.
     */

    // AuthService.kt
    override suspend fun signUpWithoutVerification(email: String, password: String): FirebaseUser {
        val auth = FirebaseAuth.getInstance()
        val userCredential = auth.createUserWithEmailAndPassword(email, password).await()
        val user = userCredential.user ?: throw Exception("Error al registrar usuario")

        // NO enviar correo de verificación automáticamente
        // user.sendEmailVerification() // ELIMINADO

        return user
    }

    override suspend fun signUpEmail(
        email    : String,
        pass     : String,
        userName : String,
        birthDate: LocalDate
    ) {
        val user = signUpWithoutVerification(email, pass) // Registra y pide verificación
        // Crea el perfil con los datos adicionales si no existe
        createProfileIfAbsent(user, userName, birthDate)

        val emailSent = sendVerificationEmailBackend(email)
        if (!emailSent) {
            throw Exception("Error enviando correo de verificación desde el backend")
        }
    }


    /* ─────────── Helper privado ─────────── */

    /**
     * @brief Crea el documento de perfil del usuario en Firestore si este no existe aún.
     * @details Realiza una lectura (`get`) para verificar la existencia del documento `profiles/{user.uid}`.
     * Si el documento no existe (`!docSnapshot.exists()`), procede a crearlo (`set`)
     * con los campos iniciales: `email`, `provider` (obtenido de `user.providerData`),
     * `createdAt` (usando `FieldValue.serverTimestamp()` para la hora del servidor),
     * `verified` (basado en `user.isEmailVerified`), y los campos opcionales `userName`
     * y `birthDate` (convertido a String ISO) si se proporcionaron.
     * La operación se ejecuta en el contexto del dispatcher IO.
     *
     * @param user       El objeto [FirebaseUser] que representa al usuario recién autenticado o creado.
     * @param userName   (Opcional) El nombre de usuario proporcionado durante el registro. Si es `null`, no se incluirá.
     * @param birthDate  (Opcional) La fecha de nacimiento proporcionada. Si es `null`, no se incluirá. Se almacena como String "YYYY-MM-DD".
     * @throws com.google.firebase.FirebaseException Si ocurre un error durante la lectura o escritura en Firestore.
     */
    private suspend fun createProfileIfAbsent(
        user      : FirebaseUser,
        userName  : String?    = null,
        birthDate : LocalDate? = null
    ): Unit = withContext(io) { // Asegura ejecución en IO dispatcher
        val profileDocRef = db.collection("profiles").document(user.uid) // Referencia al doc del perfil
        val profileSnapshot = profileDocRef.get().await() // Intenta leer el documento

        if (!profileSnapshot.exists()) { // Si el documento NO existe
            // Prepara los datos para el nuevo documento
            val initialProfileData = mapOf(
                "email"     to user.email, // Email del usuario
                // Obtiene el ID del proveedor (e.g., "password", "google.com")
                "provider"  to user.providerData.firstOrNull()?.providerId,
                // Usa timestamp del servidor para la fecha de creación
                "createdAt" to FieldValue.serverTimestamp(),
                // Guarda el estado de verificación actual
                "verified"  to false,
                // Incluye userName si no es nulo
                "userName"  to userName,
                // Convierte LocalDate a String ISO si no es nulo
                "birthDate" to birthDate?.toString()
            ).filterValues { it != null } // Filtra valores nulos para no escribirlos en Firestore

            // Crea el documento. Si falla, lanzará una excepción.
            profileDocRef.set(initialProfileData).await()
            Log.i("AuthRepo", "Documento de perfil creado para ${user.uid}")
        } else {
            // Opcional: Loguear que el perfil ya existía
            // Log.d("AuthRepo", "Documento de perfil ya existe para ${user.uid}")
        }
    }

    private suspend fun sendVerificationEmailBackend(email: String): Boolean = withContext(io) {
        try {
            val url = URL("https://tibiserver.onrender.com/send-confirmation") // Cambia a tu dominio en producción
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            // Cuerpo de la solicitud JSON
            val json = JSONObject().apply {
                put("email", email)
            }

            connection.outputStream.use {
                it.write(json.toString().toByteArray())
            }

            val responseCode = connection.responseCode
            val responseMessage = connection.inputStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            if (responseCode == 200) {
                Log.i("AuthRepo", "Correo de verificación enviado correctamente para $email")
                return@withContext true
            } else {
                Log.e("AuthRepo", "Error enviando correo de verificación: Código $responseCode - $responseMessage")
                throw Exception("Error enviando correo de verificación: Código $responseCode - $responseMessage")
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Error enviando correo de verificación al backend: ${e.message}", e)
            throw Exception("Error enviando correo de verificación al backend: ${e.message}")
        }
    }

}
