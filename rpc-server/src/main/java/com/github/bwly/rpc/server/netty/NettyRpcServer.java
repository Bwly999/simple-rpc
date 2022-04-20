package com.github.bwly.rpc.server.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServer {
    private int port;

    public NettyRpcServer(int port) {
        this.port = port;
    }

    public void start() {
        log.info("NettyRpcServer start");
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
    }
}
