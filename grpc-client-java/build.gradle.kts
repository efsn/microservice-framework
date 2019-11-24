val lib = ext["lib"] as Map<String, String>

dependencies {
    implementation(project(":grpc-etcd"))
    implementation(lib.getValue("grpc-protobuf"))
    api(lib.getValue("grpc-stub"))
    implementation(lib.getValue("grpc-auth"))

    implementation(lib.getValue("grpc-netty")) {
        exclude(group = "io.netty", module = "*")
    }

    implementation(lib.getValue("netty-codec-http2"))
    implementation(lib.getValue("commons-pool2"))
    implementation(lib.getValue("commons-lang3"))
    api(lib.getValue("guava"))
    // implementation("cn.elmi.components:component-cache-spring-boot-starter:1.0-SNAPSHOT")
    implementation(fileTree("dir" to "lib", "include" to "*.jar"))

    implementation(lib.getValue("ribbon")) {
        exclude(group = "io.netty", module = "netty-common")
        exclude(group = "com.google.guava", module = "guava")
    }

    implementation(lib.getValue("ribbon-core")) {
        exclude(group = "com.google.guava", module = "guava")
    }

    implementation(lib.getValue("ribbon-loadbalancer")) {
        exclude(group = "com.google.guava", module = "guava")
    }

    implementation(lib.getValue("ribbon-transport")) {
        exclude(group = "com.google.guava", module = "guava")
    }
}