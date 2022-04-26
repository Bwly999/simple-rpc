package com.github.bwly.rpc.client.proxy;

import com.github.bwly.rpc.client.transport.RpcTransport;
import com.github.bwly.rpc.client.transport.netty.NettyRpcClient;
import com.github.bwly.rpc.core.model.RpcRequest;
import com.github.bwly.rpc.core.model.RpcResponse;
import com.github.bwly.rpc.core.model.ServiceConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class RpcProxy implements InvocationHandler {
    private final RpcTransport rpcTransport;
    private final ServiceConfig serviceConfig;

    public RpcProxy(RpcTransport rpcTransport, ServiceConfig serviceConfig) {
        this.rpcTransport = rpcTransport;
        this.serviceConfig = serviceConfig;
    }

    /**
     * get the proxy object
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * This method is actually called when you use a proxy object to call a method.
     * The proxy object is the object you get through the getProxy method.
     */
    @SneakyThrows
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .className(method.getDeclaringClass().getName())
//                .className(method.getDeclaringClass().getInterfaces()[0].getCanonicalName())
                .parameterTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(serviceConfig.getGroup())
                .version(serviceConfig.getVersion())
                .build();
        RpcResponse rpcResponse = null;
        try {
            if (rpcTransport instanceof NettyRpcClient) {
                CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) rpcTransport.sendRpcRequest(rpcRequest);
                rpcResponse = completableFuture.get();
            }
        } catch (Exception e) {
            log.error("send rpc request error", e);
            return null;
        }
        return rpcResponse.getData();
    }
}
