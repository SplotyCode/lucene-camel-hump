plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "de.scandurra"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(21)
}
dependencies {
    val luceneVersion = "10.3.1"
    implementation("org.apache.lucene:lucene-core:$luceneVersion")
    implementation("org.apache.lucene:lucene-analysis-common:${luceneVersion}")
}


tasks.test {
    useJUnitPlatform()
}