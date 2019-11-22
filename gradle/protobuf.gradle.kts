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

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${project.extra["protobufVersion"]}"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${project.extra["grpcVersion"]}"
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
        dependsOn.add(build)
        archiveFileName.set("example-proto-${archiveVersion}.jar")
        from("build/classes/java/main/")
        destinationDirectory.dir("build/libs")
        include("cn/elmi/grpc/**/*.class")
    }
}