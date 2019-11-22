dependencies {
    val ext = project.extra

    implementation(project(":grpc-etcd"))
    implementation("io.grpc:grpc-protobuf:${ext["grpcVersion"]}")
    implementation("io.grpc:grpc-stub:${ext["grpcVersion"]}")
    implementation("io.grpc:grpc-auth:${ext["grpcVersion"]}")

    implementation("io.grpc:grpc-netty:${ext["grpcVersion"]}") {
        exclude(group = "io.netty", module = "*")
    }

    implementation("io.netty:netty-codec-http2:${ext["nettyHandlerVersion"]}")
    implementation("org.apache.commons:commons-pool2:2.5.0")
    implementation("org.apache.commons:commons-lang3:3.7")
    implementation("com.google.guava:guava:${ext["guavaVersion"]}")
    implementation("cn.elmi.components:component-cache-spring-boot-starter:1.0-SNAPSHOT")

    implementation("com.netflix.ribbon:ribbon:2.2.0") {
        exclude(group = "io.netty", module = "netty-common")
        exclude(group = "com.google.guava", module = "guava")
    }

    implementation("com.netflix.ribbon:ribbon-core:2.2.0") {
        exclude(group = "com.google.guava", module = "guava")
    }

    implementation("com.netflix.ribbon:ribbon-loadbalancer:2.2.0") {
        exclude(group = "com.google.guava", module = "guava")
    }

    implementation("com.netflix.ribbon:ribbon-transport:2.2.0") {
        exclude(group = "com.google.guava", module = "guava")
    }
}