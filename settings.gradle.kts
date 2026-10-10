pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		mavenCentral()
		gradlePluginPortal()
		maven("https://repo.papermc.io/repository/maven-public/")
	}

	plugins {
		id("net.fabricmc.fabric-loom") version "1.18-SNAPSHOT"
	}
}

// Should match your modid
rootProject.name = "lagless"

listOf(
	"common",
	"fabric",
	"paper"
).forEach {
	include(it)
}