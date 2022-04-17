package com.github.bwly.rpc.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceConfig {
    private String group = "";

    private String version = "";

    private Object service;

    public String getServiceName() {
        return this.getGroup() + this.getVersion();
    }
}
