package com.github.bwly.rpc.core.model;

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
}
