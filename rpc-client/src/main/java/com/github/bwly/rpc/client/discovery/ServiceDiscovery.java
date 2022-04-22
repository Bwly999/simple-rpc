package com.github.bwly.rpc.client.discovery;

import com.github.bwly.rpc.core.model.RpcRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    InetSocketAddress findService(RpcRequest rpcRequest);
}
