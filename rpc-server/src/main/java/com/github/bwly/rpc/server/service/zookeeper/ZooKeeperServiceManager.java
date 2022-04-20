package com.github.bwly.rpc.server.service.zookeeper;

import com.github.bwly.rpc.server.service.ServiceManager;
import org.springframework.beans.factory.InitializingBean;

public class ZooKeeperServiceManager implements ServiceManager, InitializingBean {
    @Override
    public void addService(String serviceName, Object service) {

    }

    @Override
    public void removeService(String serviceName) {

    }

    @Override
    public Object getService(String serviceName) {
        return null;
    }

    @Override
    public void publishService(String serviceName, String host, int port) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        
    }
}
