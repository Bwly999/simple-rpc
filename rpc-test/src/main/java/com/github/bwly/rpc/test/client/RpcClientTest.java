package com.github.bwly.rpc.test.client;

import com.github.bwly.rpc.client.proxy.RpcProxy;
import com.github.bwly.rpc.client.transport.RpcTransport;
import com.github.bwly.rpc.client.transport.netty.NettyRpcClient;
import com.github.bwly.rpc.core.model.ServiceConfig;
import com.github.bwly.rpc.test.api.Hello;

public class RpcClientTest {
    public static void main(String[] args) {
        RpcTransport rpcTransport = new NettyRpcClient();
        ServiceConfig serviceConfig = new ServiceConfig();
        RpcProxy rpcProxy = new RpcProxy(rpcTransport, serviceConfig);
        Hello proxy = rpcProxy.getProxy(Hello.class);
        System.out.println("------------------");
        System.out.println(proxy.hello("test"));
        System.out.println(proxy.hello("test"));
        System.out.println(proxy.hello("test"));
    }
}
