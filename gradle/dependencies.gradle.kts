val version = mapOf(
    "spring-boot" to "2.2.1.RELEASE",
    "spring-dependency-management" to "1.0.8.RELEASE",
    "spring-security-oauth2" to "2.3.3.RELEASE",

    "ribbon" to "2.2.0",
    "grpc" to "1.25.0",
    "protobuf" to "3.9.2",
    "protobufJavaFormat" to "1.4",
    "nettyNative" to "2.0.27.Final",
    "nettyHandler" to "4.1.43.Final",

    "mysqlConnector" to "5.1.48",
    "influxdb" to "2.16",
    "jedis" to "3.1.0",
    "etcd4j" to "2.17.0",

    "commons-pool2" to "2.5.0",
    "commons-lang3" to "3.7",
    "lombok" to "1.18.8",
    "servlet" to "3.1.0",
    "validator" to "5.4.3.Final",
    "guava" to "28.1-jre",
    "fastjson" to "1.2.62",
    "junit5" to "5.5.2"
)
val lib = mapOf(
    "spring-boot-starter" to "org.springframework.boot:spring-boot-starter",
    "spring-boot-starter-test" to "org.springframework.boot:spring-boot-starter-test",
    "spring-boot-starter-data-mongodb" to "org.springframework.boot:spring-boot-starter-data-mongodb",
    "spring-security-oauth2" to "org.springframework.security.oauth:spring-security-oauth2:${version["spring-security-oauth2"]}",

    "slf4j-api" to "org.slf4j:slf4j-api",
    "logback-core" to "ch.qos.logback:logback-core",
    "logback-classic" to "ch.qos.logback:logback-classic",

    "junit-jupiter-api" to "org.junit.jupiter:junit-jupiter-api:${version["junit5"]}",
    "junit-jupiter-engine" to "org.junit.jupiter:junit-jupiter-engine:${version["junit5"]}",

    "netty-handler" to "io.netty:netty-handler:${version["nettyHandler"]}",
    "netty-codec-http2" to "io.netty:netty-codec-http2:${version["nettyHandler"]}",
    "netty-tcnative" to "io.netty:netty-tcnative:${version["nettyNative"]}",
    "netty-tcnative-boringssl-static" to "io.netty:netty-tcnative-boringssl-static:${version["nettyNative"]}",

    "influxdb-java" to "org.influxdb:influxdb-java:${version["influxdb"]}",
    "jedis" to "redis.clients:jedis:${version["jedis"]}",
    "etcd4j" to "org.mousio:etcd4j:${version["etcd4j"]}",

    "commons-pool2" to "org.apache.commons:commons-pool2:${version["commons-pool2"]}",
    "commons-lang3" to "org.apache.commons:commons-lang3:${version["commons-lang3"]}",

    "ribbon" to "com.netflix.ribbon:ribbon:${version["ribbon"]}",
    "ribbon-core" to "com.netflix.ribbon:ribbon-core:${version["ribbon"]}",
    "ribbon-transport" to "com.netflix.ribbon:ribbon-transport:${version["ribbon"]}",
    "ribbon-loadbalancer" to "com.netflix.ribbon:ribbon-loadbalancer:${version["ribbon"]}",

    "protoc" to "com.google.protobuf:protoc:${version["protobuf"]}",
    "protobuf-java" to "com.google.protobuf:protobuf-java:${version["protobuf"]}",
    "protoc-gen-grpc-java" to "io.grpc:protoc-gen-grpc-java:${version["grpc"]}",
    "protobuf-java-format" to "com.googlecode.protobuf-java-format:protobuf-java-format:${version["protobufJavaFormat"]}",

    "grpc-auth" to "io.grpc:grpc-auth:${version["grpc"]}",
    "grpc-stub" to "io.grpc:grpc-stub:${version["grpc"]}",
    "grpc-netty" to "io.grpc:grpc-netty:${version["grpc"]}",
    "grpc-protobuf" to "io.grpc:grpc-protobuf:${version["grpc"]}",

    "lombok" to "org.projectlombok:lombok:${version["lombok"]}",
    "fastjson" to "com.alibaba:fastjson:${version["fastjson"]}",
    "guava" to "com.google.guava:guava:${version["guava"]}"
)

extra["lib"] = lib
