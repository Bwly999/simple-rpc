package com.github.bwly.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperUtils {
    private String host;
    public static final String ZK_HOST = "" ;
    public static final int ZK_PORT = 2181 ;
    public static final String ZK_PATH = "/bwly" ;
    public static final String ZK_NODE = "bwly" ;
    public static final String ZK_NODE_PATH = ZK_PATH + "/" + ZK_NODE ;
    public static final String ZK_NODE_DATA = "bwly" ;
    public static final String ZK_NODE_DATA_PATH = ZK_NODE_PATH + "/" + ZK_NODE_DATA ;
}
