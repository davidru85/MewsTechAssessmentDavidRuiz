plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mews.guestroom.feature.controls.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    // Mirrors the app's `dataSource` dimension so Gradle variant-matching wires the
    // matching source set (mock/live) into each app flavor. This is the production
    // seam: the bound ControlsDataSource is chosen by flavor, not by code change.
    flavorDimensions += "dataSource"
    productFlavors {
        create("mock") { dimension = "dataSource" }
        create("live") { dimension = "dataSource" }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":feature:controls:domain"))
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
