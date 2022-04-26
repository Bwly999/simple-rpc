package com.github.bwly.rpc.client.transport.netty;

import com.github.bwly.rpc.client.discovery.ServiceDiscovery;
import com.github.bwly.rpc.client.discovery.ZookeeperDiscovery;
import com.github.bwly.rpc.client.transport.netty.handler.NettyRpcClientHandler;
import com.github.bwly.rpc.client.transport.netty.handler.PendingRpc;
import com.github.bwly.rpc.client.transport.RpcTransport;
import com.github.bwly.rpc.core.codec.RpcDecoder;
import com.github.bwly.rpc.core.codec.RpcEncoder;
import com.github.bwly.rpc.core.model.RpcRequest;
import com.github.bwly.rpc.core.model.RpcResponse;
import com.github.bwly.rpc.core.serialize.ProtobufSerializer;
import com.github.bwly.rpc.core.serialize.Serializer;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Slf4j
public class NettyRpcClient implements RpcTransport {
    private final ServiceDiscovery serviceDiscovery;

    private final EventLoopGroup eventLoopGroup;

    private final Bootstrap bootstrap;

    private final ChannelCache channelCache;

    private final Serializer serializer;

    private final PendingRpc pendingRpc;


    public NettyRpcClient(ServiceDiscovery serviceDiscovery) {
        this.channelCache = new ChannelCache();
        this.serviceDiscovery = serviceDiscovery;
        this.serializer = new ProtobufSerializer();
        this.pendingRpc = PendingRpc.getInstance();
        this.eventLoopGroup = new NioEventLoopGroup(5);
        this.bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                //  The timeout period of the connection.
                //  If this time is exceeded or the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        // If no data is sent to the server within 15 seconds, a heartbeat request is sent
//                        p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcEncoder(serializer, RpcRequest.class));
                        p.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
                        p.addLast(new RpcDecoder(serializer, RpcResponse.class));
                        p.addLast(new NettyRpcClientHandler());
                    }
                });
    }

    public NettyRpcClient() {
        this(new ZookeeperDiscovery());
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // build return value
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        // get server address
        InetSocketAddress inetSocketAddress = serviceDiscovery.findService(rpcRequest);
        // get  server address related channel
        Channel channel = getChannel(inetSocketAddress);
        if (channel != null && channel.isActive()) {
            // put unprocessed request
            pendingRpc.put(rpcRequest.getRequestId(), resultFuture);
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcRequest);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }

        return resultFuture;
    }

    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelCache.get(inetSocketAddress);
        if (channel == null) {
            try {
                channel = doConnect(inetSocketAddress);
            } catch (Exception e) {
                log.error("Connect failed:", e);
                return null;
            }
            channelCache.put(inetSocketAddress, channel);
        }
        return channel;
    }

    static class ChannelCache {
        Cache<InetSocketAddress, Channel> cache = CacheBuilder.newBuilder().build();
        public Channel get(InetSocketAddress inetSocketAddress) {
            return cache.getIfPresent(inetSocketAddress);
        }
        public void put(InetSocketAddress address, Channel channel) {
            cache.put(address, channel);
        }
        public void remove(InetSocketAddress address) {
            cache.invalidate(address);
        }
    }
}
