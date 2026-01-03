import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    // --- ANDROID TARGET ---
    androidTarget {
        @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // --- IOS TARGETS ---
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    // --- DESKTOP TARGET ---
    jvm("desktop")

    // --- DEPENDENCIES ---
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.android.material)
                implementation(libs.androidx.work.runtime.ktx)
            }
        }
        val commonMain by getting {
            dependencies {
                // Compose Core
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // Material Icons
                implementation(compose.materialIconsExtended)

                // Lifecycle (ViewModel & Runtime)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                // Voyager (Navigation)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenmodel)
                implementation(libs.voyager.koin)
                implementation(libs.voyager.transitions)

                // Koin (DI)
                implementation(libs.koin.core)

                // DataStore (Basit veriler için)
                implementation(libs.androidx.datastore.preferences.core)
                implementation(libs.squareup.okio)

                // --- EKLENEN DATABASE DEPENDENCIES ---
                // Room Runtime
                implementation(libs.androidx.room.runtime)
                // SQLite Bundled (Native Driver - iOS/Desktop için kritik)
                implementation(libs.androidx.sqlite.bundled)
                // Kotlinx DateTime (KMP zaman işlemleri için)
                implementation(libs.kotlinx.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }
}

// --- ROOM SCHEMA CONFIGURATION ---
// Room'un veritabanı şemasını nerede oluşturacağını belirtiyoruz
room {
    schemaDirectory("$projectDir/schemas")
}

// --- KSP DEPENDENCIES (Code Generation) ---
// Room Compiler'ı tüm platformlara bağlıyoruz
dependencies {
    // KSP, CommonMainMetadata ile tüm target'lara kod üretir
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    // Her platform için ayrı ayrı eklemek en garantisidir
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

android {
    namespace = "com.vahitkeskin.equatix"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.vahitkeskin.equatix"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// --- DESKTOP CONFIGURATION ---
compose.desktop {
    application {
        mainClass = "com.vahitkeskin.equatix.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.vahitkeskin.equatix"
            packageVersion = "1.0.0"
            description = "Equatix: Matrix Math Puzzle"
            copyright = "© 2025 Vahit Keskin. All rights reserved."
        }
    }
}

// Versiyon çakışmalarını önleme
configurations.all {
    resolutionStrategy {
        force("androidx.datastore:datastore-preferences-core:1.1.1")
        force("androidx.datastore:datastore-core:1.1.1")
        force("com.squareup.okio:okio:3.9.1")
    }
}