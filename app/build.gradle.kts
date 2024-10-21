plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.sinergi5.kliksewa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sinergi5.kliksewa"
        minSdk = 28
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

//    Firease
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

//    Viewmodel Lifecycle
    implementation (libs.androidx.lifecycle.viewmodel.ktx)

//    Image Auto Slider
    implementation (libs.imageslideshow)

//    Glide
    implementation (libs.glide)
    implementation (libs.androidx.swiperefreshlayout)
    implementation (libs.glidetovectoryou)


//    Shimmer effect
    implementation (libs.shimmer)

//    Kebutuhan untuk pemanggilan viewModel
    implementation (libs.androidx.fragment.ktx) // Versi sesuai kebutuhan
    implementation (libs.androidx.activity.ktx) // Versi sesuai kebutuhan



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}