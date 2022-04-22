package com.github.bwly.rpc.client.transport.netty.handler;

import com.github.bwly.rpc.core.model.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private PendingRpc pendingRpc = PendingRpc.getInstance();
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        log.info("receive response: {}", rpcResponse);
        pendingRpc.complete(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("rpc server error", cause);
        ctx.close();
    }
}
