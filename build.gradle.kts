buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.github.takahirom.roborazzi:roborazzi-gradle-plugin:1.7.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.25" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}

// Force consistent Kotlin version across all modules
allprojects {
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:1.9.25")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.25")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.25")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:1.9.25")
            force("org.jetbrains.kotlin:kotlin-reflect:1.9.25")
        }
    }
}
