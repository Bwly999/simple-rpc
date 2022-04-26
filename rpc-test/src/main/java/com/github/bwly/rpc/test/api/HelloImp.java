package com.github.bwly.rpc.test.api;

public class HelloImp implements Hello {
    @Override
    public String hello(String name) {
        return "hello: " + name;
    }
}
