dependencies {
    api(project(":logging"))
    api("io.vertx:vertx-core:${property("vertxVersion")}")
    api("io.vertx:vertx-launcher-application:${property("vertxVersion")}")
    api("io.vertx:vertx-opentelemetry:${property("vertxVersion")}")
}
