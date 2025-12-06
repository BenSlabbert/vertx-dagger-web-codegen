plugins {
    id("com.gradleup.shadow") version "9.0.1"
}

dependencies {
    implementation(project(":annotation"))
    implementation(project(":logging"))
    implementation(project(":txmanager:platform"))
    implementation("net.bytebuddy:byte-buddy:${property("byteBuddyVersion")}")
}

tasks.shadowJar {
    archiveBaseName.set("agent")
    archiveClassifier.set("uber")
    manifest {
        attributes(
            "Premain-Class" to "github.benslabbert.vdw.codegen.txmanager.agent.TxManagerAgent"
        )
    }
}
