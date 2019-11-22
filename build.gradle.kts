import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    `maven-publish`
    id("org.springframework.boot") version "2.1.9.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("com.google.protobuf") version "0.8.10"
    id("com.google.osdetector") version "1.6.2"
}

repositories {
    mavenLocal()
    mavenCentral()
}

allprojects {
    ext {
        set("guavaVersion", "23.0")
        set("grpcVersion", "1.25.0")
        set("protobufVersion", "3.5.1")
        set("protobufJavaFormatVersion", "1.4")

        set("nettyNativeVersion", "2.0.27.Final")
        set("nettyHandlerVersion", "4.1.22.Final")

        set("etcd4jVersion", "2.16.0")
        set("jedisVersion", "2.9.0")
        set("mysqlConnectorVersion", "5.1.34")

        set("lombokVersion", "1.16.16")
        set("servletVersion", "3.1.0")
        set("validator", "5.4.1.Final")
        set("influxdbVersion", "2.5")
        set("fastjsonVersion", "1.2.30")
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.google.protobuf")
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
        implementation("org.projectlombok:lombok:${project.extra["lombokVersion"]}")

        implementation("org.slf4j:slf4j-api")
        implementation("ch.qos.logback:logback-core")
        implementation("ch.qos.logback:logback-classic")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
    }

    the<DependencyManagementExtension>().apply {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
        }
    }

    tasks.withType<BootJar> {
        launchScript()
    }

    tasks.test {
        failFast = true
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}