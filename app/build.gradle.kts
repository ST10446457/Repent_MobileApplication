// REPENT† — Android Application
// Firebase Authentication + Cloud Firestore
// Kotlin 2.1.0 + Jetpack Compose

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.repent.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.repent.app"

        minSdk = 24
        targetSdk = 34

        versionCode = 1
        versionName = "1.0"

        // Local XAMPP website address.
        // Change the IP address when testing on your local network.
        buildConfigField(
            "String",
            "BASE_URL",
            "\"http://10.0.0.4/Repent_Website-main/\""
        )
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
    }

    // Compose Compiler is managed by the Kotlin Compose plugin.

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // =====================================================
    // FIREBASE AUTHENTICATION + CLOUD FIRESTORE
    // =====================================================

    // Firebase BoM 33.16.0:
    // Firebase Authentication 23.2.1
    // Cloud Firestore 25.1.4

    implementation(
        platform("com.google.firebase:firebase-bom:33.16.0")
    )

    implementation("com.google.firebase:firebase-auth")

    implementation("com.google.firebase:firebase-firestore")


    // =====================================================
    // COROUTINES + FIREBASE TASKS
    // =====================================================

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1"
    )

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1"
    )


    // =====================================================
    // GOOGLE SIGN-IN
    // =====================================================

    implementation(
        "androidx.credentials:credentials:1.3.0"
    )

    implementation(
        "androidx.credentials:credentials-play-services-auth:1.3.0"
    )

    implementation(
        "com.google.android.libraries.identity.googleid:googleid:1.1.1"
    )


    // =====================================================
    // JETPACK COMPOSE
    // =====================================================

    val composeBom = platform(
        "androidx.compose:compose-bom:2024.06.00"
    )

    implementation(composeBom)

    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")

    implementation("androidx.compose.ui:ui-graphics")

    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.compose.material3:material3")

    implementation(
        "androidx.compose.material:material-icons-extended"
    )


    // =====================================================
    // ANDROID CORE + LIFECYCLE
    // =====================================================

    implementation(
        "androidx.core:core-ktx:1.13.1"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.8.4"
    )

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4"
    )

    implementation(
        "androidx.activity:activity-compose:1.9.1"
    )


    // =====================================================
    // NAVIGATION
    // =====================================================

    implementation(
        "androidx.navigation:navigation-compose:2.7.7"
    )


    // =====================================================
    // NETWORKING
    // =====================================================

    implementation(
        "com.squareup.retrofit2:retrofit:2.11.0"
    )

    implementation(
        "com.squareup.retrofit2:converter-gson:2.11.0"
    )

    implementation(
        "com.squareup.okhttp3:logging-interceptor:4.12.0"
    )


    // =====================================================
    // LOCAL DATA STORAGE
    // =====================================================

    implementation(
        "androidx.datastore:datastore-preferences:1.1.1"
    )


    // =====================================================
    // IMAGE LOADING
    // =====================================================

    implementation(
        "io.coil-kt:coil-compose:2.6.0"
    )


    // =====================================================
    // DEBUGGING
    // =====================================================

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )

    debugImplementation(
        "androidx.compose.ui:ui-test-manifest"
    )


    // =====================================================
    // UNIT TESTING
    // =====================================================

    testImplementation(
        "junit:junit:4.13.2"
    )


    // =====================================================
    // ANDROID INSTRUMENTATION TESTING
    // =====================================================

    androidTestImplementation(
        "androidx.test.ext:junit:1.2.1"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.6.1"
    )
}