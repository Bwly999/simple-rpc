package com.github.bwly.rpc.server;

import com.github.bwly.rpc.core.model.ServiceConfig;

public abstract class RpcServer {
    /**
     * 启动服务
     */
    public abstract void start();

    /**
     * 停止服务
     */
    public abstract void stop();

    public abstract void registerService(ServiceConfig serviceConfig);
}
