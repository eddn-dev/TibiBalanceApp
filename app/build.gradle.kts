import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

// app/build.gradle.kts
@Suppress("DSL_SCOPE_VIOLATION")      // habilita 'libs.' accessors del catálogo
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace   = "com.app.tibibalance"
    compileSdk  = 35            // OK: Android 15 Preview (ajusta a 34 si tu CI usa solo estables)
    defaultConfig {
        applicationId         = "com.app.tibibalance"
        minSdk                = 26
        targetSdk             = 35
        versionCode           = 1
        versionName           = "1.0"
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
    buildFeatures  { compose = true }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}


dependencies {

    /* ──────────────── UI (Compose) ──────────────── */
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)            // Material-3, Nav, Icons, Activity
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation("androidx.compose.foundation:foundation")   // versión la pone tu compose-bom


    /* ──────────────── Persistencia local (Room) ──────────────── */
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    /* ──────────────── Firebase (BoM 33.13.0 definido en Toml) ──────────────── */
    implementation(platform(libs.firebase.bom))      // ► controla versiones de todos los artefactos
    implementation("com.google.firebase:firebase-analytics-ktx")   // Analytics
    implementation(libs.firebase.auth.ktx)           // Auth (KTX)
    implementation("com.google.firebase:firebase-firestore-ktx")   // Firestore (KTX)

    /* ──────────────── Inyección de dependencias (Hilt) ──────────────── */
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation)

    /* ──────────────── Google Identity / Credential Manager ──────────────── */
    implementation(libs.bundles.auth)               // credential-core, credentials-play, GIS

    /* ──────────────── KotlinX ──────────────── */
    implementation(libs.kotlinx.serialization.json)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    /* ──────────────── Core & Tests ──────────────── */
    implementation("androidx.core:core-ktx:1.13.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(libs.kotlinx.datetime)

    /* ──────────────── Animations  ────────────────*/
    implementation ("com.airbnb.android:lottie-compose:6.6.4")
    implementation ("com.github.commandiron:SpinWheelCompose:1.1.1")
}

allprojects {
    repositories {
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/*") }
    }
}