plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

apply(plugin = "io.github.takahirom.roborazzi")

android {
    namespace = "com.example.netflixtv"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.netflixtv"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-P", "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=1.9.20"
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Roborazzi configuration — uses system properties via buildscript classpath
val runRoborazziBaseline = providers.environmentVariable("RUN_ROBORAZZI_BASELINE").getOrElse("0") == "1"

tasks.withType<Test> {
    // Capture baseline screenshots when RUN_ROBORAZZI_BASELINE=1
    systemProperty("roborazzi.test.record", if (runRoborazziBaseline) "true" else "false")
    // Output dir for reports
    systemProperty("roborazzi.output.dir", layout.buildDirectory.dir("reports/roborazzi").get().asFile.absolutePath)
    systemProperty("roborazzi.compare.with", file("${project.rootDir}/baseline").absolutePath)
    // Allow 1% pixel diff for anti-aliasing variations
    systemProperty("roborazzi.pixel.match.threshold", "0.01")
    systemProperty("roborazzi.report.on.failure", "true")
}

dependencies {
    // Feature modules
    implementation(project(":modules:feature-home"))
    implementation(project(":modules:feature-browse"))
    implementation(project(":modules:feature-detail"))
    implementation(project(":modules:feature-player"))
    implementation(project(":modules:feature-search"))
    implementation(project(":modules:ui-common"))
    implementation(project(":modules:data"))

    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Image loading (for ImageLoaderFactory in MainActivity)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.06.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest")

    // Roborazzi screenshot testing
    androidTestImplementation("io.github.takahirom.roborazzi:roborazzi:1.7.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
}
