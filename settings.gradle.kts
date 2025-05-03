// settings.gradle.kts  ────────────────────────────────────────────────
pluginManagement {
    repositories {
        // Plugins publicados por Google (AGP, Hilt, Google-Services, Compose, etc.)
        google()
        // Plugins de JetBrains, Dagger y demás (si Google no los aloja)
        mavenCentral()
        // Portal público (plugins comunitarios)
        gradlePluginPortal()
    }

    // ➊  Declara aquí TODAS las versiones de *plugins* que necesitas
    plugins {
        id("com.android.application")            version "8.9.2"
        id("org.jetbrains.kotlin.android")       version "2.0.21"
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
        id("com.google.dagger.hilt.android")     version "2.57.2" // disponible
        id("com.google.gms.google-services")     version "4.4.0"
    }
}

dependencyResolutionManagement {
    // ➋  Evita que cada módulo declare repositorios propios
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

/* ➍  Módulos que conforman tu proyecto */
rootProject.name = "TibiBalance"
include(":app")
