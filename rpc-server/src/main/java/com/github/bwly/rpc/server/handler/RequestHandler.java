package com.github.bwly.rpc.server.handler;

import com.github.bwly.rpc.core.exception.NoSuchServiceException;
import com.github.bwly.rpc.core.model.RpcRequest;
import com.github.bwly.rpc.server.service.ServiceManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
@AllArgsConstructor
public class RequestHandler {
    private ServiceManager serviceManager;

    public Object handle(RpcRequest request) {
        log.info("request: {}", request);
        String className = request.getClassName();
        String version = request.getVersion();
        Object serviceBean = serviceManager.getService(request.getServiceName());

        if (serviceBean == null) {
            log.error("Can not find service implement with interface name: {} and version: {}", className, version);
            throw new NoSuchServiceException();
        }

        return invoke(request, serviceBean);
    }

    private Object invoke(RpcRequest request, Object serviceBean) {
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        Class<?> serviceClass = serviceBean.getClass();

        log.debug(serviceClass.getName());
        log.debug(methodName);
        for (int i = 0; i < parameterTypes.length; ++i) {
            log.debug(parameterTypes[i].getName());
        }
        for (int i = 0; i < parameters.length; ++i) {
            log.debug(parameters[i].toString());
        }

        try {
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            Object result = method.invoke(serviceBean, parameters);
            log.info("service:[{}] successful invoke method:[{}]", request.getMethodName(), request.getMethodName());
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NoSuchServiceException();
        }
    }
}
