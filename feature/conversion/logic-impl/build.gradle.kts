plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    androidLibrary {
        namespace = "com.onthecrow.nomadrates.currency"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:database"))
            implementation(project(":core:remoteconfig"))
            implementation(project(":feature:conversion:logic-api"))
            implementation(project(":feature:currency:logic-api"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.androidx.room.runtime)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("ksp", libs.androidx.room.compiler)
}
