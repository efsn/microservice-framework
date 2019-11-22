dependencies {
    val ext = project.extra

    implementation("io.grpc:grpc-netty:${ext["grpcVersion"]}")
    implementation("io.grpc:grpc-protobuf:${ext["grpcVersion"]}")
    implementation("io.grpc:grpc-stub:${ext["grpcVersion"]}")
    implementation("io.grpc:grpc-auth:${ext["grpcVersion"]}")
    implementation("io.netty:netty-tcnative:${ext["nettyNativeVersion"]}")
    implementation("com.google.guava:guava:${ext["guavaVersion"]}")
}
