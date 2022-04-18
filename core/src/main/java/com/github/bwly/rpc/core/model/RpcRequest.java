package com.github.bwly.rpc.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {
    private String requestId;
    private Object[] parameters;
    private String methodName;
    private Class<?>[] parameterTypes;
    private String className;
    private String version;
    private String serviceName;
    private String methodDesc;
}
