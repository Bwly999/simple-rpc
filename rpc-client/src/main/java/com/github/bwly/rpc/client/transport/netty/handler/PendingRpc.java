package com.github.bwly.rpc.client.transport.netty.handler;

import com.github.bwly.rpc.core.model.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PendingRpc {
    private enum Holder {
        /**
         * 单例对象
         */
        INSTANCE;
        private PendingRpc pendingRpc;
        Holder() {
            pendingRpc = new PendingRpc();
        }
    }
    private Map<String, CompletableFuture<RpcResponse>> pendRpcMap = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        pendRpcMap.put(requestId, future);
    }

    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = pendRpcMap.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }

    public static PendingRpc getInstance() {
        return Holder.INSTANCE.pendingRpc;
    }
}
