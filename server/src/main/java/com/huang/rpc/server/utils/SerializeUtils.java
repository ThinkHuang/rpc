package com.huang.rpc.server.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.alibaba.fastjson.JSON;
import com.google.gson.GsonBuilder;
import com.huang.rpc.server.handler.RpcInvocation;

/**
 * @author huangyejun
 *
 */
public class SerializeUtils {

    private SerializeUtils() {
    }

    /**
     * jdk序列化
     * 
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
     * 
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
     * 
     * @param <T>
     * @param object
     * @return
     */
    public static <T> String serializableFastjson(T object) {
        return JSON.toJSONString(object);
    }

    /**
     * 将JSON字符串反序列化为java对象
     * 
     * @param <T>
     * @param value
     * @param clazz
     * @return
     */
    public static <T> T deserializableFastjson(String value, Class<T> clazz) {
        return JSON.parseObject(value, clazz);
    }

    public static <T> T deserializableGjson(String value, Class<T> clazz) {
        return new GsonBuilder().create().fromJson(value, clazz);
    }

    public static void main(String[] args) {
        String json = "{\n" + "    \"arguments\": [\n" + "        \"Hello World\"\n" + "    ],\n" + "    \"clazz\": {\n" + "        \n" + "    },\n" + "    \"method\": {\n"
                + "        \"accessible\": false,\n" + "        \"annotatedExceptionTypes\": [\n" + "            \n" + "        ],\n" + "        \"annotatedParameterTypes\": [\n"
                + "            {\n" + "                \"annotations\": [\n" + "                    \n" + "                ],\n" + "                \"declaredAnnotations\": [\n"
                + "                    \n" + "                ],\n" + "                \"type\": \"java.lang.String\"\n" + "            }\n" + "        ],\n"
                + "        \"annotatedReceiverType\": {\n" + "            \"annotations\": [\n" + "                \n" + "            ],\n"
                + "            \"declaredAnnotations\": [\n" + "                \n" + "            ],\n"
                + "            \"type\": \"com.huang.rpc.server.service.UserServiceImpl\"\n" + "        },\n" + "        \"annotatedReturnType\": {\n"
                + "            \"annotations\": [\n" + "                \n" + "            ],\n" + "            \"declaredAnnotations\": [\n" + "                \n"
                + "            ],\n" + "            \"type\": \"java.lang.String\"\n" + "        },\n" + "        \"annotations\": [\n" + "            {\n"
                + "                \"value\": \"v1.0\"\n" + "            },\n" + "            {\n" + "                \"value\": \"http\"\n" + "            }\n" + "        ],\n"
                + "        \"bridge\": false,\n" + "        \"declaringClass\": \"com.huang.rpc.server.service.UserServiceImpl\",\n" + "        \"default\": false,\n"
                + "        \"exceptionTypes\": [\n" + "            \n" + "        ],\n" + "        \"genericExceptionTypes\": [\n" + "            \n" + "        ],\n"
                + "        \"genericParameterTypes\": [\n" + "            \"java.lang.String\"\n" + "        ],\n" + "        \"genericReturnType\": \"java.lang.String\",\n"
                + "        \"modifiers\": 1,\n" + "        \"name\": \"say\",\n" + "        \"parameterAnnotations\": [\n" + "            [\n" + "                \n"
                + "            ]\n" + "        ],\n" + "        \"parameterCount\": 1,\n" + "        \"parameterTypes\": [\n" + "            \"java.lang.String\"\n"
                + "        ],\n" + "        \"returnType\": \"java.lang.String\",\n" + "        \"synthetic\": false,\n" + "        \"typeParameters\": [\n" + "            \n"
                + "        ],\n" + "        \"varArgs\": false\n" + "    }\n" + "}";
        RpcInvocation invocation = deserializableFastjson(json, RpcInvocation.class);
        System.out.println(invocation);
    }
}
