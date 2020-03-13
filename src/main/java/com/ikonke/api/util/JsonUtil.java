package com.ikonke.api.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JsonUtil {

    private static final Gson GSON = new Gson();

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> clsOfT) {
        return GSON.fromJson(json, clsOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return GSON.fromJson(json, typeOfT);
    }

}
