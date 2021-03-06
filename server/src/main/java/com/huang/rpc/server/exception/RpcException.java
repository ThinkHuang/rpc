package com.huang.rpc.server.exception;

public final class RpcException extends RuntimeException {

    private static final long serialVersionUID = 6695561665389720970L;
    
    private final int code;

    public RpcException(final int code) {
        super();
        this.code = code;
    }

    public RpcException(final int code, final String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public RpcException(final int code, final String message) {
        super(message);
        this.code = code;
    }

    public RpcException(final int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
