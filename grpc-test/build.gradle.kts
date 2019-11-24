val lib = ext["lib"] as Map<String, String>

dependencies {
    implementation(lib.getValue("grpc-netty"))
    implementation(lib.getValue("grpc-protobuf"))
    implementation(lib.getValue("grpc-stub"))
    implementation(lib.getValue("grpc-auth"))
    implementation(lib.getValue("netty-tcnative"))
    implementation(lib.getValue("guava"))

    implementation(lib.getValue("junit-jupiter-api"))
    runtimeOnly(lib.getValue("junit-jupiter-engine"))
}
