package com.github.bwly.rpc.core.exception;

public class NoSuchServiceException extends RuntimeException {
    public NoSuchServiceException(String s) {
        super(s);
    }

    public NoSuchServiceException() {}
}
