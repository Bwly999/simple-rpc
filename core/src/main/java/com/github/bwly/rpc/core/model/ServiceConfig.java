package com.github.bwly.rpc.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceConfig {
    private String group = "default";

    private String version = "1.0";

    private Object service;

    public ServiceConfig(Object service) {
        this.service = service;
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

    public String getRpcServiceName() {
        return this.getGroup() + ":" + this.getServiceName() + ":" + this.getVersion();
    }
}
