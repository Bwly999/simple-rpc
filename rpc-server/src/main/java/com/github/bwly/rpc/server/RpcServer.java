package com.github.bwly.rpc.server;

public abstract class RpcServer {
    /**
     * 启动服务
     */
    public abstract void start();

    /**
     * 停止服务
     */
    public abstract void stop();
}
