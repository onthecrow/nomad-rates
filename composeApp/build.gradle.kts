import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.firebase.crashlytics)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }

        val archSlice = when (iosTarget.name) {
            "iosArm64" -> "ios-arm64"
            "iosSimulatorArm64" -> "ios-arm64_x86_64-simulator"
            else -> throw GradleException("Unknown iOS target architecture: ${iosTarget.name}")
        }

        val libsDir = rootProject.file("libs")

        val firebaseGroups = listOf(
            "FirebaseAnalytics",
            "FirebaseCrashlytics",
            "FirebaseRemoteConfig"
        )

        fun getFrameworks(): List<Pair<String, String>> {
            val frameworks = mutableListOf<Pair<String, String>>()

            firebaseGroups.forEach { groupName ->
                val groupDir = File(libsDir, groupName)
                if (groupDir.exists()) {
                    groupDir.listFiles { file -> file.isDirectory && file.name.endsWith(".xcframework") }
                        ?.forEach { xcFramework ->
                            val sliceDir = File(xcFramework, archSlice)
                            if (sliceDir.exists()) {
                                val frameworkName = xcFramework.name.removeSuffix(".xcframework")
                                frameworks.add(frameworkName to sliceDir.absolutePath)
                            } else {
                                logger.warn("Warning: Slice '$archSlice' not found in ${xcFramework.name}")
                            }
                        }
                }
            }
            return frameworks.distinctBy { it.first }
        }

        val allFrameworks = getFrameworks()

        iosTarget.compilations.getByName("main") {
            @Suppress("unused")
            val firebase by cinterops.creating {
                defFile(project.file("src/nativeInterop/cinterop/firebase.def"))

                allFrameworks.forEach { (_, path) ->
                    extraOpts("-compiler-option", "-F$path")
                }
            }
        }

        iosTarget.binaries.all {
            allFrameworks.forEach { (_, path) ->
                linkerOpts("-F$path")
            }

            allFrameworks.forEach { (name, _) ->
                linkerOpts("-framework", name)
            }

            linkerOpts(
                "-lsqlite3", "-lz", "-lc++",
                "-framework", "StoreKit",
                "-framework", "Foundation",
                "-framework", "UIKit",
                "-framework", "SystemConfiguration",
                "-framework", "Security",
                "-framework", "AdSupport",
                "-framework", "UserNotifications"
            )
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.crashlytics)
        }
        commonMain.dependencies {
            implementation(project(":core:navigation:api"))
            implementation(project(":core:navigation:impl"))
            implementation(project(":core:ui"))
            implementation(project(":feature:conversion:logic-api"))
            implementation(project(":feature:conversion:logic-impl"))
            implementation(project(":feature:conversion:ui-api"))
            implementation(project(":feature:conversion:ui-impl"))
            implementation(project(":feature:currency:api"))
            implementation(project(":feature:currency:impl"))
            implementation(project(":feature:currency:logic-api"))
            implementation(project(":feature:currency:logic-impl"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.onthecrow.nomadrates"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.onthecrow.nomadrates"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties()
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
            storeFile = keystoreProperties["storeFile"]?.let { file(it) }
            storePassword = keystoreProperties["storePassword"] as String?
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
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
}

dependencies {
    debugImplementation(compose.uiTooling)
}
