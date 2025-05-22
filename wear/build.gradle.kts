// wear/build.gradle.kts
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Google Services para procesar google-services.json
    alias(libs.plugins.google.services)

    // Hilt
    alias(libs.plugins.hilt)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace   = "com.app.wear"
    compileSdk  = 35

    defaultConfig {
        applicationId    = "com.app.wear"
        minSdk           = 24
        targetSdk        = 35
        versionCode      = 1
        versionName      = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        // Ajusta al versión de tu Compose Compiler
        kotlinCompilerExtensionVersion = "1.4.8"
    }
}

dependencies {
    // Core & Lifecycle
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Compose UI
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.material3)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // Firebase BoM + Firestore & Auth
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // Hilt DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)

    // Wearable Data Layer
    implementation ("com.google.android.gms:play-services-wearable:19.0.0")

    // Kotlinx Serialization JSON
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Si tu modelo DailyMetrics está en el módulo app, agrega también:
    implementation (project(":app"))
}
