import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Kotlin Multiplatform (Android + iOS + Desktop)
    alias(libs.plugins.kotlinMultiplatform)
    // Android Application plugin
    alias(libs.plugins.androidApplication)
    // Compose Multiplatform
    alias(libs.plugins.composeMultiplatform)
    // Compose Compiler (required for Kotlin 2.x)
    alias(libs.plugins.composeCompiler)
    // Kotlin Symbol Processing (Room, etc.)
    alias(libs.plugins.ksp)
    // Room Gradle Plugin (schema management)
    alias(libs.plugins.room)
}

kotlin {

    // =========================
    // ANDROID TARGET
    // =========================
    androidTarget {
        @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            // JVM bytecode target for Android
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // =========================
    // IOS TARGETS
    // =========================
    listOf(
        iosArm64(),           // Physical devices
        iosSimulatorArm64()   // Apple Silicon simulator
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp" // iOS framework name
            isStatic = true         // Recommended for SwiftUI integration
        }
    }

    // =========================
    // DESKTOP TARGET
    // =========================
    jvm("desktop")

    // =========================
    // SOURCE SETS & DEPENDENCIES
    // =========================
    sourceSets {

        // -------- ANDROID MAIN --------
        val androidMain by getting {
            dependencies {
                implementation(compose.preview)                 // Android Studio preview support
                implementation(libs.androidx.activity.compose) // Activity + Compose integration
                implementation(libs.android.material)          // Material Components
                implementation(libs.androidx.work.runtime.ktx) // WorkManager
            }
        }

        // -------- COMMON MAIN (KMP CORE) --------
        val commonMain by getting {
            dependencies {
                // --- Compose Core ---
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // --- Material Icons ---
                implementation(compose.materialIconsExtended)

                // --- Lifecycle ---
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                // --- Navigation (Voyager) ---
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenmodel)
                implementation(libs.voyager.koin)
                implementation(libs.voyager.transitions)

                // --- Dependency Injection ---
                implementation(libs.koin.core)

                // --- Data & IO ---
                implementation(libs.androidx.datastore.preferences.core)
                implementation(libs.squareup.okio)

                // --- Database (KMP-compatible) ---
                implementation(libs.androidx.room.runtime)      // Room runtime
                implementation(libs.androidx.sqlite.bundled)   // Native SQLite (iOS/Desktop)
                implementation(libs.kotlinx.datetime)           // Cross-platform date & time
            }
        }

        // -------- COMMON TEST --------
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        // -------- DESKTOP MAIN --------
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)      // Desktop Compose runtime
                implementation(libs.kotlinx.coroutines.swing)  // Swing dispatcher
            }
        }
    }
}

// =========================
// ROOM CONFIGURATION
// =========================
// Defines where Room database schemas will be generated
room {
    schemaDirectory("$projectDir/schemas")
}

// =========================
// KSP CONFIGURATION
// =========================
// Room compiler must be attached to ALL targets explicitly
dependencies {
    // Shared metadata for KMP (critical for code generation)
    add("kspCommonMainMetadata", libs.androidx.room.compiler)

    // Platform-specific KSP configurations (safest approach)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

// =========================
// ANDROID CONFIGURATION
// =========================
android {
    namespace = "com.vahitkeskin.equatix"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.vahitkeskin.equatix"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        versionCode = 2
        versionName = "1.0.2"
    }

    // Exclude conflicting license files from packaging
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // Release build configuration
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    // Java bytecode compatibility
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// =========================
// DEBUG DEPENDENCIES
// =========================
dependencies {
    debugImplementation(compose.uiTooling) // Layout inspector & preview tooling
}

// =========================
// DESKTOP DISTRIBUTION
// =========================
compose.desktop {
    application {
        mainClass = "com.vahitkeskin.equatix.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg, // macOS
                TargetFormat.Msi, // Windows
                TargetFormat.Deb  // Linux
            )

            packageName = "com.vahitkeskin.equatix"
            packageVersion = "1.0.0"

            description = "Equatix: Matrix Math Puzzle"
            copyright =
                "© 2025 Vahit Keskin. All rights reserved."
        }
    }
}

// =========================
// DEPENDENCY CONFLICT RESOLUTION
// =========================
// Explicitly force critical transitive dependencies
configurations.all {
    resolutionStrategy {
        force("androidx.datastore:datastore-preferences-core:1.1.1")
        force("androidx.datastore:datastore-core:1.1.1")
        force("com.squareup.okio:okio:3.9.1")
    }
}