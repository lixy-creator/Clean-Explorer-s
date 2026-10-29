plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
    kotlin("plugin.serialization")
}

//apply plugin: 'com.android.application'
//apply plugin: 'androidx.navigation.safeargs.kotlin'
//apply plugin: 'org.jetbrains.kotlin.plugin.serialization'

android {
    compileSdk = 34
    //namespace = "com.cinnamon.cleanexplorers"
    //  buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "blooom.cinnamon.cleanexplorers"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
        named("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
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

    implementation(Kotlin.stdlib)
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.appCompat)
    implementation(Google.android.material)
    implementation(AndroidX.constraintLayout)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.+")
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)

    //Navigation
    implementation(AndroidX.navigation.fragmentKtx)
    implementation(AndroidX.navigation.uiKtx)

    //Google Services
    implementation(Google.android.playServices.auth)
    implementation(Google.android.maps.utils)
    implementation(Google.android.maps.utils.ktx)
    implementation(Google.android.playServices.location)
    implementation(Google.android.playServices.maps)

    //Facebook
    implementation("com.facebook.android:facebook-android-sdk:9.0.0")
    implementation("com.facebook.android:facebook-login:9.0.0")

    //Preferences
    implementation(AndroidX.preference.ktx)


    // Lifecycle components
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.commonJava8)

    // Room components
    implementation(AndroidX.room.ktx)
    kapt(AndroidX.room.compiler)
    androidTestImplementation(AndroidX.room.testing)


    //API
    implementation(Square.retrofit2)
    implementation(KotlinX.serialization.json)
    implementation(JakeWharton.retrofit2.converter.kotlinxSerialization)
    implementation(Square.okHttp3.loggingInterceptor)


    // MLKit
    implementation(Google.mlKit.vision.barcodeScanning)

    //CameraX
    implementation(AndroidX.camera.camera2)
    implementation(AndroidX.camera.lifecycle)
    implementation(AndroidX.camera.view)

    //Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    //Lottie
    implementation("com.airbnb.android:lottie:3.4.0")

}