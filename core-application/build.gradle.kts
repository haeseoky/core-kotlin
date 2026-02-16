plugins {
    id("java-library")
    kotlin("jvm") version "2.1.0"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.1")
    }
}

dependencies {
    api(project(":core-domain"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Spring annotations (@Service, @Transactional)
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
    implementation("jakarta.annotation:jakarta.annotation-api")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.springframework:spring-context")
    testImplementation("org.mockito:mockito-core")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
