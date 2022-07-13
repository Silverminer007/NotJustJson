package com.ygdevs.notjustjson.toml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TomlObject extends TomlElement {
    private final Map<String, TomlElement> root;
    public TomlObject() {
        this.root = new HashMap<>();
    }
    public void put(String key, TomlElement value) {
        this.root.put(key, value);

    }
    public TomlElement get(String key) {
        return this.root.get(key);
    }
    public Set<Map.Entry<String, TomlElement>> entrySet() {
        return root.entrySet();
    }
}