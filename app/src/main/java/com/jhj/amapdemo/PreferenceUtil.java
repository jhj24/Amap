package com.jhj.amapdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class PreferenceUtil {

    public static <T> boolean save(Context context, T entity, String key) {
        if (entity == null) {
            return false;
        }
        String prefFileName = entity.getClass().getName();
        SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
        SharedPreferences.Editor et = sp.edit();
        String json = toGson(entity);
        et.putString(key, json);
        return et.commit();
    }

    public static <T> List<T> findAll(Context context, Class<T> clazz) {
        String prefFileName = clazz.getName();
        SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
        Map<String, String> values = (Map<String, String>) sp.getAll();

        List<T> results = new ArrayList<T>();

        if (values == null || values.isEmpty())
            return (List<T>) results;

        Collection<String> colles = values.values();

        for (String json : colles) {
            T bean = parseJson(json, clazz);
            results.add(bean);
        }
        return (List<T>) results;
    }

    public static <T> T find(Context context, String key, Class<T> clazz) {
        String prefFileName = clazz.getName();
        SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
        String json = sp.getString(key, null);
        if (json == null)
            return null;
        return parseJson(json, clazz);
    }

    public static <T extends Serializable> void delete(Context context, String key, Class<T> clazz) {
        String prefFileName = clazz.getName();
        SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
        if (sp.contains(key)) {
            sp.edit().remove(key).apply();
        }
    }

    public static <T extends Serializable> void deleteAll(Context context, Class<T> clazz) {
        String prefFileName = clazz.getName();
        SharedPreferences sp = context.getSharedPreferences(prefFileName, 0);
        sp.edit().clear().apply();
    }


    public static <T> T parseJson(String json, Class cls) {
        try {
            return (T) new Gson().fromJson(json, cls);
        } catch (JsonSyntaxException e) {
            Log.e("preference", "解析gson字符串失败");
        }
        return null;
    }

    public static String toGson(Object obj) {
        try {
            return new Gson().toJson(obj);
        } catch (Exception e) {
            Log.e("preference", "数据生成gson字符串失败");
        }
        return null;
    }
}
