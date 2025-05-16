/**
 * @file    AuthRepository.kt
 * @ingroup data_repository
 * @brief   Contrato de alto nivel para la gestión de la autenticación de usuarios.
 *
 * @details Esta interfaz define las operaciones estándar para manejar la autenticación
 * en la aplicación, abstrayendo la implementación específica del proveedor
 * (actualmente Firebase Authentication a través de [com.app.tibibalance.data.repository.FirebaseAuthRepository]).
 * Las capas superiores (ViewModels, UseCases) deben depender de esta interfaz
 * para mantener el desacoplamiento y facilitar las pruebas.
 *
 * Gestiona las siguientes responsabilidades:
 * - Monitorizar el estado de la sesión de usuario de forma reactiva.
 * - Proveer acceso al objeto [FirebaseUser] actual.
 * - Iniciar sesión y registrar usuarios mediante correo/contraseña y Google Sign-In (One-Tap).
 * - Gestionar el flujo de restablecimiento de contraseña.
 * - Sincronizar y verificar el estado del correo electrónico del usuario.
 * - Registrar usuarios proporcionando datos adicionales para el perfil inicial.
 * - Cerrar la sesión del usuario actual.
 */
package com.app.tibibalance.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * @brief Interfaz que define el contrato para el repositorio de autenticación.
 * @see com.app.tibibalance.data.repository.FirebaseAuthRepository Implementación concreta que utiliza Firebase.
 */
interface AuthRepository {

    /**
     * @brief Flujo reactivo que indica si hay un usuario actualmente autenticado.
     * @return [Flow] que emite `true` si existe una sesión activa, `false` en caso contrario.
     * Se actualiza automáticamente ante cambios en el estado de autenticación.
     */
    val isLoggedIn: Flow<Boolean>

    /**
     * @brief Proporciona acceso síncrono al objeto [FirebaseUser] del usuario actual.
     * @return La instancia de [FirebaseUser] si hay una sesión activa, o `null` si no hay ningún usuario autenticado.
     */
    val currentUser: FirebaseUser?

    /* ─────────── Autenticación con Correo / Contraseña ─────────── */

    /**
     * @brief Inicia sesión utilizando las credenciales de correo electrónico y contraseña.
     * @param email El correo electrónico del usuario.
     * @param pass  La contraseña del usuario.
     * @return El objeto [FirebaseUser] del usuario autenticado. // <-- Añadido (basado en implementación)
     * @throws com.google.firebase.auth.FirebaseAuthException Si las credenciales son inválidas, la cuenta no existe, está deshabilitada, o hay otros errores de autenticación.
     * @throws com.google.firebase.FirebaseNetworkException Si ocurre un error de red durante la comunicación.
     * @throws Exception Para otros errores inesperados.
     */
    suspend fun signIn(email: String, pass: String) // La implementación devuelve FirebaseUser

    /**
     * @brief Registra un nuevo usuario con correo electrónico y contraseña.
     * @details Esta versión básica no envía correo de verificación. Para ello, usar [signUpEmail].
     * @param email El correo electrónico para la nueva cuenta.
     * @param pass  La contraseña para la nueva cuenta (debe cumplir los requisitos de Firebase).
     * @return El objeto [FirebaseUser] recién creado. // <-- Añadido (basado en implementación)
     * @throws com.google.firebase.auth.FirebaseAuthUserCollisionException Si el correo ya está registrado.
     * @throws com.google.firebase.auth.FirebaseAuthWeakPasswordException Si la contraseña no cumple los requisitos de seguridad.
     * @throws com.google.firebase.auth.FirebaseAuthInvalidCredentialsException Si el formato del correo es inválido.
     * @throws com.google.firebase.FirebaseNetworkException Si ocurre un error de red.
     * @throws Exception Para otros errores inesperados.
     */
    suspend fun signUp(email: String, pass: String) // La implementación devuelve FirebaseUser

    suspend fun signUpWithoutVerification(email: String, password: String, ): FirebaseUser
    /**
     * @brief Envía un correo electrónico al usuario especificado para que pueda restablecer su contraseña.
     * @param email El correo electrónico asociado a la cuenta.
     * @throws com.google.firebase.auth.FirebaseAuthException Si el correo no está registrado o hay problemas al enviar el email.
     * @throws com.google.firebase.FirebaseNetworkException Si ocurre un error de red.
     * @throws Exception Para otros errores inesperados.
     */
    suspend fun resetPass(email: String)

    /* ───────────── Autenticación con Google Sign-In / One-Tap ───────────── */

    /**
     * @brief Inicia sesión o registra un usuario utilizando una credencial de Google (ID Token).
     * @details Típicamente se usa con el resultado de Google Sign-In o Google One-Tap.
     * @param idToken El ID Token JWT obtenido del proveedor de identidad de Google.
     * @return El objeto [FirebaseUser] del usuario autenticado (puede ser nuevo o existente). // <-- Añadido (basado en implementación)
     * @throws com.google.firebase.auth.FirebaseAuthException Si el token es inválido, caducado, o hay problemas al vincular/autenticar con Firebase.
     * @throws com.google.firebase.FirebaseNetworkException Si ocurre un error de red.
     * @throws Exception Para otros errores inesperados.
     */
    suspend fun signInGoogle(idToken: String) // La implementación devuelve FirebaseUser

    /* ─────────── Verificación de Correo Electrónico ─────────── */

    /**
     * @brief Comprueba y actualiza el estado de verificación del correo electrónico del usuario actual.
     * @details Primero fuerza una recarga del estado del usuario desde Firebase (`reload()`) y luego
     * verifica la propiedad `isEmailVerified`. Si está verificada, asegura que el estado
     * correspondiente en el perfil de Firestore también esté actualizado.
     * @return `true` si el correo del usuario actual está verificado (después de la recarga), `false` en caso contrario o si no hay usuario.
     * @throws com.google.firebase.FirebaseNetworkException Si ocurre un error de red durante la recarga o la actualización de Firestore.
     * @throws Exception Para otros errores inesperados.
     */
    suspend fun syncVerification(): Boolean

    /* ───────── Registro con Datos Adicionales de Perfil ───────── */

    /**
     * @brief Registra un nuevo usuario con correo/contraseña, envía correo de verificación y crea su documento de perfil inicial en Firestore.
     *
     * @param email     El correo electrónico para la nueva cuenta.
     * @param pass      La contraseña para la nueva cuenta.
     * @param userName  El nombre visible inicial para el perfil del usuario.
     * @param birthDate La fecha de nacimiento del usuario.
     * @return El objeto [FirebaseUser] recién creado y verificado (inicio del proceso). // <-- Añadido (basado en implementación)
     * @throws com.google.firebase.auth.FirebaseAuthException Si ocurren errores durante el registro (correo existente, contraseña débil, etc.) o el envío del correo de verificación.
     * @throws com.google.firebase.FirebaseNetworkException Si ocurre un error de red.
     * @throws Exception Si ocurre un error al crear el documento de perfil en Firestore o por otras causas.
     */
    suspend fun signUpEmail(
        email: String,
        pass : String,
        userName: String,
        birthDate: LocalDate
    ) // La implementación devuelve FirebaseUser

    /* ───────────── Cerrar Sesión ───────────── */

    /**
     * @brief Cierra la sesión del usuario actualmente autenticado en Firebase.
     * @throws Exception Potencialmente podría lanzar excepciones si hay problemas con el estado interno de Firebase, aunque es raro.
     */
    suspend fun signOut()
}
