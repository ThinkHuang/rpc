package com.huang.rpc.server;

import java.io.Serializable;
import java.util.Map;

public final class URL implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = -993639071240393516L;

    /**
     * 协议
     */
    private final String protocol;
    
    /**
     * 主机
     */
    private final String host;
    
    /**
     * 端口
     */
    private final int port;
    
    /**
     * 资源路径
     */
    private final String path;
    
    /**
     * 参数映射
     */
    private final Map<String, String> parameters;

    public URL(String protocol, String host, int port, String path, Map<String, String> parameters) {
        super();
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.path = path;
        this.parameters = parameters;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
    
    
}
