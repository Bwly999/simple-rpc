package com.github.bwly.rpc.core.constants;

public enum RcpResponseCode {
    /**
     * rpc 回应状态码
     */
    SUCCESS(0, "成功"),
    PARAM_ERROR(2, "参数错误"),
    TIMEOUT(3, "超时"),
    SERVICE_NOT_FOUND(4, "服务未找到"),
    SERVICE_NOT_AVAILABLE(5, "服务不可用"),
    SERVICE_ERROR(6, "服务异常"),
    SERVICE_INTERNAL_ERROR(8, "服务器内部错误");

    private int code;
    private String message;

    RcpResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
