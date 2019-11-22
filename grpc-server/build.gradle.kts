dependencies {
    val ext = project.extra

    implementation(project(":grpc-etcd"))
    implementation("io.netty:netty-tcnative-boringssl-static:${ext["nettyNativeVersion"]}")
    implementation("io.grpc:grpc-netty:${ext["grpcVersion"]}")
    implementation("io.grpc:grpc-protobuf:${ext["grpcVersion"]}")
    implementation("io.grpc:grpc-stub:${ext["grpcVersion"]}")
    implementation("com.alibaba:fastjson:${ext["fastjsonVersion"]}")
    implementation("org.influxdb:influxdb-java:${ext["influxdbVersion"]}")
    implementation("com.google.guava:guava:${ext["guavaVersion"]}")
    implementation("redis.clients:jedis:${ext["jedisVersion"]}")
    
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.security.oauth:spring-security-oauth2:2.3.3.RELEASE")
    // implementation("cn.elmi.components:component-cache-spring-boot-starter:1.0-SNAPSHOT")

    implementation(fileTree("dir" to "lib", "include" to "*.jar"))

}