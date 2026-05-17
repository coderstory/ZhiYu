plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.zhiyu.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.zhiyu.app"
        minSdk = 36
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    aaptOptions {
        // Allow dependencies targeting higher compileSdk
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose UI Core
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.foundation)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Material3 + Icons
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)

    // Navigation
    implementation(libs.navigation.compose)

    // Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Preferences
    implementation(libs.datastore.preferences)

    // Splash Screen
    implementation(libs.core.splashscreen)

    // Koin DI
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)


    // MIUIX
    implementation(libs.miuix.ui)
    implementation(libs.miuix.icons)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Markdown (compose-richtext)
    implementation(libs.richtext.ui)
    implementation(libs.richtext.commonmark)
    implementation(libs.richtext.ui.material3)

    // Testing
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.room.testing)
    androidTestImplementation(libs.compose.ui.test.junit4)
}
