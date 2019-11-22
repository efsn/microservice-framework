dependencies {
    val ext = project.extra

    api(project(":grpc-etcd"))
    api("io.netty:netty-tcnative-boringssl-static:${ext["nettyNativeVersion"]}")
    api("io.grpc:grpc-netty:${ext["grpcVersion"]}")
    api("io.grpc:grpc-protobuf:${ext["grpcVersion"]}")
    api("io.grpc:grpc-stub:${ext["grpcVersion"]}")
    api("com.alibaba:fastjson:${ext["fastjsonVersion"]}")
    api("org.influxdb:influxdb-java:${ext["influxdbVersion"]}")
    api("com.google.guava:guava:${ext["guavaVersion"]}")
    api("redis.clients:jedis:${ext["jedisVersion"]}")
    api("org.springframework.boot:spring-boot-starter-data-mongodb")
    api("org.springframework.security.oauth:spring-security-oauth2:2.3.3.RELEASE")
    // api("cn.elmi.components:component-cache-spring-boot-starter:1.0-SNAPSHOT")

    api(fileTree("dir" to "lib", "include" to "*.jar"))
}