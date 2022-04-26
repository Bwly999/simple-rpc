package com.github.bwly.rpc.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest {
    private String requestId;
    private Object[] parameters;
    private String methodName;
    private Class<?>[] parameterTypes;
    private String className;
    private String group;
    private String version;

    public String getServiceName() {
        return this.group + ":" + this.className + ":" + this.version;
    }
}
