dependencies {
    val ext = project.extra

    implementation("io.netty:netty-handler:${ext["nettyHandlerVersion"]}")
    implementation("org.mousio:etcd4j:${ext["etcd4jVersion"]}") {
        exclude(group = "io.netty", module = "netty-handler")
        exclude(group = "io.netty", module = "netty-common")
    }
}

