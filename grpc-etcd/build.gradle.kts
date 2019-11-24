val lib = ext["lib"] as Map<String, String>

dependencies {
    api(lib.getValue("netty-handler"))
    api(lib.getValue("etcd4j")) {
        exclude(group = "io.netty", module = "netty-handler")
        exclude(group = "io.netty", module = "netty-common")
    }
}

