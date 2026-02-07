plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    androidLibrary {
        namespace = "com.onthecrow.nomadrates.navigation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    jvm()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.navigation3)
            implementation(project(":core:navigation:api"))
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.ui)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.koin.core)
        }
    }
}
