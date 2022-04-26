package com.github.bwly.rpc.server.config;

import com.github.bwly.rpc.core.zookeeper.ZookeeperUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Slf4j
public class CustomShutdownHook {
    private int port;

    public CustomShutdownHook(int port) {
        this.port = port;
    }

    public void mount() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), port);
                ZookeeperUtils.clearRegistry(inetSocketAddress);
            } catch (UnknownHostException ignored) {
            }
        }));
    }
}