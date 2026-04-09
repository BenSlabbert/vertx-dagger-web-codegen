plugins {
  `java-gradle-plugin`
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

dependencies {
  // Resolved via includeBuild("../advice-extractor-core") in settings.gradle.kts when building
  // as part of the composite build. For standalone builds, the published artifact is used.
  implementation("github.benslabbert.vdw.codegen:advice-extractor-core:${project.version}")
}

gradlePlugin {
  plugins {
    create("mergeAdvices") {
      id = "github.benslabbert.vdw.codegen.merge-advices"
      implementationClass = "github.benslabbert.vdw.codegen.gradle.MergeAdvicesPlugin"
      displayName = "Merge Advices Plugin"
      description =
          "Merges META-INF/advice_annotations files from dependency JARs with the " +
              "annotation-processor-generated file for the current module."
    }
  }
}

publishing {
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

description = "advice-extractor-gradle-plugin"
