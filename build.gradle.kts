// Top-level build file (build.gradle.kts)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false // O la versión que uses

    // --- ASEGÚRATE DE TENER ESTOS DOS ---
    id("com.google.dagger.hilt.android") version "2.56.1" apply false // Usa la última versión estable
    id("com.google.devtools.ksp") version "2.1.20-2.0.0" apply false // Usa versión KSP compatible con tu Kotlin
    // --- FIN DE LÍNEAS CLAVE ---
}