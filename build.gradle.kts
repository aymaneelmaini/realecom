plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.aymanegeek"
version = "0.0.1-SNAPSHOT"
description = "E-commerce project"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.spring.boot.starters)
    implementation(libs.bundles.kotlin.base)
    implementation(libs.arrow.core)
    implementation(libs.springdoc.openapi)
    implementation(libs.stripe.java)
    implementation(libs.postgresql)
    developmentOnly(libs.devtools)

    testImplementation(libs.starter.test) {
        exclude(group = "org.assertj")
        exclude(group = "org.mockito")
    }
    testImplementation(libs.starter.testcontainers)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.bundles.testing.mocks)
    testImplementation(libs.bundles.testcontainers.all)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets {
    create("e2e") {
        kotlin {
            srcDir("src/e2e/kotlin")
        }
        resources {
            srcDir("src/e2e/resources")
        }
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

configurations {
    val e2eImplementation by getting {
        extendsFrom(configurations.testImplementation.get())
    }
    val e2eRuntimeOnly by getting {
        extendsFrom(configurations.testRuntimeOnly.get())
    }
}


val e2e = tasks.register<Test>("e2e") {
    description = "Run integration tests (full Spring context with Testcontainers)"
    group = "verification"

    testClassesDirs = sourceSets["e2e"].output.classesDirs
    classpath = sourceSets["e2e"].runtimeClasspath

    shouldRunAfter(tasks.test)

    systemProperty("spring.profiles.active", "test")
}

tasks.register("testAll") {
    description = "Runs all tests: unit and integration"
    group = "verification"
    dependsOn(tasks.test, e2e)
}
