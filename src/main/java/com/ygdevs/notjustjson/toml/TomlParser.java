package com.ygdevs.notjustjson.toml;

import com.google.common.io.CharStreams;
import com.moandjiezana.toml.Toml;
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
    public static @NotNull TomlObject fromReader(Reader reader) throws IOException {
        return fromString(CharStreams.toString(reader));
    }

    public static @NotNull TomlObject fromString(String string) {
        return fromToml(new Toml().read(string));
    }

    public static @NotNull TomlObject fromToml(@NotNull Toml toml) {
        TomlObject result = new TomlObject();
        for (Map.Entry<String, Object> entry : toml.entrySet()) {
            result.put(removeEscapeChars(entry.getKey()), fromObject(entry.getValue()));
        }
        return result;
    }

    private static @NotNull TomlElement fromObject(Object object) {
        if (object == null) {
            return TomlNull.INSTANCE;
        }
        if (object instanceof Number || object instanceof Boolean || object instanceof String) {
            return new TomlPrimitive(object);
        }
        if (object instanceof List<?> list) {
            TomlArray result = new TomlArray();
            list.forEach(o -> result.add(fromObject(o)));
            return result;
        }
        if (object instanceof Map map) {
            TomlObject result = new TomlObject();
            for (Object entry : map.keySet()) {
                if (entry instanceof String key) {
                    result.put(removeEscapeChars(key), fromObject(map.get(key)));
                } else {
                    throw new IllegalStateException("Only string are allowed keys for TOML files!");
                }
            }
            return result;
        }
        if(object instanceof Toml toml) {
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
        if (tomlElement instanceof TomlObject object) {
            StringBuilder stringBuilder = new StringBuilder();
            Set<Map.Entry<String, TomlElement>> entrySet = object.entrySet();
            if (nestedArray) {
                stringBuilder.append("{");
            }
            StringJoiner stringJoiner = new StringJoiner(nestedArray ? "," : "\n");
            if (parent != null && entrySet.stream().anyMatch(entry -> !(entry.getValue() instanceof TomlObject)) && !nestedArray) {
                stringBuilder.append("[").append(parent).append("]").append("\n");
            }
            entrySet.stream().sorted((e1, e2) -> (e1.getValue() instanceof TomlObject && e2.getValue() instanceof TomlObject) ? e1.getKey().compareTo(e2.getKey()) : e1.getValue() instanceof TomlObject ? 1 : e2.getValue() instanceof TomlObject ? -1 : e1.getKey().compareTo(e2.getKey())).forEach(entry ->
                    stringJoiner.add((entry.getValue() instanceof TomlObject && !nestedArray ? "" : escapeKey(entry.getKey()) + " = ") +
                            toString(entry.getValue(), nestedArray,
                                    parent == null ? entry.getKey() : parent + "." + escapeKey(entry.getKey()))));
            stringBuilder.append(stringJoiner);
            if (nestedArray) {
                stringBuilder.append("}");
            }
            return stringBuilder.toString();
        }
        if (tomlElement instanceof TomlArray array) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            StringJoiner stringJoiner = new StringJoiner(",");
            for (TomlElement element : array) {
                stringJoiner.add(toString(element, true, null));
            }
            stringBuilder.append(stringJoiner);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
        if (tomlElement instanceof TomlPrimitive primitive) {
            if (primitive.value instanceof CharSequence charSequence) {
                return "\"" + charSequence + "\"";
            }
            return String.valueOf(primitive.value);
        }
        return "";
    }

    private static String escapeKey(@NotNull String key) {
        for(int i = 0; i < key.length(); i++) {
            if(!validNamespaceChar(key.charAt(i))){
                return "\"" + key + "\"";
            }
        }
        return key;
    }

    private static boolean validNamespaceChar(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
    }

    private static @NotNull String removeEscapeChars(@NotNull String string) {
        if(string.startsWith("\"") && string.endsWith("\""))
            return string.substring(1, string.length() - 1);
        return string;
    }
}