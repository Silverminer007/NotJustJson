package com.ygdevs.notjustjson.toml;

public class TomlElement {
    @Override
    public String toString() {
        return TomlParser.toString(this);
    }
}