package com.github.bwly.rpc.client;

import com.github.bwly.rpc.client.discovery.ServiceDiscovery;
import com.github.bwly.rpc.client.discovery.ZookeeperDiscovery;
import com.github.bwly.rpc.core.model.RpcRequest;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;


@Builder
@Slf4j
public class NettyRpcClient {
    @Builder.Default
    private ServiceDiscovery serviceDiscovery = new ZookeeperDiscovery();

    public NettyRpcClient() {

    }

    public Object sendRpcRequest(RpcRequest rpcRequest) {

    }
}
