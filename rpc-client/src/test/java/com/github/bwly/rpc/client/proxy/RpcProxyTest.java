package com.github.bwly.rpc.client.proxy;


import com.github.bwly.rpc.client.proxy.api.Hello;
import com.github.bwly.rpc.client.transport.RpcTransport;
import com.github.bwly.rpc.client.transport.netty.NettyRpcClient;
import com.github.bwly.rpc.core.model.ServiceConfig;

class RpcProxyTest {
    public static void main(String[] args) {
        RpcTransport rpcTransport = new NettyRpcClient();
        ServiceConfig serviceConfig = new ServiceConfig();
        RpcProxy rpcProxy = new RpcProxy(rpcTransport, serviceConfig);
        Hello proxy = rpcProxy.getProxy(Hello.class);
        System.out.println(proxy.hello("test"));
    }
}