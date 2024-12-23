plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.cnn"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cnn"
        minSdk = 24
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        mlModelBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // CameraX core libraries
    implementation("androidx.camera:camera-core:1.5.0")
    implementation("androidx.camera:camera-camera2:1.5.0")
    implementation("androidx.camera:camera-lifecycle:1.5.0")

    // Optional: CameraX View (for Camera Preview UI)
    implementation("androidx.camera:camera-view:1.5.0")

    // Optional: CameraX Extensions (for advanced features like HDR, Night Mode)
    implementation("androidx.camera:camera-extensions:1.5.0")

    // TensorFlow Lite dependencies
    implementation("org.tensorflow:tensorflow-lite:2.15.0") // Use the latest stable version
    implementation("org.tensorflow:tensorflow-lite-support:2.15.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.15.0")

    // Optional: TensorFlow Lite Metadata
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")

    // Optional: TensorFlow Lite for buffer operations
    implementation("org.tensorflow:tensorflow-lite-support:0.1.0-rc1")

    // Testing dependencies
    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    // Additional dependencies from libs (ensure these are correctly defined in your `libs.versions.toml`)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.litert.support.api)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
