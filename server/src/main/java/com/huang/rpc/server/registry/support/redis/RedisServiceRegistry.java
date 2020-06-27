package com.huang.rpc.server.registry.support.redis;

import java.net.URL;

import com.huang.rpc.api.request.RequestBody;
import com.huang.rpc.server.handler.Invocation;
import com.huang.rpc.server.registry.AbstractServiceRegistry;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisServiceRegistry extends AbstractServiceRegistry
{
    // redis默认端口
    private static final int DEFAULT_REDIS_PORT = 6379;
    // redis默认主机
    private static final String DEFAULT_REDIS_HOST = "127.0.0.1";
    
    private static Jedis jedis;
    
    public RedisServiceRegistry() {
        // 配置jedispool连接池相关信息
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool(poolConfig, DEFAULT_REDIS_HOST, DEFAULT_REDIS_PORT);
        try 
        {
            //从连接池获取jedis对象
            jedis = jedisPool.getResource();
        }
        finally
        {
            if (jedis != null) {
                //这里使用的close不代表关闭连接，指的是归还资源
                jedisPool.close();
            }
        }
    }
    
    
    
    public static Jedis getJedis()
    {
        return jedis;
    }

    public static void main(String[] args)
    {
        new RedisServiceRegistry();
        jedis.set("java", "good");
        System.out.println(jedis.get("java"));
        RequestBody body = new RequestBody();
        body.setMethodName("test");
        jedis.set("test".getBytes(), SerializeUtil.serialize(body));
        byte[] result = jedis.get("test".getBytes());
        System.out.println(SerializeUtil.unserialize(result));
    }
    
    @Override
    public void doPublish(String basePackage)
    {
        
    }
    
    @Override
    public void registr(URL url)
    {
        
    }

    @Override
    protected Invocation doGetInvocation(boolean singleton, RequestBody body) throws ReflectiveOperationException {
        return null;
    }
}
