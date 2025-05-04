// app/build.gradle.kts
@Suppress("DSL_SCOPE_VIOLATION") // habilita 'libs.' accessors del catálogo
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    id("org.jetbrains.kotlin.kapt")
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

    /* ──────────────── UI (Compose) ──────────────── */
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)                 // Material-3, Nav, Icons, Activity
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.accompanist.navanim)


    /* ──────────────── Firebase ──────────────── */
    implementation(platform(libs.firebase.bom))           // BOM v34.1.0
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation(libs.firebase.auth.ktx)                // ← FirebaseAuth & GoogleAuthProvider (KTX)
    implementation("com.google.firebase:firebase-firestore-ktx")

    /* ──────────────── Inyección de dependencias (Hilt) ──────────────── */
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)                              // Hilt requiere KAPT, no KSP
    implementation(libs.hilt.navigation)

    /* ──────────────── Google Identity / Credential Manager ──────────────── */
    implementation(libs.bundles.auth)                    // credential-core, GIS googleid, play-auth

    /* ──────────────── KotlinX ──────────────── */
    implementation(libs.kotlinx.serialization.json)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    /* ──────────────── Core & tests ──────────────── */
    implementation("androidx.core:core-ktx:1.13.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

