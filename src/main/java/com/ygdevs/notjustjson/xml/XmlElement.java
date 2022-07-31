package com.ygdevs.notjustjson.xml;

import com.ygdevs.notjustjson.util.DataElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class XmlElement extends DataElement<XmlElement> {
    public static final XmlElement NULL = new XmlElement(null);

    private XmlElement(Object o) {
        super(o);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull XmlElement number(Number number) {
        return new XmlElement(Objects.requireNonNull(number));
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull XmlElement string(String string) {
        return new XmlElement(Objects.requireNonNull(string));
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull XmlElement bool(Boolean bool) {
        return new XmlElement(Objects.requireNonNull(bool));
    }

    @Contract(" -> new")
    public static @NotNull XmlElement list() {
        return new XmlElement(new ArrayList<>());
    }

    @Contract(" -> new")
    public static @NotNull XmlElement object() {
        return new XmlElement(new HashMap<>());
    }
}
