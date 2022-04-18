package com.github.bwly.rpc.core.codec;

import com.github.bwly.rpc.core.model.RpcRequest;
import com.github.bwly.rpc.core.model.RpcResponse;
import com.github.bwly.rpc.core.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class RpcEncoder extends MessageToByteEncoder {
    private Serializer serializer;

    private Class<?> genericClass;

    public RpcEncoder(Serializer serializer, Class<?> genericClass) {
        this.serializer = serializer;
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf) throws Exception {
        byte[] bytes = null;
        if (genericClass.equals(RpcRequest.class)) {
            RpcRequest rpcRequest = (RpcRequest) obj;
            bytes = serializer.serialize(rpcRequest);

        } else if (genericClass.equals(RpcResponse.class)) {
            RpcResponse rpcResponse = (RpcResponse) obj;
            bytes = serializer.serialize(rpcResponse);

        } else {
            log.error("RpcEncoder encode error, genericClass is not RpcRequest or RpcResponse");
            return;
        }

        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
