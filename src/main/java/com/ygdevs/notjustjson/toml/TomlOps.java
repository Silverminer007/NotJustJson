package com.ygdevs.notjustjson.toml;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class TomlOps implements DynamicOps<TomlElement> {
    public static final TomlOps INSTANCE = new TomlOps();

    private TomlOps() {
    }

    @Override
    public TomlElement empty() {
        return TomlNull.INSTANCE;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, TomlElement input) {
        if (input instanceof TomlObject)
            return convertMap(outOps, input);
        if (input instanceof TomlArray)
            return convertList(outOps, input);
        if (input instanceof TomlNull) {
            return outOps.empty();
        }
        if (input instanceof TomlPrimitive primitive) {
            Object value = primitive.value;
            if (value instanceof String string) {
                return outOps.createString(string);
            }
            if (value instanceof Boolean bool) {
                return outOps.createBoolean(bool);
            }
            if (value instanceof Number number) {
                return outOps.createNumeric(number);
            }
        }
        throw new IllegalStateException("Don't know how to parse this object: " + input);
    }

    @Override
    public DataResult<Number> getNumberValue(TomlElement input) {
        if (input instanceof TomlPrimitive primitive) {
            Object value = primitive.value;
            if (value instanceof Number number) {
                return DataResult.success(number);
            }
            if (value instanceof Boolean bool) {
                return DataResult.success(bool ? 1 : 0);
            }
        }
        return DataResult.error("Not a number: " + input);
    }

    @Override
    public TomlElement createNumeric(Number i) {
        return new TomlPrimitive(i);
    }

    @Override
    public DataResult<String> getStringValue(TomlElement input) {
        return input instanceof TomlPrimitive primitive && primitive.value instanceof String string ? DataResult.success(string) : DataResult.error("Not a string: " + input);
    }

    @Override
    public TomlElement createString(String value) {
        return new TomlPrimitive(value);
    }

    @Override
    public DataResult<TomlElement> mergeToList(final TomlElement list, final TomlElement value) {
        if (!(list instanceof TomlArray) && list != empty()) {
            return DataResult.error("mergeToList called with not a list: " + list, list);
        }

        final TomlArray result = new TomlArray();
        if (list != empty()) {
            if (list instanceof TomlArray array) {
                array.stream().forEach(result::add);
            } else {
                return DataResult.error("mergeToList called with not a list: " + list, list);
            }
        }
        result.add(value);
        return DataResult.success(result);
    }

    @Override
    public DataResult<TomlElement> mergeToList(final TomlElement list, final List<TomlElement> values) {
        if (!(list instanceof TomlArray) && list != empty()) {
            return DataResult.error("mergeToList called with not a list: " + list, list);
        }

        final TomlArray result = new TomlArray();
        if (list != empty()) {
            if (list instanceof TomlArray array) {
                array.stream().forEach(result::add);
            } else {
                return DataResult.error("mergeToList called with not a list: " + list, list);
            }
        }
        values.forEach(result::add);
        return DataResult.success(result);
    }

    @Override
    public DataResult<TomlElement> mergeToMap(final TomlElement map, final TomlElement key, final TomlElement value) {
        if (!(map instanceof TomlObject) && map != empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map);
        }
        if (!(key instanceof TomlPrimitive primitive && primitive.value instanceof String)) {
            return DataResult.error("key is not a string: " + key, map);
        }

        final TomlObject output = new TomlObject();
        if (map != empty()) {
            if (map instanceof TomlObject object) {
                object.entrySet().forEach(entry -> output.put(entry.getKey(), entry.getValue()));
            } else {
                return DataResult.error("mergeToMap called with not an object: " + map);
            }
        }
        if (((TomlPrimitive) key).value instanceof String string)
            output.put(string, value);
        else
            return DataResult.error("mergeToMap called with key not being a string");

        return DataResult.success(output);
    }

    @Override
    public DataResult<TomlElement> mergeToMap(final TomlElement map, final MapLike<TomlElement> values) {
        if (!(map instanceof TomlObject) && map != empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map);
        }

        final TomlObject output = new TomlObject();
        if (map != empty()) {
            if (map instanceof TomlObject object) {
                object.entrySet().forEach(entry -> output.put(entry.getKey(), entry.getValue()));
            } else {
                return DataResult.error("mergeToMap called with not an object: " + map);
            }
        }

        final List<TomlElement> missed = Lists.newArrayList();

        values.entries().forEach(entry -> {
            final TomlElement key = entry.getFirst();
            if (!(key instanceof TomlPrimitive primitive && primitive.value instanceof String)) {
                missed.add(key);
                return;
            }
            if (((TomlPrimitive) key).value instanceof String string)
                output.put(string, entry.getSecond());
        });

        if (!missed.isEmpty()) {
            return DataResult.error("some keys are not strings: " + missed, output);
        }

        return DataResult.success(output);
    }

    @Override
    public DataResult<Stream<Pair<TomlElement, TomlElement>>> getMapValues(TomlElement input) {
        return input instanceof TomlObject object ? DataResult.success(object.entrySet().stream().map(entry -> Pair.of(new TomlPrimitive(entry.getKey()), entry.getValue() instanceof TomlNull ? null : entry.getValue()))) : DataResult.error("Not an object: " + input);
    }

    @Override
    public TomlElement createMap(Stream<Pair<TomlElement, TomlElement>> map) {
        TomlObject result = new TomlObject();
        map.forEach(p -> result.put(p.getFirst() instanceof TomlPrimitive primitive && primitive.value instanceof String string ? string : null, p.getSecond()));
        return result;
    }

    @Override
    public DataResult<Stream<TomlElement>> getStream(TomlElement input) {
        if (input instanceof TomlArray array) {
            return DataResult.success(array.stream());
        }
        return DataResult.error("Not a toml array: " + input);
    }

    @Override
    public TomlElement createList(Stream<TomlElement> input) {
        TomlArray result = new TomlArray();
        input.forEach(result::add);
        return result;
    }

    @Override
    public TomlElement remove(TomlElement input, String key) {
        if (input instanceof TomlObject object) {
            final TomlObject result = new TomlObject();
            object.entrySet().stream().filter(entry -> !Objects.equals(entry.getKey(), key)).forEach(entry -> result.put(entry.getKey(), entry.getValue()));
            return result;
        }
        return input;
    }
}