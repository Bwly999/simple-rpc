package com.github.bwly.rpc.test.server;

import com.github.bwly.rpc.core.model.ServiceConfig;
import com.github.bwly.rpc.server.RpcServer;
import com.github.bwly.rpc.server.netty.NettyRpcServer;
import com.github.bwly.rpc.test.api.HelloImp;

public class RpcServerTest {
    public static void main(String[] args) {
        RpcServer rpcServer = new NettyRpcServer(12345);
        rpcServer.registerService(new ServiceConfig(new HelloImp()));
        rpcServer.start();
    }
}
