plugins {
    id("buildlogic.java-conventions")
    `java-test-fixtures`
}

dependencies {
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    api(libs.commons.codec.commons.codec)
    api(libs.org.testcontainers.testcontainers.postgresql)
    api(project(":commons"))
    api(project(":config"))
    api(project(":logging"))
    api(libs.io.rest.assured.rest.assured) {
        // TODO: This exclude was sourced from a POM exclusion and is NOT exactly equivalent, see:
        // https://docs.gradle.org/9.4.1/userguide/build_init_plugin.html#sec:pom_maven_conversion
        exclude(mapOf("group" to "commons-codec", "module" to "commons-codec"))
    }

    testFixturesApi(libs.io.vertx.vertx.web.client)
    testFixturesApi(libs.com.microsoft.playwright.playwright)
    testFixturesApi(libs.org.mockito.mockito.core)
    testFixturesApi(libs.org.testcontainers.testcontainers)
    testFixturesApi(libs.org.testcontainers.testcontainers.junit.jupiter)
    testFixturesApi(libs.compile.testing)
    testFixturesApi(libs.io.vertx.vertx.junit5)
    testFixturesApi(libs.org.assertj.assertj.core)
    testFixturesApi(libs.org.junit.jupiter.junit.jupiter)
    testFixturesApi(libs.org.junit.jupiter.junit.jupiter.api)
    testFixturesRuntimeOnly(libs.org.junit.jupiter.junit.jupiter.engine)
    testFixturesRuntimeOnly(libs.org.junit.platform.junit.platform.launcher)
}

description = "commons-test"
