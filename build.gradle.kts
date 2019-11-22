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

allprojects {
    ext {
        set("guavaVersion", "28.1-jre")
        set("grpcVersion", "1.25.0")
        set("protobufVersion", "3.9.2")
        set("protobufJavaFormatVersion", "1.4")

        set("nettyNativeVersion", "2.0.27.Final")
        set("nettyHandlerVersion", "4.1.43.Final")

        set("etcd4jVersion", "2.17.0")
        set("jedisVersion", "3.1.0")
        set("mysqlConnectorVersion", "5.1.48")

        set("lombokVersion", "1.18.8")
        set("servletVersion", "3.1.0")
        set("validator", "5.4.3.Final")
        set("influxdbVersion", "2.16")
        set("fastjsonVersion", "1.2.62")
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "maven-publish")
    apply(from = rootProject.file("gradle/ktlint.gradle.kts"))

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
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.slf4j:slf4j-api")
        implementation("ch.qos.logback:logback-core")
        implementation("ch.qos.logback:logback-classic")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")

        compileOnly("org.projectlombok:lombok:${project.extra["lombokVersion"]}")
        annotationProcessor("org.projectlombok:lombok:${project.extra["lombokVersion"]}")
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