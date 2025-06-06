
plugins {
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

secrets {

}


android {
    namespace = "edu.cit.sarismart"
    compileSdk = 35

    defaultConfig {
        applicationId = "edu.cit.sarismart"
        minSdk = 27
        targetSdk = 34
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
    buildFeatures {
        compose = true
        buildConfig = true
        dataBinding = false
        viewBinding = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt + ViewModel
    implementation(libs.androidx.hilt.navigation.compose)

    // Jetpack Compose
    val composeBom = platform("androidx.compose:compose-bom:2025.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.androidx.foundation)

    // Material 3
    implementation(libs.androidx.material3)

    // Android UI
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Material Icons
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material)

    // Google Font
    implementation(libs.androidx.ui.text.google.fonts)

    // Animation
    implementation(libs.androidx.animation)

    // Lottie
    implementation(libs.lottie.compose)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Data Store
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)

    // Google Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)

    // Location Services
    implementation(libs.play.services.location)

    // Navigation
    implementation(libs.androidx.navigation.compose.v277)

    // Accompanist
    implementation(libs.accompanist.permissions)

    // Biometrics
    implementation(libs.androidx.biometric.ktx)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // OkHttp
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Camera
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Barcode Scanning
    implementation(libs.barcode.scanning)
}


kapt {
    correctErrorTypes = true
}






