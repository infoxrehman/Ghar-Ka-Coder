plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.lixtanetwork.gharkacoder"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lixtanetwork.gharkacoder"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.android.gif.drawable)

    implementation(libs.lottie)

    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    implementation(libs.constraintlayout)
    implementation(libs.activity)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}