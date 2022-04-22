package com.github.bwly.rpc.client.discovery;

import com.github.bwly.rpc.core.model.RpcRequest;

import java.net.InetAddress;

public interface ServiceDiscovery {
    InetAddress findService(RpcRequest rpcRequest);
}
