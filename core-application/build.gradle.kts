plugins {
    id("java")
    kotlin("jvm") version "2.1.0"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core-domain"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}
