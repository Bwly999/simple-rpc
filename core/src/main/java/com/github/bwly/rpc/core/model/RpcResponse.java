package com.github.bwly.rpc.core.model;

import com.github.bwly.rpc.core.constants.RcpResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {
    private String requestId;

    private int code;

    private String message;

    private Object data;

    public void setRpcStatus(RcpResponseCode rpcCode) {
        this.code = rpcCode.getCode();
        this.message = rpcCode.getMessage();
    }

    public static RpcResponse Success(String requestId, Object data) {
        return new RpcResponse(requestId, RcpResponseCode.SUCCESS.getCode(), RcpResponseCode.SUCCESS.getMessage(), data);
    }

    public static RpcResponse Failure(String requestId, RcpResponseCode rpcCode) {
        return new RpcResponse(requestId, rpcCode.getCode(), rpcCode.getMessage(), null);
    }
}
