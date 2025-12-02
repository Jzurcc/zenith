plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.cc17.zenith"
    compileSdk = 36

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.cc17.zenith"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // camerax and ml kit for qr scanner
    implementation ("androidx.camera:camera-core:1.5.1")
    implementation ("androidx.camera:camera-camera2:1.5.1")
    implementation ("androidx.camera:camera-lifecycle:1.5.1")
    implementation ("androidx.camera:camera-view:1.5.1")
    implementation ("com.google.mlkit:barcode-scanning:17.3.0")

    // zxing library for qr code generator
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-ai")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.4")
    implementation("androidx.activity:activity-ktx:1.11.0")
    implementation("io.noties.markwon:core:4.6.2")


    // TODO: Add the dependencies for Firebase products you want to use
    // https://firebase.google.com/docs/android/setup#available-libraries

}