// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(Android.tools.build.gradlePlugin)
        classpath(Kotlin.gradlePlugin)
        classpath(AndroidX.navigation.safeArgsGradlePlugin)
        classpath("org.jetbrains.kotlin:kotlin-serialization:_")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://jitpack.io")
    }
}

tasks.register<Delete>("clean").configure {
    delete(rootProject.layout.buildDirectory)
    delete(rootProject.layout.buildDirectory.get())
}
