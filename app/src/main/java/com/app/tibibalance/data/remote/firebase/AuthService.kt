// data/remote/firebase/AuthService.kt
package com.app.tibibalance.data.remote.firebase

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * @file    AuthService.kt
 * @ingroup data_remote
 * @brief   Fuente de datos que envuelve **FirebaseAuth**.
 *
 * Este contrato define las operaciones de autenticación que
 * el repositorio implementará usando el SDK de Firebase.
 * Devuelve el [FirebaseUser] resultante para que la capa
 * de datos pueda crear o actualizar el perfil en Firestore.
 */

/**
 * @brief Interface de servicio de autenticación.
 *
 * Expone un flujo reactivo del estado de sesión y métodos para:
 * - Iniciar / registrar con correo-contraseña.
 * - Enviar correos de verificación y restablecer contraseña.
 * - Iniciar sesión con Google One-Tap.
 * - Cerrar sesión.
 */
interface AuthService {

    /* ─────────────── Sesión ─────────────── */

    /** @brief Flujo que emite `true` cuando hay usuario logueado. */
    val authState: Flow<Boolean>

    /* ───────── Correo / contraseña ───────── */

    /**
     * @brief Inicia sesión con correo y contraseña.
     * @param email Correo electrónico del usuario.
     * @param pass  Contraseña.
     * @return      [FirebaseUser] autenticado.
     */
    suspend fun signIn(email: String, pass: String): FirebaseUser

    /**
     * @brief Registra usuario sin verificación de e-mail.
     * @param email Correo.
     * @param pass  Contraseña.
     * @return      [FirebaseUser] creado.
     */
    suspend fun signUp(email: String, pass: String): FirebaseUser

    /**
     * @brief Registra usuario y envía e-mail de verificación.
     * @param email Correo.
     * @param pass  Contraseña.
     * @return      [FirebaseUser] creado.
     */
    suspend fun signUpAndVerify(email: String, pass: String): FirebaseUser

    /**
     * @brief Envía correo para restablecer la contraseña.
     * @param email Correo del usuario.
     */
    suspend fun sendPasswordReset(email: String)

    /* ─────────── Google One-Tap ──────────── */

    /**
     * @brief Inicia sesión con credencial de Google.
     * @param idToken Token JWT obtenido de One-Tap.
     * @return        [FirebaseUser] autenticado.
     */
    suspend fun signInGoogle(idToken: String): FirebaseUser

    /* ───────────── Cerrar sesión ──────────── */

    /** @brief Cierra la sesión del usuario actual. */
    suspend fun signOut()
}
