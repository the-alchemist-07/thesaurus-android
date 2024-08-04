plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.gms.googleServices)
    alias(libs.plugins.firebase.crashlytics.gradle)
    alias(libs.plugins.room.gradle)
}

android {
    namespace = "com.mashood.thesaurus"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mashood.thesaurus"
        minSdk = 21
        targetSdk = 34
        versionCode = 11
        versionName = "2.3.0"

        room {
            schemaDirectory("$projectDir/schemas")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
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
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Lifecycle + ViewModel & LiveData
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)

    // Navigation Component
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // SplashScreen API
    implementation(libs.splashscreen)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.annotation)

    // Network Interceptor
    implementation(libs.okhttp.interceptor)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi.converter)

    // Moshi - JSON Parser
    implementation(libs.moshi)
    ksp(libs.moshi.annotation)

    // Sandwich - Network Handler
    implementation(libs.sandwich)

    // Room
    implementation(libs.andriodx.room.runtime)
    implementation(libs.andriodx.room.ktx)
    ksp(libs.andriodx.room.compiler)

    // Lottie
    implementation(libs.lottie)

    // In-app updates
    implementation(libs.app.update)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.database)

//    implementation 'androidx.legacy:legacy-support-v4:1.0.0'  // TODO: Add this
//    implementation "androidx.fragment:fragment-ktx:1.5.5"  // TODO: Add this
}
