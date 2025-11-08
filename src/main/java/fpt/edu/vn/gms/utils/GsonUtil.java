package fpt.edu.vn.gms.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonUtil {

    private GsonUtil() {
    }

    private static class GsonHelper {
        private static final Gson GSON_SINGLETON_INSTANCE = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .setDateFormat("YYYY-MM-dd")
                .serializeNulls()
                .create();
    }

    public static String toJson(Object obj) {
        try {
            return getInstance().toJson(obj);
        } catch (Throwable e) {
            return StringUtils.EMPTY;
        }
    }

    public static Gson getInstance() {
        return GsonHelper.GSON_SINGLETON_INSTANCE;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return getInstance().fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type clazz) {
        return getInstance().fromJson(json, clazz);
    }

    public static <T> T fromJson(JsonObject json, Class<T> clazz) {
        return getInstance().fromJson(json, clazz);
    }

    public static <T> List<T> fromJsonList(String json, Class<T> clazz) {
        Type listType = new TypeToken<ArrayList<T>>(){}.getType();
        return new Gson().fromJson(json, listType);
    }

    public static <K, V> Map<K, V> fromJsonMap(String json, Class<K> key, Class<V> val) {
        Type listType = new TypeToken<HashMap<K, V>>(){}.getType();
        return new Gson().fromJson(json, listType);
    }

    public static Map<String, String> objectToMap(Object obj) {
        return fromJsonMap(toJson(obj), String.class, String.class);
    }
}
