package com.github.bwly.rpc.service;

public interface ServiceManager {
    void addService(String serviceName, Object service);

    void removeService(String serviceName);

    Object getService(String serviceName);

    void publishService(String serviceName, String host, int port);
}
