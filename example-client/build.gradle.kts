dependencies {
    val ext = project.extra

    implementation(project(":grpc-client-java"))
    implementation("com.google.protobuf:protobuf-java:${ext["protobufVersion"]}")
    implementation("com.googlecode.protobuf-java-format:protobuf-java-format:${ext["protobufJavaFormatVersion"]}")
    implementation("io.netty:netty-tcnative-boringssl-static:${ext["nettyNativeVersion"]}")
    implementation(fileTree("dir" to "libs", "include" to "*.jar"))
//    implementation(files("libs/example-proto.jar")

    testImplementation(project(":grpc-test"))
}

