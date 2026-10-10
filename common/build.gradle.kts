plugins {
    id("java")
    alias(libs.plugins.paperweightUserdev)
}

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle(project.property("minecraft_version").toString() + ".build.+")
}