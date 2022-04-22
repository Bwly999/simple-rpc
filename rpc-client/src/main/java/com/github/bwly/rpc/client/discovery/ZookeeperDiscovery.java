package com.github.bwly.rpc.client.discovery;

import com.github.bwly.rpc.core.model.RpcRequest;
import com.github.bwly.rpc.core.zookeeper.ZookeeperUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ZookeeperDiscovery implements ServiceDiscovery{
    @Override
    public InetAddress findService(RpcRequest rpcRequest) {
        String serviceName = rpcRequest.getServiceName();
        List<String> serviceUrlList = ZookeeperUtils.getChildrenNodes(serviceName);
        String targetServiceUrl = serviceUrlList.get(0);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
