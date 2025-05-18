// build.gradle.kts (proyecto raíz)

@Suppress("DSL_SCOPE_VIOLATION") // ← permite usar libs.plugins.*
plugins {
    // --- Plugins de Android/Kotlin ---
    alias(libs.plugins.android.application) apply false   // AGP 8.4.0 :contentReference[oaicite:0]{index=0}
    alias(libs.plugins.kotlin.android)       apply false   // Kotlin 2.1.20
    alias(libs.plugins.kotlin.compose)       apply false
    alias(libs.plugins.kotlin.serialization) apply false

    // --- Servicios de Google & DI ---
    alias(libs.plugins.google.services) apply false        // google-services 4.4.2 :contentReference[oaicite:1]{index=1}
    alias(libs.plugins.hilt)            apply false        // Hilt 2.56.2 :contentReference[oaicite:2]{index=2}
    alias(libs.plugins.ksp)
}
