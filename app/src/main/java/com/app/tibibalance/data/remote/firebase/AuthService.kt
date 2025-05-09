/**
 * @file    AuthService.kt
 * @ingroup data_remote_service // Grupo específico para servicios remotos
 * @brief   Define el contrato para el servicio de autenticación basado en Firebase.
 *
 * @details Esta interfaz abstrae las operaciones directas con el SDK de Firebase Authentication,
 * permitiendo que los repositorios dependan de este contrato en lugar de la implementación
 * concreta ([FirebaseAuthService]). Define los métodos esenciales para el manejo de
 * sesiones de usuario, incluyendo registro, inicio de sesión (con correo/contraseña y Google),
 * recuperación de contraseña y cierre de sesión.
 *
 * La mayoría de las operaciones devuelven el [FirebaseUser] resultante para permitir
 * que la capa de datos (repositorio) realice acciones adicionales, como crear o
 * actualizar el perfil del usuario en Firestore tras una autenticación exitosa.
 */
package com.app.tibibalance.data.remote.firebase

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * @brief Interfaz que define las operaciones del servicio de autenticación.
 * @see FirebaseAuthService Implementación concreta que utiliza Firebase Authentication.
 * @see com.app.tibibalance.data.repository.AuthRepository Repositorio que utiliza este servicio.
 */
interface AuthService {

    /* ─────────────── Estado de Sesión ─────────────── */

    /**
     * @brief Flujo reactivo que emite el estado actual de la sesión.
     * @return [Flow] que emite `true` si hay un usuario autenticado, `false` en caso contrario.
     * Se actualiza automáticamente cuando el estado de autenticación cambia.
     */
    val authState: Flow<Boolean>

    /* ───────── Autenticación con Correo / Contraseña ───────── */

    /**
     * @brief Inicia sesión utilizando correo electrónico y contraseña.
     * @param email El correo electrónico del usuario.
     * @param pass  La contraseña del usuario.
     * @return El objeto [FirebaseUser] correspondiente al usuario autenticado.
     * @throws com.google.firebase.auth.FirebaseAuthException Si las credenciales son inválidas,
     * la cuenta no existe, o hay otros errores de autenticación.
     */
    suspend fun signIn(email: String, pass: String): FirebaseUser

    /**
     * @brief Registra un nuevo usuario utilizando correo electrónico y contraseña.
     * @details No envía correo de verificación automáticamente.
     * @param email El correo electrónico para la nueva cuenta.
     * @param pass  La contraseña para la nueva cuenta.
     * @return El objeto [FirebaseUser] recién creado.
     * @throws com.google.firebase.auth.FirebaseAuthException Si el correo ya está en uso,
     * la contraseña es débil, o hay otros errores de registro.
     */
    suspend fun signUp(email: String, pass: String): FirebaseUser

    /**
     * @brief Registra un nuevo usuario y envía automáticamente un correo de verificación.
     * @param email El correo electrónico para la nueva cuenta.
     * @param pass  La contraseña para la nueva cuenta.
     * @return El objeto [FirebaseUser] recién creado, después de solicitar el envío del correo de verificación.
     * @throws com.google.firebase.auth.FirebaseAuthException Si ocurren errores durante el registro o el envío del correo.
     */
    suspend fun signUpAndVerify(email: String, pass: String): FirebaseUser

    /**
     * @brief Envía un correo electrónico al usuario para restablecer su contraseña.
     * @param email El correo electrónico asociado a la cuenta cuya contraseña se desea restablecer.
     * @throws com.google.firebase.auth.FirebaseAuthException Si el correo no está registrado o hay otros errores.
     */
    suspend fun sendPasswordReset(email: String)

    /* ─────────── Autenticación con Google (One-Tap / GIS) ──────────── */

    /**
     * @brief Inicia sesión o registra un usuario utilizando una credencial de Google obtenida vía One-Tap.
     * @param idToken El ID Token JWT proporcionado por Google Identity Services después de que el usuario selecciona una cuenta.
     * @return El objeto [FirebaseUser] autenticado (puede ser un usuario nuevo o existente).
     * @throws com.google.firebase.auth.FirebaseAuthException Si el token es inválido o hay problemas al vincular con Firebase.
     */
    suspend fun signInGoogle(idToken: String): FirebaseUser

    /* ───────────── Cerrar Sesión ──────────── */

    /**
     * @brief Cierra la sesión del usuario actualmente autenticado en el dispositivo.
     */
    suspend fun signOut()
}
