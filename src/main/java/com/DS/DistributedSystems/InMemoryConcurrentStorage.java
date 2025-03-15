package com.DS.DistributedSystems;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryConcurrentStorage implements Storage {
    private final Map<String, String> storage = new ConcurrentHashMap<>();

    public InMemoryConcurrentStorage() {}

    public void put(String key, String value) {
        storage.put(key, value);
    }
    public String get(String key) {
        return storage.get(key);
    }
}
