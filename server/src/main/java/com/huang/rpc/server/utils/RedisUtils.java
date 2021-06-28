package com.huang.rpc.server.utils;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huang.rpc.server.config.RedisConfig;

import redis.clients.jedis.Jedis;

/**
 * @author huangyejun
 *
 */
public class RedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);
    
    private RedisUtils() {}

    private static final Jedis JEDIS = RedisConfig.getJedis();

    /**
     * 根据key值来获取对应的数据
     * 
     * @param key
     * @return
     */
    public static String getString(final String key) {
        String result = "";
        try (Jedis jedis = JEDIS) {
            if (StringUtils.isEmpty(key)) {
                return result;
            }
            result = jedis.get(key);
        } catch (Exception e) {
            logger.error("get String fail", e);
        }
        return result;
    }

    /**
     * 设置缓存
     * 
     * @param key
     * @param values
     */
    public static void setString(final String key, final String values) {
        try (Jedis jedis = JEDIS) {
            if (StringUtils.isEmpty(key)) {
                return;
            }
            jedis.set(key, values);
        } catch (Exception e) {
            logger.error("set String fail", e);
        }

    }

    /**
     * 根据key值来删除缓存
     * 
     * @param key
     * @return
     */
    public static long delKey(final String key) {
        long result = 0;
        try (Jedis jedis = JEDIS) {
            if (StringUtils.isEmpty(key)) {
                return result;
            }
            result = jedis.del(key);
        } catch (Exception e) {
            logger.error("del key fail", e);
        }
        return result;
    }

    /**
     * 生成key genKey
     * 
     * @param projectId
     * @param module
     * @param unit
     * @return String
     */
    public static String genKey(String projectId, String module, String unit) {
        StringBuilder builder = new StringBuilder();
        builder.append(unit);
        builder.append("_");
        builder.append(module);
        builder.append(projectId);
        return builder.toString();
    }

    /**
     * 设置键值 setString
     * 
     * @param key
     * @param values
     * @param seconds
     */
    public static void setString(final String key, final String values, final int seconds) {
        try (Jedis jedis = JEDIS) {
            if (StringUtils.isEmpty(key)) {
                return;
            }
            jedis.setex(key, seconds, values);
        } catch (Exception e) {
            logger.error("set String and expire fail ", e);
        }
    }

    public static synchronized void deleteKey(String... keys) {
        try (Jedis jedis = JEDIS) {

            jedis.del(keys);
        } catch (Exception e) {
            logger.error("delete key[] fail ", e);
        }
    }

    public static synchronized void deleteKeys(String pattern) {
        try (Jedis jedis = JEDIS) {
            Set<String> keySet = jedis.keys(pattern);
            if (keySet == null || keySet.isEmpty()) {
                return;
            }
            String[] keyArr = new String[keySet.size()];
            int i = 0;
            for (String keys : keySet) {
                keyArr[i] = keys;
                i++;
            }
            deleteKey(keyArr);
        } catch (Exception e) {
            logger.error("delete keys by pattern fail ", e);
        }

    }

    public static synchronized void deleteKeyByPrefix(String prefix) {
        deleteKeys(prefix + "*");
    }

    public static synchronized void deleteKeyByContain(String contain) {
        deleteKeys("*" + contain + "*");
    }
}
