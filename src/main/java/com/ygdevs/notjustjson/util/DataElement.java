package com.ygdevs.notjustjson.util;

import com.ygdevs.notjustjson.util.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class DataElement<T> {
    private final Object value;

    protected DataElement(Object o) {
        this.value = o;
    }

    public boolean isString() {
        return this.value instanceof String;
    }

    public boolean isNumber() {
        return this.value instanceof Number || this.value instanceof String string && Utils.canParseToNumber(string);
    }

    public boolean isBoolean() {
        return this.value instanceof Boolean || this.value instanceof String string && Utils.canParseToBoolean(string);
    }

    public boolean isArray() {
        return this.value instanceof List<?> || this.value instanceof String string && string.isEmpty();
    }

    public boolean isObject() {
        return this.value instanceof Map<?, ?> || this.value instanceof String string && string.isEmpty();
    }

    public boolean isNull() {
        return this.value == null;
    }

    public Object get() {
        return this.value;
    }

    public <V> V get(@NotNull Class<V> type) {
        return type.cast(this.get());
    }

    public String getString() {
        return (String) this.get();
    }

    public Number getNumber() {
        if (this.value instanceof Number) {
            return (Number) this.get();
        } else {
            String string = (String) this.get();
            if (string.contains("-")) {
                return Double.parseDouble(string);
            } else {
                return Integer.parseInt(string);
            }
        }
    }

    public Boolean getBoolean() {
        if (this.value instanceof Boolean) {
            return (Boolean) this.get();
        } else {
            return Boolean.parseBoolean((String) this.get());
        }
    }

    public List<T> getArray() {
        return Collections.unmodifiableList(this.getArrayMutable());
    }

    @SuppressWarnings("unchecked")
    private List<T> getArrayMutable() {
        if (this.value instanceof List<?>)
            return (List<T>) this.get();
        else
            return List.of();
    }

    public T getArray(int idx) {
        return this.getArrayMutable().get(idx);
    }

    public void addArray(T T) {
        this.getArrayMutable().add(T);
    }

    public Map<String, T> getObject() {
        return Collections.unmodifiableMap(this.getObjectMutable());
    }

    @SuppressWarnings("unchecked")
    private Map<String, T> getObjectMutable() {
        if (this.value instanceof Map<?, ?>)
            return (Map<String, T>) this.get();
        else
            return Map.of();
    }

    public T getObject(String key) {
        return this.getObjectMutable().get(key);
    }

    public void putObject(String key, T T) {
        this.getObjectMutable().put(key, T);
    }
}