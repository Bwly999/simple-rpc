package com.github.bwly.rpc.server.autowire;

import com.github.bwly.rpc.core.annotation.RpcService;
import com.github.bwly.rpc.core.model.ServiceConfig;
import com.github.bwly.rpc.server.service.ServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class RpcBeanPostProcessor implements BeanPostProcessor {
    private ServiceManager serviceManager;

    public RpcBeanPostProcessor(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("RpcBeanPostProcessor.postProcessBeforeInitialization: {}, {}",
                    beanName, RpcService.class.getCanonicalName());
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // build RpcServiceProperties
            ServiceConfig serviceConfig = ServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceManager.publishService(serviceConfig);
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
