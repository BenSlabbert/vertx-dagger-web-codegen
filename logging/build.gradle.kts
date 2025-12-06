dependencies {
    api(project(":commons"))
    api("io.vertx:vertx-core:${property("vertxVersion")}")
    api("io.vertx:vertx-launcher-application:${property("vertxVersion")}")
    api("io.vertx:vertx-opentelemetry:${property("vertxVersion")}")
}
