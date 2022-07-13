package com.ygdevs.notjustjson.toml;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class TomlArray extends TomlElement implements Iterable<TomlElement>{
    public List<TomlElement> elements;

    public TomlArray(TomlElement... elements) {
        this.elements = new ArrayList<>(List.of(elements));
    }

    @NotNull
    @Override
    public Iterator<TomlElement> iterator() {
        return this.elements.iterator();
    }

    public Stream<TomlElement> stream() {
        return this.elements.stream();
    }

    public void add(TomlElement element) {
        this.elements.add(element);
    }
}