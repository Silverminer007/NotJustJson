package com.ygdevs.notjustjson.toml;

public class TomlPrimitive extends TomlElement{
    public final Object value;

    public TomlPrimitive(Object value) {
        this.value = value;
    }
}
