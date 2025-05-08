// data/repository/AuthRepository.kt
package com.app.tibibalance.data.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * @file    AuthRepository.kt
 * @ingroup data_repository
 * @brief   Contrato de alto nivel para la autenticación de usuarios.
 *
 * Encapsula toda interacción con el proveedor externo (Firebase Auth),
 * de modo que las capas superiores (ViewModels / Use-cases) dependan
 * sólo de esta interfaz y no del SDK concreto.
 * Gestiona:
 * - Monitorizar el estado de sesión.
 * - Iniciar/cerrar sesión con correo-contraseña o Google.
 * - Flujo de restablecimiento de contraseña.
 * - Sincronizar/verificar el correo electrónico.
 * - Registro con datos de perfil adicionales (nombre, fecha de nacimiento).
 */
interface AuthRepository {

    /** @brief Flujo que emite `true` cuando hay usuario autenticado. */
    val isLoggedIn: Flow<Boolean>

    /** @brief Usuario actual proporcionado por Firebase, o `null` si no hay sesión. */
    val currentUser: FirebaseUser?

    /* ─────────── Correo / contraseña ─────────── */

    /**
     * @brief Inicia sesión con correo y contraseña.
     * @param email Correo electrónico.
     * @param pass  Contraseña.
     */
    suspend fun signIn(email: String, pass: String)

    /**
     * @brief Registra un nuevo usuario (sin verificación de e-mail).
     * @param email Correo electrónico.
     * @param pass  Contraseña.
     */
    suspend fun signUp(email: String, pass: String)

    /**
     * @brief Envía correo para restablecer la contraseña.
     * @param email Correo electrónico del usuario.
     */
    suspend fun resetPass(email: String)

    /* ───────────── Google Sign-In ───────────── */

    /**
     * @brief Inicia sesión con Google One-Tap.
     * @param idToken *ID Token* JWT proporcionado por Google Identity Services.
     */
    suspend fun signInGoogle(idToken: String)

    /* ─────────── Verificación e-mail ─────────── */

    /**
     * @brief Sincroniza el estado de verificación del correo.
     * @return `true` si el correo ha sido verificado.
     */
    suspend fun syncVerification(): Boolean

    /* ───────── Registro con datos extra ───────── */

    /**
     * @brief Registra un usuario y crea su documento de perfil.
     *
     * @param email     Correo electrónico.
     * @param pass      Contraseña.
     * @param userName  Nombre visible.
     * @param birthDate Fecha de nacimiento.
     */
    suspend fun signUpEmail(
        email: String,
        pass : String,
        userName: String,
        birthDate: LocalDate
    )

    /* ───────────── Cerrar sesión ───────────── */

    /** @brief Cierra la sesión del usuario actual. */
    suspend fun signOut()
}
