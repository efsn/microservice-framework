val lib = ext["lib"] as Map<String, String>

dependencies {
    implementation(project(":grpc-client-java"))
    implementation(lib.getValue("protobuf-java"))
    implementation(lib.getValue("protobuf-java-format"))
    implementation(lib.getValue("netty-tcnative-boringssl-static"))
    implementation(lib.getValue("guava"))

//    implementation(files("libs/example-proto.jar")
    implementation(fileTree("dir" to "lib", "include" to "*.jar"))

    testImplementation(project(":grpc-test"))
}

