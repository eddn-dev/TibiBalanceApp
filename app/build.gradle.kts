// app/build.gradle.kts
@Suppress("DSL_SCOPE_VIOLATION") // habilita 'libs.' accessors del catálogo
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.app.tibibalance"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.tibibalance"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures { compose = true }
}

dependencies {
    // UI
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation)

    // Credential Manager + GIS
    implementation(libs.bundles.auth)

    // KotlinX
    implementation(libs.kotlinx.serialization.json)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Dependencias para habilitar la previsualización de @Preview en Android Studio
    implementation(libs.compose.ui.tooling.preview) // Contiene la anotación @Preview
    debugImplementation(libs.compose.ui.tooling)     // Necesario para renderizar la preview en el IDE (solo debug)

    // Core / tests
    implementation("androidx.core:core-ktx:1.13.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

