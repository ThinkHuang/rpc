package com.huang.rpc.server.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.alibaba.fastjson.JSON;

/**
 * @author huangyejun
 *
 */
public class SerializeUtils {
    
    private SerializeUtils() {}

    /**
     * jdk序列化
     * @param obj
     * @return
     */
    public static byte[] serializeJDK(Object obj) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            bytes = baos.toByteArray();
            baos.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * jdk反序列
     * @param bytes
     * @return
     */
    public static Object deserializeJDK(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
    
    /**
     * fastjson将对象序列化为JSON字符串
     * @param <T>
     * @param object
     * @return
     */
    public static <T> String serializableFastjson(T object) {
        System.out.println("序列化对象为：" + object);
        return JSON.toJSONString(object);
    }

    /**
     * 将JSON字符串反序列化为java对象
     * @param <T>
     * @param value
     * @param clazz
     * @return
     */
    public static <T> T deserializableFastjson(String value, Class<T> clazz) {
        return JSON.parseObject(value, clazz);
    }
}
