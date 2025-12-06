dependencies {
    api(project(":txmanager:agent"))
    api(project(":aop"))
    api("net.bytebuddy:byte-buddy:${property("byteBuddyVersion")}")
    api("net.bytebuddy:byte-buddy-dep:${property("byteBuddyVersion")}")
    api("org.ow2.asm:asm:${property("asmVersion")}")
    api("org.slf4j:slf4j-api:${property("slf4jVersion")}")
}
