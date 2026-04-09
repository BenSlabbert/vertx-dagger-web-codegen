plugins {
  `java-library`
  `maven-publish`
}

group = "github.benslabbert.vdw.codegen"

version = findProperty("projectVersion") ?: "0.0.0-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_21
  withSourcesJar()
}

repositories {
  mavenCentral()
}

publishing {
  publications.create<MavenPublication>("maven") { from(components["java"]) }

  repositories {
    maven {
      name = "github"
      url = uri("https://maven.pkg.github.com/BenSlabbert/vertx-dagger-web-codegen")
      credentials {
        username = "BenSlabbert"
        password = System.getenv("GH_TOKEN")
      }
    }
  }
}

description = "advice-extractor-core"
