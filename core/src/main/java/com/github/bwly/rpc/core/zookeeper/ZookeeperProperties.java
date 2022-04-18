package com.github.bwly.rpc.core.zookeeper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zookeeper")
@Data
public class ZookeeperProperties {
    private String host = "127.0.0.1";
    private int port = 2181;
    private int baseSleepTime = 1000;
    private int maxRetries = 3;
    private String namespace = "simple-rpc";

    public String getConnectString() {
        return host + ":" + port;
    }
}
