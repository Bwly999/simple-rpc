package com.github.bwly.rpc.server.registry;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    void registerService(String serviceName, InetSocketAddress inetSocketAddress);
}
