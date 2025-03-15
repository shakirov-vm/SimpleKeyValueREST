package com.DS.DistributedSystems;

public interface Storage {

    public void put(String key, String value);
    public String get(String key);
}
