import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    `maven-publish`
    id("org.springframework.boot") version "2.2.1.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

repositories {
    mavenLocal()
    mavenCentral()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "maven-publish")
    apply(from = rootProject.file("gradle/ktlint.gradle.kts"))
    apply(from = rootProject.file("gradle/dependencies.gradle.kts"))

    val lib = ext["lib"] as Map<String, String>

    group = "cn.elmi.microservice"
    version = "1.0.0-SNAPSHOT"

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        implementation(lib.getValue("spring-boot-starter"))
        implementation(lib.getValue("slf4j-api"))
        implementation(lib.getValue("logback-core"))
        implementation(lib.getValue("logback-classic"))

        testImplementation(lib.getValue("spring-boot-starter-test"))
        testImplementation(lib.getValue("junit-jupiter-api"))
        testRuntimeOnly(lib.getValue("junit-jupiter-engine"))

        compileOnly(lib.getValue("lombok"))
        annotationProcessor(lib.getValue("lombok"))
    }

    the<DependencyManagementExtension>().apply {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
        }
    }

    tasks {
        bootJar {
            launchScript()
        }

        test {
            failFast = true
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }
    }

    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("${project.findProperty("GITHUB_URI")}/microservice-framework")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                    username = project.findProperty("gpr.key") as String? ?: System.getenv("PASSWORD")
                }
            }
        }
        publications {
            register<MavenPublication>("gpr") {
                from(components["java"])
            }
        }
    }
}