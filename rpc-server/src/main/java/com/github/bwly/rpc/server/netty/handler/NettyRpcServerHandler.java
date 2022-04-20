package com.github.bwly.rpc.server.netty.handler;

import com.github.bwly.rpc.core.constants.RcpResponseCode;
import com.github.bwly.rpc.core.model.RpcRequest;
import com.github.bwly.rpc.core.model.RpcResponse;
import com.github.bwly.rpc.core.utils.ThreadFactoryUtils;
import com.github.bwly.rpc.server.handler.RequestHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private RequestHandler requestHandler;

    public NettyRpcServerHandler(RequestHandler requestHandler) {
        Objects.requireNonNull(requestHandler, "requestHandler must not be null");

        this.requestHandler = requestHandler;
    }

    private static ThreadPoolExecutor getDefaultThreadPool() {
        int coreSize = Runtime.getRuntime().availableProcessors() * 2;
        ThreadFactory threadFactory = ThreadFactoryUtils.createThreadFactory("rpc-server-thread");
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(coreSize, coreSize * 2, 60
                , TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);

        return threadPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        log.info("receive request: {}", rpcRequest);
        RpcResponse rpcResponse = null;
        try {
            Object result = requestHandler.handle(rpcRequest);
            rpcResponse = RpcResponse.Success(rpcRequest.getRequestId(), result);

        } catch (Exception e) {
            log.error("handle request error", e);
            rpcResponse = RpcResponse.Failure(rpcRequest.getRequestId(), RcpResponseCode.SERVICE_INTERNAL_ERROR);
        }

        // 发送失败时，关闭连接
        channelHandlerContext.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("rpc server error", cause);
        ctx.close();
    }
}
