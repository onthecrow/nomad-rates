plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidLibrary {
        namespace = "com.onthecrow.nomadrates"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
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
            implementation(libs.ui.tooling.preview)
            implementation(libs.koin.android)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.config)
        }
        commonMain.dependencies {
            implementation(projects.core.database)
            implementation(projects.core.coroutines)
            implementation(projects.core.datastore)
            implementation(projects.core.navigation.api)
            implementation(projects.core.navigation.impl)
            implementation(projects.core.remoteconfig)
            implementation(projects.core.ui)
            implementation(projects.feature.conversion.logicApi)
            implementation(projects.feature.conversion.logicImpl)
            implementation(projects.feature.conversion.uiApi)
            implementation(projects.feature.conversion.uiImpl)
            implementation(projects.feature.currency.uiApi)
            implementation(projects.feature.currency.uiImpl)
            implementation(projects.feature.currency.logicApi)
            implementation(projects.feature.currency.logicImpl)
            implementation(projects.feature.settings.uiApi)
            implementation(projects.feature.settings.uiImpl)
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.components.resources)
            implementation(libs.ui.tooling.preview)
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

dependencies {
    androidRuntimeClasspath(libs.ui.tooling)
}
