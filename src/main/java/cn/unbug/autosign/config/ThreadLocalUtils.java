package cn.unbug.autosign.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: AutoSign
 * @Package: cn.unbug.autosign.config
 * @ClassName: ThreadLoaclUtils
 * @Author: zhangtao
 * @Description: []
 * @Date: 2023/5/30 21:14
 */
public class ThreadLocalUtils {

    private static final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(HashMap::new);


    public static void set(String key, Object value) {
        Map<String, Object> map = threadLocal.get();
        map.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        Map<String, Object> map = threadLocal.get();
        return (T) map.get(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getOrDefault(String key, T defaultValue) {
        Map<String, Object> map = threadLocal.get();
        return (T) map.getOrDefault(key, defaultValue);
    }

    public static void clear() {
        threadLocal.remove();
    }
}
