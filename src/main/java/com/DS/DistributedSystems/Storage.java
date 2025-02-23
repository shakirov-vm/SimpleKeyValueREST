package com.DS.DistributedSystems;

import java.util.HashMap;
import java.util.Map;

public class Storage {

    private final Map<String, String> storage = new HashMap<>();

    public Storage() {}

    public void put(String key, String value) {
        storage.put(key, value);
    }
    public String get(String key) {
        return storage.get(key);
    }
}
