plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(projects.core.util)
            api(projects.feature.conversion.logicApi)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
