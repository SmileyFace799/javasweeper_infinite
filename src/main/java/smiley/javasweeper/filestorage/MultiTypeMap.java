package smiley.javasweeper.filestorage;

import java.util.HashMap;
import java.util.Map;

/**
 * Map that can store values of any type.
 *
 * @param <K> The key type
 */
public class MultiTypeMap<K> {
    private final Map<K, Object> valueMap;

    public MultiTypeMap() {
        valueMap = new HashMap<>();
    }

    public MultiTypeMap(Map<? extends K, ?> map) {
        this();
        putAll(map);
    }

    public void put(K key, Object value) {
        valueMap.put(key, value);
    }

    public void putAll(Map<? extends K, ?> map) {
        valueMap.putAll(map);
    }

    public <V> V get(K key, Class<V> returnType) {
        V value = null;
        if (valueMap.containsKey(key)) {
            value = returnType.cast(valueMap.get(key));
        }
        return value;
    }

    public <V> V get(K key, Class<V> returnType, V fallback) {
        return returnType.cast(valueMap.getOrDefault(key, fallback));
    }

    public Map<K, Object> getAll() {
        return valueMap;
    }
}
