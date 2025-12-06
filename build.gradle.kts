plugins {
    id("java")
    id("java-library")
}

allprojects {
    group = "github.benslabbert.vdw.codegen"
    version = findProperty("revision") as String? ?: "0.0.0-SNAPSHOT"
    
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/BenSlabbert/git-version-extension")
            credentials {
                username = System.getenv("GH_USERNAME") ?: "BenSlabbert"
                password = System.getenv("GH_TOKEN") ?: ""
            }
        }
        maven {
            url = uri("https://maven.pkg.github.com/BenSlabbert/vertx-dagger-bom")
            credentials {
                username = System.getenv("GH_USERNAME") ?: "BenSlabbert"
                password = System.getenv("GH_TOKEN") ?: ""
            }
        }
        maven {
            url = uri("https://maven.pkg.github.com/BenSlabbert/vertx-dagger-web-codegen")
            credentials {
                username = System.getenv("GH_USERNAME") ?: "BenSlabbert"
                password = System.getenv("GH_TOKEN") ?: ""
            }
        }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(findProperty("javaVersion") as String? ?: "21"))
        }
    }
    
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf(
            "-parameters",
            "-g"
        ))
        // Add JVM arguments for google-java-format compatibility
        options.forkOptions.jvmArgs?.addAll(listOf(
            "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
            "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
            "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
            "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
            "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
            "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
        ))
    }
    
    tasks.withType<Test> {
        useJUnitPlatform()
        maxParallelForks = Runtime.getRuntime().availableProcessors()
        jvmArgs(
            "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
            "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED"
        )
    }
    
    // Ensure JUnit Platform launcher is available for all test tasks
    dependencies {
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}
