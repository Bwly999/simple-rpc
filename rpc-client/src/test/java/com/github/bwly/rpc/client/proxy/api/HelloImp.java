package com.github.bwly.rpc.client.proxy.api;

public class HelloImp implements Hello {
    @Override
    public String hello(String name) {
        return "hello: " + name;
    }
}
