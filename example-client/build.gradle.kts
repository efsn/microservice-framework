dependencies {
    val ext = project.extra

    implementation(project(":grpc-client-java"))
    implementation("com.google.protobuf:protobuf-java:${ext["protobufVersion"]}")
    implementation("com.googlecode.protobuf-java-format:protobuf-java-format:${ext["protobufJavaFormatVersion"]}")
    implementation("io.netty:netty-tcnative-boringssl-static:${ext["nettyNativeVersion"]}")
    implementation(fileTree("dir" to "lib", "include" to "*.jar"))
//    implementation(files("libs/example-proto.jar")
    implementation("com.google.guava:guava:${ext["guavaVersion"]}")

    testImplementation(project(":grpc-test"))
}

