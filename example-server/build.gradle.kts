import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.ofSourceSet
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("com.google.protobuf") version "0.8.10"
    id("com.google.osdetector") version "1.6.2"
}

apply(plugin = "com.google.protobuf")

sourceSets {
    main {
        java.srcDirs("src/main/java", "src-gen/main/grpc", "src-gen/main/java", "src/main/proto")
        resources.srcDirs("src/main/resources")
    }
}

val lib = ext["lib"] as Map<String, String>

dependencies {
    implementation(project(":grpc-server"))
    implementation(lib.getValue("protobuf-java"))
    implementation(lib.getValue("protoc"))
}

protobuf {
    protoc {
        artifact = lib.getValue("protoc")
    }

    plugins {
        id("grpc") {
            artifact = lib.getValue("protoc-gen-grpc-java")
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
    generatedFilesBaseDir = "$projectDir/src-gen"
}

tasks {
    processResources {
        include("**/*.*")
        exclude("**/*.proto")
    }

    clean {
        delete(protobuf.protobuf.generatedFilesBaseDir)
    }

    register<Jar>("protoJar") {
        enabled = true
        dependsOn.add(build)
        archiveFileName.set("example-proto-${archiveVersion.get()}.jar")
        from("build/classes/java/main/")
        destinationDirectory.dir("build/libs")
        include("cn/elmi/grpc/**/*.class")
    }

    bootJar {
        manifest {
            attributes("Start-Class" to "cn.elmi.Application")
        }
    }
}