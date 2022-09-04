package main;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;

public class DataHandler<K, V> {
    final String fileName;
    private HashMap<K, V> data;

    public DataHandler(String fileName) {
        this.fileName = fileName;
        load();
    }

    public void load() {
        try {
            data = new ObjectMapper().readValue(getClass()
                    .getResourceAsStream("/data/" + fileName + ".json"), HashMap.class
            );
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public V get(K key) {
        return data.get(key);
    }

    public void put(K key, V value) {
        data.put(key, value);
    }
}
