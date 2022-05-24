plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "ru.castprograms.calendarkmmsuai.android"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.6.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("io.insert-koin:koin-android:3.1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
    implementation("dev.icerock.moko:mvvm-core:0.13.0")
    implementation("androidx.navigation:navigation-fragment:2.4.2")
    implementation("androidx.core:core:1.7.0")
    implementation("net.sf.biweekly:biweekly:0.6.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.2")
}