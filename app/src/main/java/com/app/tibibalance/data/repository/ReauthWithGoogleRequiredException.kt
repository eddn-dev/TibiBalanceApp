// app/src/main/java/com/app/tibibalance/data/repository/ReauthWithGoogleRequiredException.kt
package com.app.tibibalance.data.repository

/**
 * Lanzada cuando una cuenta Google necesita re-autenticarse
 * mediante el flujo interactivo de Google Sign-In.
 */
class ReauthWithGoogleRequiredException : Exception("GOOGLE_REAUTH_REQUIRED")
