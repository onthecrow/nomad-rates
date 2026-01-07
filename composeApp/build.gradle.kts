import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
                    // Ищем все папки, заканчивающиеся на .xcframework
                    groupDir.listFiles { file -> file.isDirectory && file.name.endsWith(".xcframework") }
                        ?.forEach { xcFramework ->
                            // Формируем путь внутрь: libs/Group/Name.xcframework/ios-arm64/
                            val sliceDir = File(xcFramework, archSlice)
                            if (sliceDir.exists()) {
                                // Имя фреймворка = имя файла без расширения (FirebaseAnalytics.xcframework -> FirebaseAnalytics)
                                val frameworkName = xcFramework.name.removeSuffix(".xcframework")
                                frameworks.add(frameworkName to sliceDir.absolutePath)
                            } else {
                                logger.warn("Warning: Slice '$archSlice' not found in ${xcFramework.name}")
                            }
                        }
                }
            }
            // Убираем дубликаты, если один и тот же фреймворк (например FirebaseCore) лежит в разных папках
            return frameworks.distinctBy { it.first }
        }

        val allFrameworks = getFrameworks()

        // Настройка Cinterop
        iosTarget.compilations.getByName("main") {
            val firebase by cinterops.creating {
                defFile(project.file("src/nativeInterop/cinterop/firebase.def"))

                // Твой список allFrameworks уже содержит правильные пути к ios-arm64 слайсам
                allFrameworks.forEach { (_, path) ->
                    // Используем extraOpts для надежной передачи флага компилятору
                    extraOpts("-compiler-option", "-F$path")
                }
            }
        }

        // Настройка бинарников (Линковка)
        iosTarget.binaries.all {
            // 1. Передаем пути поиска (-F)
            allFrameworks.forEach { (_, path) ->
                linkerOpts("-F$path")
            }

            // 2. Линкуем сами фреймворки (-framework Name)
            allFrameworks.forEach { (name, _) ->
                linkerOpts("-framework", name)
            }

            // 3. Добавляем системные зависимости, которые нужны Firebase
            // Firebase требует довольно много системных библиотек
            linkerOpts(
                "-lsqlite3", "-lz", "-lc++",
                "-framework", "StoreKit",
                "-framework", "Foundation",
                "-framework", "UIKit",
                "-framework", "SystemConfiguration",
                "-framework", "Security",
                "-framework", "AdSupport",     // Нужно для Analytics
                "-framework", "UserNotifications" // Может понадобиться
            )
        }

    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.config)
            implementation(libs.firebase.crashlytics)
        }
        commonMain.dependencies {
            implementation(project(":feature:currency:api"))
            implementation(project(":feature:currency:impl"))
            implementation(project(":core:navigation"))
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
            implementation(project(":core:navigation"))
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
