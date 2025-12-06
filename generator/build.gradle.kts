dependencies {
    api(project(":annotation"))
    api(project(":commons"))
    api("com.google.dagger:dagger:${property("daggerVersion")}")
    api("com.google.googlejavaformat:google-java-format:${property("googleJavaFormatVersion")}")
    api("com.palantir.javapoet:javapoet:${property("javapoetVersion")}")
    api("jakarta.annotation:jakarta.annotation-api:${property("jakartaAnnotationApiVersion")}")
    
    compileOnly("org.slf4j:slf4j-simple:${property("slf4jVersion")}")
    
    // compile-testing dependency has version issues - using Google's version instead
    testImplementation("com.google.testing.compile:compile-testing:0.21.0")
    testImplementation("org.assertj:assertj-core:${property("assertjVersion")}")
    testImplementation("org.junit.jupiter:junit-jupiter:${property("junitVersion")}")
}

tasks.withType<JavaCompile> {
    // Do not run annotation processors on the generator itself
    options.compilerArgs.add("-proc:none")
}
