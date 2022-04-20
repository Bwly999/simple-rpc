package com.github.bwly.rpc.server.registry;

import com.github.bwly.rpc.core.zookeeper.ZookeeperUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

public class ZookeeperServiceRegister implements ServiceRegister {

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        String servicePath = ZookeeperUtils.ZK_REGISTER_ROOT_PATH + "/" + serviceName + inetSocketAddress.toString();
        ZookeeperUtils.createPersistentNode(servicePath);
    }
}
