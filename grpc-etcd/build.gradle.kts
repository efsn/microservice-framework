dependencies {
    val ext = project.extra

    api("io.netty:netty-handler:${ext["nettyHandlerVersion"]}")
    api("org.mousio:etcd4j:${ext["etcd4jVersion"]}") {
        exclude(group = "io.netty", module = "netty-handler")
        exclude(group = "io.netty", module = "netty-common")
    }
}

