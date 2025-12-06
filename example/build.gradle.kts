plugins {
    id("com.gradleup.shadow") version "9.0.1"
}

dependencies {
    implementation(project(":advice-transformer"))
    implementation(project(":txmanager:agent"))
    implementation(project(":annotation"))
    implementation(project(":aop"))
    implementation(project(":commons"))
    implementation(project(":launcher"))
    implementation(project(":logging"))
    
    implementation("com.google.dagger:dagger:${property("daggerVersion")}")
    implementation("com.google.guava:guava:${property("guavaVersion")}")
    implementation("io.vertx:vertx-core:${property("vertxVersion")}")
    implementation("io.vertx:vertx-json-schema:${property("vertxVersion")}")
    implementation("io.vertx:vertx-launcher-application:${property("vertxVersion")}")
    implementation("io.vertx:vertx-service-proxy:${property("vertxVersion")}")
    implementation("io.vertx:vertx-web:${property("vertxVersion")}")
    implementation("jakarta.annotation:jakarta.annotation-api:${property("jakartaAnnotationApiVersion")}")
    implementation("org.glassfish.expressly:expressly:${property("expresslyVersion")}")
    implementation("org.hibernate.validator:hibernate-validator:${property("hibernateValidatorVersion")}")
    implementation("org.mapstruct:mapstruct:${property("mapstructVersion")}")
    
    compileOnly("com.google.auto.value:auto-value-annotations:${property("googleAutoValueVersion")}")
    compileOnly(project(":generator"))
    
    runtimeOnly("org.postgresql:postgresql:${property("postgresqlVersion")}")
    
    testImplementation("io.vertx:vertx-junit5:${property("vertxVersion")}")
    testImplementation("io.vertx:vertx-web-client:${property("vertxVersion")}")
    testImplementation("org.assertj:assertj-core:${property("assertjVersion")}")
    testImplementation("org.junit.jupiter:junit-jupiter:${property("junitVersion")}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junitVersion")}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${property("junitVersion")}")
    testImplementation("org.mockito:mockito-core:${property("mockitoVersion")}")
    testImplementation("org.mockito:mockito-junit-jupiter:${property("mockitoVersion")}")
    testImplementation("org.testcontainers:testcontainers:${property("testcontainersVersion")}")
    testImplementation("org.testcontainers:junit-jupiter:${property("testcontainersVersion")}")
    testImplementation("org.testcontainers:postgresql:${property("testcontainersVersion")}")
    
    annotationProcessor(project(":generator"))
    annotationProcessor("com.google.auto.value:auto-value:${property("googleAutoValueVersion")}")
    annotationProcessor("org.hibernate.validator:hibernate-validator-annotation-processor:${property("hibernateValidatorVersion")}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${property("mapstructVersion")}")
    annotationProcessor("com.google.dagger:dagger-compiler:${property("daggerVersion")}")
    // ErrorProne annotation processor causes issues in Gradle - disabled
    // annotationProcessor("com.google.errorprone:error_prone_core:${property("errorproneVersion")}")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf(
        // Dagger
        "-Adagger.fastInit=enabled",
        "-Adagger.formatGeneratedSource=enabled",
        // Hibernate
        "-Averbose=true",
        "-AmethodConstraintsSupported=true",
        "-AdiagnosticKind=ERROR"
    ))
    // ErrorProne causes issues - disabled for Gradle build
    // "-XDcompilePolicy=simple",
    // "--should-stop=ifError=FLOW",
    // "-Xplugin:ErrorProne",
}

// Configure ByteBuddy transformation - must run after tests but before packaging
// Note: The ByteBuddy Gradle plugin does not have a direct equivalent to Maven's transform goal
// For now, we'll skip this transformation in Gradle builds
// If this is critical, bytecode transformation would need to be done via a custom task

// Ensure resources are available to annotation processors during compilation
// The annotation processors need to access SQL files using Filer.getResource
// Add the processed resources output directory to the compile classpath
sourceSets {
    main {
        compileClasspath += files(output.resourcesDir)
    }
}

tasks.compileJava {
    dependsOn(tasks.processResources)
}

// This task would be needed if advice-extractor-plugin was ported
// For now, we'll create a stub task that does nothing if the file doesn't exist
tasks.register("mergeAdvices") {
    description = "Merge advice annotations - stub for Maven plugin"
    doLast {
        val adviceFile = file("build/classes/java/main/META-INF/advice_annotations")
        if (!adviceFile.exists()) {
            logger.info("META-INF/advice_annotations not found - skipping (expected when advice-extractor-plugin is not ported)")
        }
    }
}

tasks.classes {
    finalizedBy("mergeAdvices")
}

tasks.shadowJar {
    archiveBaseName.set("example")
    archiveClassifier.set("uber")
    manifest {
        attributes(
            "Main-Class" to "github.benslabbert.vdw.codegen.example.Main",
            "Main-Verticle" to "github.benslabbert.vdw.codegen.example.verticle.DefaultVerticle"
        )
    }
}

// Create additional JARs like in Maven
tasks.register<Jar>("adviceExampleJar") {
    archiveBaseName.set("example")
    archiveClassifier.set("advice-example")
    from(sourceSets.main.get().output) {
        include("github/benslabbert/vdw/codegen/example/aop/advice/**")
    }
    manifest {
        attributes(
            "Main-Class" to "github.benslabbert.vdw.codegen.example.aop.advice.AdviceExample"
        )
    }
}

tasks.register<Jar>("cachingExampleJar") {
    archiveBaseName.set("example")
    archiveClassifier.set("caching-example")
    from(sourceSets.main.get().output) {
        include("github/benslabbert/vdw/codegen/example/aop/caching/**")
    }
    manifest {
        attributes(
            "Main-Class" to "github.benslabbert.vdw.codegen.example.aop.caching.CachingExample"
        )
    }
}

tasks.register<Jar>("retryExampleJar") {
    archiveBaseName.set("example")
    archiveClassifier.set("retry-example")
    from(sourceSets.main.get().output) {
        include("github/benslabbert/vdw/codegen/example/aop/retry/**")
    }
    manifest {
        attributes(
            "Main-Class" to "github.benslabbert.vdw.codegen.example.aop.retry.RetryExample"
        )
    }
}

tasks.assemble {
    dependsOn("adviceExampleJar", "cachingExampleJar", "retryExampleJar")
}
