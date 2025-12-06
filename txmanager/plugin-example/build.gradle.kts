plugins {
    id("com.gradleup.shadow") version "9.0.1"
}

dependencies {
    implementation(project(":advice-transformer"))
    implementation(project(":annotation"))
    implementation(project(":logging"))
    implementation(project(":txmanager:platform"))
    
    testImplementation("org.assertj:assertj-core:${property("assertjVersion")}")
    testImplementation("org.junit.jupiter:junit-jupiter:${property("junitVersion")}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("junitVersion")}")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:${property("junitVersion")}")
    testImplementation("org.mockito:mockito-core:${property("mockitoVersion")}")
    testImplementation("org.mockito:mockito-junit-jupiter:${property("mockitoVersion")}")
}

// Note: ByteBuddy transformation is not configured in Gradle
// The byte-buddy-maven-plugin transform goal does not have a direct Gradle equivalent
// This would require custom bytecode manipulation tasks if needed

tasks.shadowJar {
    archiveBaseName.set("plugin-example")
    archiveClassifier.set("uber")
    manifest {
        attributes(
            "Main-Class" to "github.benslabbert.txmanager.example.Main"
        )
    }
}

tasks.test {
    jvmArgs("-javaagent:\${configurations.testRuntimeClasspath.find { it.name.contains(\"mockito-core\") }}")
}
