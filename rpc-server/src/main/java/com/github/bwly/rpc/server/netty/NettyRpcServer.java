package com.github.bwly.rpc.server.netty;

import com.github.bwly.rpc.core.codec.RpcDecoder;
import com.github.bwly.rpc.core.codec.RpcEncoder;
import com.github.bwly.rpc.core.model.RpcRequest;
import com.github.bwly.rpc.core.model.RpcResponse;
import com.github.bwly.rpc.core.serialize.ProtobufSerializer;
import com.github.bwly.rpc.core.serialize.Serializer;
import com.github.bwly.rpc.core.utils.ThreadFactoryUtils;
import com.github.bwly.rpc.server.RpcServer;
import com.github.bwly.rpc.server.config.CustomShutdownHook;
import com.github.bwly.rpc.server.handler.RequestHandler;
import com.github.bwly.rpc.server.netty.handler.NettyRpcServerHandler;
import com.github.bwly.rpc.server.registry.ServiceRegister;
import com.github.bwly.rpc.server.registry.ZookeeperServiceRegister;
import com.github.bwly.rpc.core.model.ServiceConfig;
import com.github.bwly.rpc.server.service.ServiceManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
public class NettyRpcServer extends RpcServer {
    private int port = 11451;

    private Serializer serializer = new ProtobufSerializer();

    private ServiceRegister serviceRegister = new ZookeeperServiceRegister();

    private ServiceManager serviceManager;

    private ServiceManager getServiceManager() {
        return new ServiceManager(serviceRegister, port);
    }

    public NettyRpcServer() {
        this.serviceManager = new ServiceManager(serviceRegister, port);
    }

    public NettyRpcServer(int port) {
        this.port = port;
        this.serviceManager = new ServiceManager(serviceRegister, port);
    }

    public NettyRpcServer(int port, Serializer serializer, ServiceRegister serviceRegister) {
        this.port = port;
        this.serializer = serializer;
        this.serviceRegister = serviceRegister;
        this.serviceManager = new ServiceManager(serviceRegister, port);
    }

    @Override
    public void start() {
        CustomShutdownHook shutdownHook = new CustomShutdownHook(port);
        shutdownHook.mount();
        log.info("NettyRpcServer start");
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        int cpuNum = Runtime.getRuntime().availableProcessors();
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(cpuNum * 2,
                ThreadFactoryUtils.createThreadFactory("rpc-service-handler-thread"));

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP??????????????? Nagle ????????????????????????????????????????????????????????????????????????????????????TCP_NODELAY ??????????????????????????????????????? Nagle ?????????
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // ???????????? TCP ??????????????????
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //????????????????????????????????????????????????????????????????????????????????????,????????????????????????????????????????????????????????????????????????????????????????????????
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // ???????????????????????????????????????????????????????????????
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 30 ?????????????????????????????????????????????????????????
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new RpcDecoder(serializer, RpcRequest.class));
                            p.addLast(new RpcEncoder(serializer, RpcResponse.class));
                            p.addLast(serviceHandlerGroup, new NettyRpcServerHandler(new RequestHandler(serviceManager)));
                        }
                    });

            InetAddress host = InetAddress.getLocalHost();
            // ???????????????????????????????????????
            ChannelFuture f = serverBootstrap.bind(host, port).sync();
            // ?????????????????????????????????
            f.channel().closeFuture().sync();
        } catch (UnknownHostException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }


    @Override
    public void stop() {

    }

    @Override
    public void registerService(ServiceConfig serviceConfig) {
        serviceManager.publishService(serviceConfig);
    }
}
