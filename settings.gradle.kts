
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://jitpack.io")
        jcenter()
    }
}

rootProject.name = "CalendarKMMSUAI"
include(":androidApp")
include(":shared")