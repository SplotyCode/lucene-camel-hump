plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "de.scandurra"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}