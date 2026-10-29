import java.util.Properties


plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
    kotlin("plugin.serialization")
    id("androidx.navigation.safeargs.kotlin")
}

// Secrets live in local.properties / keystore.properties (both ignored by git)
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val mapsApiKey: String = localProperties.getProperty("MAPS_API_KEY")
    ?: System.getenv("MAPS_API_KEY")
    ?: "".also { logger.warn("MAPS_API_KEY missing: add it to local.properties (see local.properties.example)") }

val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

//apply plugin: 'com.android.application'
//apply plugin: 'androidx.navigation.safeargs.kotlin'
//apply plugin: 'org.jetbrains.kotlin.plugin.serialization'

android {
    namespace = App.appNamespace
    compileSdk = App.compileSdk

    defaultConfig {
        applicationId = App.appApplicationId
        minSdk = App.minSdk
        versionCode = App.appVersionCode
        versionName = App.appVersionName

        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey

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

    signingConfigs {
        if (!keystoreProperties.isEmpty) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfigs.findByName("release")?.let { signingConfig = it }
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
        named("debug") {
            versionNameSuffix = App.getDebugVersionNameSuffix(project)
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

kapt {
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
        arg("room.expandProjection", "true")
    }
}

dependencies {

    implementation(Kotlin.stdlib)
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.appCompat)
    implementation(Google.android.material)
    implementation(AndroidX.constraintLayout)
    implementation(Libs.legacy_support_v4)
    testImplementation(Testing.junit4)
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
    implementation(Libs.facebook_sdk)
    implementation(Libs.facebook_login)

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
    implementation(Libs.glide)
    kapt(Libs.glide_compiler)

    //Lottie
    implementation(Libs.lottie)

}