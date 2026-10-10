plugins {
    id("java-library")
    alias(libs.plugins.paperweightUserdev)
    alias(libs.plugins.runPaper)
}

dependencies {
    paperweight.paperDevBundle(projectProperty("minecraft_version") + ".build.+")
    implementation(project(":common"))
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    runServer {
        minecraftVersion(projectProperty("minecraft_version"))
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        val props = mapOf(
            "version" to project.version,
            "description" to project.description,
            "api_version" to projectProperty("minecraft_version"),
        )
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}

fun projectProperty(name: String): String = project.property(name).toString()