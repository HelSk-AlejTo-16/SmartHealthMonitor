plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "mx.utng.latp.wear"
    compileSdk = 35

    defaultConfig {
        // El wear debe tener un applicationId DISTINTO al del teléfono.
        // AGP 8.x no permite que dos módulos :app compartan el mismo applicationId
        // a menos que estén configurados como base + dynamic-feature (que no es el caso).
        // La comunicación Wearable sigue funcionando vía Bluetooth pairing + capability
        // discovery (node-based), independientemente del applicationId.
        applicationId = "mx.utng.latp.smarthealthmonitor.wear"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling)
    implementation(libs.play.services.wearable)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
    // Health Services API
    implementation("androidx.health:health-services-client:1.1.0-alpha03")
    // Coroutines await() para Guava ListenableFuture y Tasks
    implementation("com.google.guava:guava:33.0.0-android")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")


    // Compose for Wear OS
    implementation("androidx.wear.compose:compose-material:1.3.1")
    implementation("androidx.wear.compose:compose-foundation:1.3.1")
    implementation("androidx.wear.compose:compose-navigation:1.3.1")
    // Horologist (utilidades Wear OS de Google)
    implementation("com.google.android.horologist:horologist-compose-layout:0.6.17")
    implementation("com.google.android.horologist:horologist-compose-material:0.6.17")
    // ELIMINADO: implementation(project(":app"))
    // Un módulo :application NO puede depender de otro módulo :application.
    // AGP 8.x lo detecta como dynamic features inválido → error de build.
    // La comunicación wear↔phone se hace vía Wearable Data Layer API,
    // no mediante importación directa de clases entre módulos.

}