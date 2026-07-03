plugins {
    id("java")
}

group = "de.tutter05"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.javassist:javassist:3.30.2-GA")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "de.tutter05.kuhfuss.Main",
            "Manifest-Version" to "1.0",
            "Premain-Class" to "de.tutter05.kuhfuss.AgentMain",
            "Agent-Class" to "de.tutter05.kuhfuss.AgentMain",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true"
        )
    }

    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

}

tasks.register<JavaExec>("buildAndAttach") {
    group = "application"
    description = "Builds the jar and attempts to attach it to a running Minecraft instance."

    val jarTask = tasks.named<Jar>("jar").get()
    dependsOn(jarTask)

    classpath = files(jarTask.archiveFile)

    mainClass.set("de.tutter05.kuhfuss.Main")
}