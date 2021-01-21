package com.huang.rpc.server.init;

import java.util.Map;

/**
 * 定义资源接口
 */
public interface Resource
{
    /**
     * 获取映射文件内容
     * @return
     */
    Map<String, String> getPropertyMap();
}
