plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-test-fixtures`
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // coroutines + javax.inject are exposed transitively via :core:common's api.
    api(project(":core:common"))

    // Shared test double lives in test fixtures and is consumed by presentation too.
    testFixturesImplementation(project(":core:common"))
    testFixturesImplementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.kotlinx.coroutines.test)
}
