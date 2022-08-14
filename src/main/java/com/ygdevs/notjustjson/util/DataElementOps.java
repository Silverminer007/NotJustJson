/*
 * Silverminer007
 * Copyright (c) 2022.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.ygdevs.notjustjson.util;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class DataElementOps<T extends DataElement<T>> implements DynamicOps<T> {

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, @NotNull T input) {
        if (input.isObject())
            return convertMap(outOps, input);
        if (input.isArray())
            return convertList(outOps, input);
        if (input.isNull()) {
            return outOps.empty();
        }
        if (input.isBoolean()) {
            return outOps.createBoolean(input.getBoolean());
        }
        if (input.isNumber()) {
            return outOps.createNumeric(input.getNumber());
        }
        if (input.isString()) {
            return outOps.createString(input.getString());
        }
        throw new IllegalStateException("Don't know how to parse this object: " + input);
    }

    @Override
    public DataResult<Number> getNumberValue(@NotNull T input) {
        if (input.isNumber()) {
            return DataResult.success(input.getNumber());
        }
        if (input.isBoolean()) {
            return DataResult.success(input.getBoolean() ? 1 : 0);
        }
        return DataResult.error("Not a number: " + input);
    }

    @Override
    public DataResult<String> getStringValue(@NotNull T input) {
        return input.isString() ? DataResult.success(input.getString()) : DataResult.error("Not a string: " + input);
    }

    @Override
    public DataResult<T> mergeToList(final @NotNull T list, final T value) {
        if (!(list.isArray()) && list != empty()) {
            return DataResult.error("mergeToList called with not a list: " + list, list);
        }

        final T result = this.createList();
        if (list != empty()) {
            if (list.isArray()) {
                list.getArray().forEach(result::addArray);
            } else {
                return DataResult.error("mergeToList called with not a list: " + list, list);
            }
        }
        result.addArray(value);
        return DataResult.success(result);
    }

    @Override
    public DataResult<T> mergeToList(final @NotNull T list, final List<T> values) {
        if (!(list.isArray()) && list != empty()) {
            return DataResult.error("mergeToList called with not a list: " + list, list);
        }

        final T result = this.createList();
        if (list != empty()) {
            if (list.isArray()) {
                list.getArray().forEach(result::addArray);
            } else {
                return DataResult.error("mergeToList called with not a list: " + list, list);
            }
        }
        values.forEach(result::addArray);
        return DataResult.success(result);
    }

    public abstract T createList();

    @Override
    public DataResult<T> mergeToMap(final @NotNull T map, final T key, final T value) {
        if (!map.isObject() && map != empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map);
        }
        if (!key.isString()) {
            return DataResult.error("key is not a string: " + key, map);
        }

        final T output = this.createObject();
        if (map != empty()) {
            if (map.isObject()) {
                map.getObject().forEach(output::putObject);
            } else {
                return DataResult.error("mergeToMap called with not an object: " + map);
            }
        }
        if (key.isString())
            output.putObject(key.getString(), value);
        else
            return DataResult.error("mergeToMap called with key not being a string");

        return DataResult.success(output);
    }

    public abstract T createObject();

    @Override
    public DataResult<T> mergeToMap(final @NotNull T map, final MapLike<T> values) {
        if (!(map.isObject()) && map != empty()) {
            return DataResult.error("mergeToMap called with not a map: " + map, map);
        }

        final T output = this.createObject();
        if (map != empty()) {
            if (map.isObject()) {
                map.getObject().forEach(output::putObject);
            } else {
                return DataResult.error("mergeToMap called with not an object: " + map);
            }
        }

        final List<T> missed = Lists.newArrayList();

        values.entries().forEach(entry -> {
            final T key = entry.getFirst();
            if (!key.isString()) {
                missed.add(key);
                return;
            }
            if (key.isString())
                output.putObject(key.getString(), entry.getSecond());
        });

        if (!missed.isEmpty()) {
            return DataResult.error("some keys are not strings: " + missed, output);
        }

        return DataResult.success(output);
    }

    @Override
    public DataResult<Stream<Pair<T, T>>> getMapValues(@NotNull T input) {
        return input.isObject() ? DataResult.success(input.getObject().entrySet().stream().map(entry -> Pair.of(this.createString(entry.getKey()), entry.getValue().isNull() ? null : entry.getValue()))) : DataResult.error("Not an object: " + input);
    }

    @Override
    public T createMap(@NotNull Stream<Pair<T, T>> map) {
        T result = this.createObject();
        map.forEach(p -> result.putObject(p.getFirst().isString() ? p.getFirst().getString() : null, p.getSecond()));
        return result;
    }

    @Override
    public DataResult<Stream<T>> getStream(@NotNull T input) {
        if (input.isArray()) {
            return DataResult.success(input.getArray().stream());
        }
        return DataResult.error("Not a toml array: " + input);
    }

    @Override
    public T createList(@NotNull Stream<T> input) {
        T result = this.createList();
        input.forEach(result::addArray);
        return result;
    }

    @Override
    public T remove(@NotNull T input, String key) {
        if (input.isObject()) {
            final T result = this.createObject();
            input.getObject().entrySet().stream().filter(entry -> !Objects.equals(entry.getKey(), key)).forEach(entry -> result.putObject(entry.getKey(), entry.getValue()));
            return result;
        }
        return input;
    }
}