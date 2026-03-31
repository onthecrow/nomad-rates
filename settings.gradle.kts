rootProject.name = "NomadRates"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":composeApp")
include(":core:coroutines")
include(":core:database")
include(":core:datastore")
include(":core:entity")
include(":core:navigation:api")
include(":core:navigation:impl")
include(":core:remoteconfig")
include(":core:ui")
include(":core:ui-core")
include(":core:util")
include(":feature:conversion:logic-api")
include(":feature:conversion:logic-impl")
include(":feature:conversion:ui-api")
include(":feature:conversion:ui-impl")
include(":feature:currency:ui-api")
include(":feature:currency:ui-impl")
include(":feature:currency:logic-api")
include(":feature:currency:logic-impl")
include(":feature:settings:ui-api")
include(":feature:settings:ui-impl")
