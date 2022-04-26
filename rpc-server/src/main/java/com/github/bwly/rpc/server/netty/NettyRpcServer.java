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
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 当客户端第一次进行请求的时候才会进行初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 30 秒之内没有收到客户端请求的话就关闭连接
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new RpcDecoder(serializer, RpcRequest.class));
                            p.addLast(new RpcEncoder(serializer, RpcResponse.class));
                            p.addLast(serviceHandlerGroup, new NettyRpcServerHandler(new RequestHandler(serviceManager)));
                        }
                    });

            InetAddress host = InetAddress.getLocalHost();
            // 绑定端口，同步等待绑定成功
            ChannelFuture f = serverBootstrap.bind(host, port).sync();
            // 等待服务端监听端口关闭
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
