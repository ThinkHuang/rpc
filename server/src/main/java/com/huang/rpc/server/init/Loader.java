package com.huang.rpc.server.init;

/**
 * 抽象加载器
 * @author huangyejun
 *
 */
public interface Loader
{
    /**
     * 加载指定目录的配置文件，并存入缓存中
     * @param properties
     */
    void load(String properties);
}
