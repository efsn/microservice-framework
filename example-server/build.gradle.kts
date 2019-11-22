/*
sourceSets {
	main.java.srcDirs = ["src/main/java", "src-gen/main/grpc", "src-gen/main/java", "src/main/proto"]
	main.resources.srcDirs = ["src/main/resources"]
}
*/

dependencies {
    val ext = project.extra

    implementation(project(":grpc-server"))
    implementation("com.google.protobuf:protobuf-java:${ext["protobufVersion"]}")
    implementation("com.google.protobuf:protoc:${ext["protobufVersion"]}")
}

tasks.processResources {
    include("**/*.*")
    exclude("**/*.proto")
}