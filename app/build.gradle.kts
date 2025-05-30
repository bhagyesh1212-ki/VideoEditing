plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.one.videoeditingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.one.videoeditingapp"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }

}

val camerax_version = "1.4.1"

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    implementation("androidx.camera:camera-core:$camerax_version")
//    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")
//    implementation("androidx.camera:camera-video:$camerax_version")
    implementation(project(":lib"))
    implementation(project(":AXVideoTimelineView"))
    implementation(project(":animators"))
    implementation(project(":library"))
    implementation(project(":cameravideobuttonlib"))
    implementation(project(":instagramvideobutton"))
    implementation("com.github.MasayukiSuda:GPUVideo-android:v0.1.2")
    implementation("jp.co.cyberagent.android:gpuimage:2.1.0")
    implementation("com.github.lincollincol:amplituda:2.3.0")
}