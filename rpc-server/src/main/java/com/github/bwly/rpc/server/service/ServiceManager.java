package com.github.bwly.rpc.server.service;

import com.github.bwly.rpc.core.exception.NoSuchServiceException;
import com.github.bwly.rpc.core.model.ServiceConfig;
import com.github.bwly.rpc.server.registry.ServiceRegister;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class ServiceManager {
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private ServiceRegister serviceRegister;

    private int port;

    public ServiceManager(ServiceRegister serviceRegister, int port) {
        this.serviceRegister = serviceRegister;
        this.port = port;
    }

    public void setServiceRegistry(ServiceRegister serviceRegister) {
        this.serviceRegister = serviceRegister;
    }

    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (null == service) {
            throw new NoSuchServiceException("No such service: " + serviceName);
        }
        return service;
    }

    public void publishService(ServiceConfig serviceConfig) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            String rpcServiceName = serviceConfig.getRpcServiceName();

            serviceRegister.registerService(rpcServiceName, new InetSocketAddress(host, port));

            if (serviceMap.containsKey(rpcServiceName)) {
                return;
            }

            serviceMap.put(rpcServiceName, serviceConfig.getService());
            log.info("Add service: {} and interfaces:{}", rpcServiceName, serviceConfig.getService().getClass().getInterfaces());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
