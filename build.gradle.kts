val kotlin_version: String by project
val logback_version: String by project
val kim_version: String by project
val kotlinx_serialization_version: String by project

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("io.ktor.plugin") version "3.0.2"
    id("me.qoomon.git-versioning") version "6.4.3"
}

group = "com.ashampoo.imageproxy"
version = "0.0.1"

gitVersioning.apply {

    refs {
        /* Release / tags have real version numbers */
        tag("v(?<version>.*)") {
            version = "\${ref.version}"
        }
    }
}

application {

    mainClass.set("com.ashampoo.metadataproxy.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {

    /* Ktor service */
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation")

    /* Ktor client */
    implementation("io.ktor:ktor-client-okhttp")

    /* Serialization */
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version")

    implementation("com.ashampoo:kim:$kim_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")
}
