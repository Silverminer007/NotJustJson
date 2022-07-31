package com.ygdevs.notjustjson.toml;

import com.ygdevs.notjustjson.util.DataElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TomlElement extends DataElement<TomlElement> {
    public static final TomlElement NULL = new TomlElement(null);

    private TomlElement(Object o) {
        super(o);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull TomlElement number(Number number) {
        return new TomlElement(Objects.requireNonNull(number));
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull TomlElement string(String string) {
        return new TomlElement(Objects.requireNonNull(string));
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull TomlElement bool(Boolean bool) {
        return new TomlElement(Objects.requireNonNull(bool));
    }

    @Contract(" -> new")
    public static @NotNull TomlElement list() {
        return new TomlElement(new ArrayList<>());
    }

    @Contract(" -> new")
    public static @NotNull TomlElement object() {
        return new TomlElement(new HashMap<>());
    }

    @Override
    public String toString() {
        return TomlParser.toString(this);
    }
}