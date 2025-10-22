plugins {
    java
    `maven-publish`
    kotlin("jvm") version "2.2.20"
}

group = "io.mikael.karslet"
version = "0.1.5"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation(kotlin("test"))
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.mikaelhg"
            artifactId = "kotlin-peg-dsl"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
