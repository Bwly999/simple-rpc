package com.github.bwly.rpc.client.transport;

import com.github.bwly.rpc.core.model.RpcRequest;

public interface RpcTransport {
    Object sendRpcRequest(RpcRequest rpcRequest);
}
