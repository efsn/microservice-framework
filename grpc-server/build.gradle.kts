val lib = ext["lib"] as Map<String, String>

dependencies {
    api(project(":grpc-etcd"))
    api(lib.getValue("netty-tcnative-boringssl-static"))
    api(lib.getValue("grpc-netty"))
    api(lib.getValue("grpc-protobuf"))
    api(lib.getValue("grpc-stub"))
    api(lib.getValue("fastjson"))
    api(lib.getValue("influxdb-java"))
    api(lib.getValue("guava"))
    api(lib.getValue("jedis"))
    api(lib.getValue("spring-boot-starter-data-mongodb"))
    api(lib.getValue("spring-security-oauth2"))
    // api("cn.elmi.components:component-cache-spring-boot-starter:1.0-SNAPSHOT")

    api(fileTree("dir" to "lib", "include" to "*.jar"))
}