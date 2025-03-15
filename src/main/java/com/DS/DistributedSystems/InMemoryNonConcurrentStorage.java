package com.DS.DistributedSystems;

import java.util.HashMap;
import java.util.Map;

public class InMemoryNonConcurrentStorage implements Storage {

    private final Map<String, String> storage = new HashMap<>();

    public InMemoryNonConcurrentStorage() {}

    public void put(String key, String value) {
        storage.put(key, value);
    }
    public String get(String key) {
        return storage.get(key);
    }
}
