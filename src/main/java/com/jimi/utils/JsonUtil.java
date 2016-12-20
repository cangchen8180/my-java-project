package com.jimi.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author jimi
 * @description
 * @date 2016-04-09 14:57.
 */
public class JsonUtil {

    /**
     * 懒加载使用的内部类
     */
    private static class LoadGson {
        private static Gson singleton = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create();
    }

    /**
     * gson单例
     *
     * @return
     */
    public static Gson getSingletonGson() {
        return LoadGson.singleton;
    }

    /**
     * 转为json格式字符串
     *
     * @param src
     * @return
     */
    public static String toJsonWithGson(Object src) {
        String jsonStr = getSingletonGson().toJson(src);
        return jsonStr;
    }

    /**
     * json字符串转为指定类
     *
     * @param jsonStr
     * @param classOfT
     * @param <T>
     * @return
     */
    public static  <T> T fromJsonWithGson(String jsonStr, Class<T> classOfT) {
        T t = getSingletonGson().fromJson(jsonStr, classOfT);
        return t;
    }
}
