plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

import java.util.Properties

val localProps = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProps.load(localPropertiesFile.inputStream())
}

android {
    namespace = "mx.utng.latp.wear"
    compileSdk = 35

    defaultConfig {
        applicationId = "mx.utng.latp.smarthealthmonitor.wear"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        
        buildConfigField("String", "NEON_API_KEY", "\"${localProps["NEON_API_KEY"] ?: ""}\"")
        buildConfigField("String", "NEON_HOST", "\"${localProps["NEON_HOST"] ?: ""}\"")
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
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
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
    implementation("androidx.compose.material:material-icons-extended")
    // ViewModel + viewModelScope para WearDashboardViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")



    // Compose for Wear OS
    implementation("androidx.wear.compose:compose-material:1.3.1")
    implementation("androidx.wear.compose:compose-foundation:1.3.1")
    implementation("androidx.wear.compose:compose-navigation:1.3.1")
    // Jetpack WatchFace API
    implementation("androidx.wear.watchface:watchface:1.2.1")
    implementation("androidx.wear.watchface:watchface-complications-rendering:1.2.1")
    implementation("androidx.wear.watchface:watchface-style:1.2.1")

    // Horologist (utilidades Wear OS de Google)
    implementation("com.google.android.horologist:horologist-compose-layout:0.6.17")
    implementation("com.google.android.horologist:horologist-compose-material:0.6.17")
    
    // Eclipse Paho MQTT para Android
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")
    // Kotlinx Serialization para JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    
    // Retrofit + OkHttp para llamadas a Neon HTTP API
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}
