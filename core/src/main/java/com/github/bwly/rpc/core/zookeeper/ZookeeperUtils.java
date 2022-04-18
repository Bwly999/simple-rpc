package com.github.bwly.rpc.core.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration

public class ZookeeperUtils implements InitializingBean {
    public static final String ZK_PATH = "/bwly" ;
    public static final String ZK_NODE = "bwly" ;
    public static final String ZK_NODE_PATH = ZK_PATH + "/" + ZK_NODE ;
    public static final String ZK_NODE_DATA = "bwly" ;
    public static final String ZK_NODE_DATA_PATH = ZK_NODE_PATH + "/" + ZK_NODE_DATA ;

    private CuratorFramework zkClient;

    @Autowired
    private ZookeeperProperties properties;

    @Override
    public void afterPropertiesSet() throws Exception {
        zkClient = CuratorFrameworkFactory.builder()
                .namespace(properties.getNamespace())
                .connectString(properties.getConnectString())
                .retryPolicy(new ExponentialBackoffRetry(properties.getBaseSleepTime(), properties.getMaxRetries()))
                .build();

        zkClient.start();
    }
}
