/*
 * Silverminer007
 * Copyright (c) 2022.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.ygdevs.notjustjson.toml;

import com.google.common.io.CharStreams;
import com.moandjiezana.toml.Toml;
import com.ygdevs.notjustjson.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class TomlParser {
    public static @NotNull TomlElement fromReader(Reader reader) throws IOException {
        return fromString(CharStreams.toString(reader));
    }

    public static @NotNull TomlElement fromString(String string) {
        return fromToml(new Toml().read(string));
    }

    public static @NotNull TomlElement fromToml(@NotNull Toml toml) {
        TomlElement result = TomlElement.object();
        for (Map.Entry<String, Object> entry : toml.entrySet()) {
            result.putObject(removeEscapeChars(entry.getKey()), fromObject(entry.getValue()));
        }
        return result;
    }

    private static @NotNull TomlElement fromObject(Object object) {
        if (object == null) {
            return TomlElement.NULL;
        }
        if (object instanceof Number number) {
            return TomlElement.number(number);
        }
        if (object instanceof String string) {
            return TomlElement.string(string);
        }
        if (object instanceof Boolean bool) {
            return TomlElement.bool(bool);
        }
        if (object instanceof List<?> list) {
            TomlElement result = TomlElement.list();
            list.forEach(o -> result.addArray(fromObject(o)));
            return result;
        }
        if (object instanceof Map map) {
            TomlElement result = TomlElement.object();
            for (Object entry : map.keySet()) {
                if (entry instanceof String key) {
                    result.putObject(removeEscapeChars(key), fromObject(map.get(key)));
                } else {
                    throw new IllegalStateException("Only string are allowed keys for TOML files!");
                }
            }
            return result;
        }
        if (object instanceof Toml toml) {
            return fromToml(toml);
        }
        throw new IllegalStateException("Don't know how to handle TOML file: " + object);
    }

    public static byte @NotNull [] toBytes(TomlElement tomlElement) {
        return toString(tomlElement).getBytes(StandardCharsets.UTF_8);
    }

    public static String toString(TomlElement tomlElement) {
        return toString(tomlElement, false, null);
    }

    public static String toString(TomlElement tomlElement, boolean nestedArray, @Nullable String parent) {
        if (tomlElement.isObject()) {
            StringBuilder stringBuilder = new StringBuilder();
            Set<Map.Entry<String, TomlElement>> entrySet = tomlElement.getObject().entrySet();
            if (nestedArray) {
                stringBuilder.append("{");
            }
            StringJoiner stringJoiner = new StringJoiner(nestedArray ? "," : "\n");
            if (parent != null && entrySet.stream().anyMatch(entry -> !(entry.getValue().isObject())) && !nestedArray) {
                stringBuilder.append("[").append(parent).append("]").append("\n");
            }
            entrySet.stream().sorted((e1, e2) -> (e1.getValue().isObject() && e2.getValue().isObject()) ? e1.getKey().compareTo(e2.getKey()) : e1.getValue().isObject() ? 1 : e2.getValue().isObject() ? -1 : e1.getKey().compareTo(e2.getKey())).forEach(entry ->
                    stringJoiner.add((entry.getValue().isObject() && !nestedArray ? "" : escapeKey(entry.getKey()) + " = ") +
                            toString(entry.getValue(), nestedArray,
                                    parent == null ? entry.getKey() : parent + "." + escapeKey(entry.getKey()))));
            stringBuilder.append(stringJoiner);
            if (nestedArray) {
                stringBuilder.append("}");
            }
            return stringBuilder.toString();
        }
        if (tomlElement.isArray()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            StringJoiner stringJoiner = new StringJoiner(",");
            for (TomlElement element : tomlElement.getArray()) {
                stringJoiner.add(toString(element, true, null));
            }
            stringBuilder.append(stringJoiner);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
        if (tomlElement.isString()) {
            return "\"" + tomlElement.getString() + "\"";
        }
        if(tomlElement.isNumber() || tomlElement.getBoolean()) {
            return String.valueOf(tomlElement.get());
        }
        return "";
    }

    private static String escapeKey(@NotNull String key) {
        for (int i = 0; i < key.length(); i++) {
            if (!validNamespaceChar(key.charAt(i))) {
                return "\"" + key + "\"";
            }
        }
        return key;
    }

    private static boolean validNamespaceChar(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
    }

    private static @NotNull String removeEscapeChars(@NotNull String string) {
        if (string.startsWith("\"") && string.endsWith("\""))
            return string.substring(1, string.length() - 1);
        return string;
    }
}