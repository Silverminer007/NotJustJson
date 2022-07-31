package com.ygdevs.notjustjson.yaml;

import com.ygdevs.notjustjson.util.DataElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class YamlElement extends DataElement<YamlElement> {
    public static final YamlElement NULL = new YamlElement(null);

    private YamlElement(Object o) {
        super(o);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull YamlElement number(Number number) {
        return new YamlElement(Objects.requireNonNull(number));
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull YamlElement string(String string) {
        return new YamlElement(Objects.requireNonNull(string));
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull YamlElement bool(Boolean bool) {
        return new YamlElement(Objects.requireNonNull(bool));
    }

    @Contract(" -> new")
    public static @NotNull YamlElement list() {
        return new YamlElement(new ArrayList<>());
    }

    @Contract(" -> new")
    public static @NotNull YamlElement object() {
        return new YamlElement(new HashMap<>());
    }
}
