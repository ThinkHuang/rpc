package com.huang.rpc.server.config;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author huangyejun
 *
 */
public class RedisConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    private RedisConfig() {}
    
    // Redis服务器IP
    private static final String ADDRESS = "localhost";
    
    // Redis的端口号
    private static final int PORT = 6379;

    // 访问密码,若你的redis服务器没有设置密码，就不需要用密码去连接
    // private static String AUTH = "123456";

    // 可用连接实例的最大数目，默认值为8；
    private static final int MAX_TOTAL = 512;

    // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static final int MAX_IDLE = 50;
    
    // 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。
    private static final int MAX_WAIT = -1;

    private static final int TIMEOUT = 100000;

    // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static final boolean TEST_ON_BORROW = true;
    
    private static JedisPool jedisPool = null;
    
    private static List<JedisShardInfo> shards = null;
    
    private static ShardedJedisPool shardedJedisPool = null;
    
    private static Object mutex = new Object();
    
    static {
        // 初始化jedis池
        initJedisPool();
        // 初始化共享池
        initShardedJedisPool();
    }
    
    /**
     * 初始化Redis连接池
     */
    private static void initJedisPool() {
        synchronized (mutex) {
            try {
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxTotal(MAX_TOTAL);
                config.setMaxIdle(MAX_IDLE);
                config.setMaxWaitMillis(MAX_WAIT);
                config.setTestOnBorrow(TEST_ON_BORROW);
                jedisPool = new JedisPool(config, ADDRESS, PORT, TIMEOUT);
            } catch (Exception e) {
                logger.error("initJedisPool出错:{}", e.getMessage(), e);
            }
        }
    }
    
    private static void initShardedJedisPool() {
        synchronized (mutex) {
            try {
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxTotal(MAX_TOTAL);
                config.setMaxIdle(MAX_IDLE);
                config.setMaxWaitMillis(MAX_WAIT);
                config.setTestOnBorrow(TEST_ON_BORROW);
                JedisShardInfo info = new JedisShardInfo(ADDRESS, PORT);
                info.setSoTimeout(TIMEOUT);
                shards = Arrays.asList(info);
                shardedJedisPool = new ShardedJedisPool(config, shards);
            } catch (Exception e) {
                logger.error("initShardedJedisPool出错:{}", e.getMessage(), e);
            }
        }
    }
    
    /**
     * 释放shardedJedis资源
     * 
     * @param jedis
     */
    public static void returnShardedResource(final ShardedJedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
    
    /**
     * 获取Jedis实例
     * 
     * @return
     */
    public static Jedis getJedis() {
        try {
            if (jedisPool == null) {
                initJedisPool();
            }
            return jedisPool.getResource();
        } catch (Exception e) {
            logger.error("getJedis出错:{}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 释放jedis资源
     * 
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
    
}
