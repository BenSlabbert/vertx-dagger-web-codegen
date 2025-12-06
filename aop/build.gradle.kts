dependencies {
    api(project(":annotation"))
    api("com.google.dagger:dagger:${property("daggerVersion")}")
    api("org.slf4j:slf4j-api:${property("slf4jVersion")}")
    
    compileOnly(project(":generator"))
    
    testImplementation("org.assertj:assertj-core:${property("assertjVersion")}")
    testImplementation("org.junit.jupiter:junit-jupiter:${property("junitVersion")}")
}

tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations.annotationProcessor.get()
}

dependencies {
    annotationProcessor(project(":generator"))
}
