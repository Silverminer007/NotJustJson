package com.ygdevs.notjustjson.yaml;

import com.ygdevs.notjustjson.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class YamlParser {
    public static YamlElement fromReader(Reader reader) {
        Yaml yaml = new Yaml();
        Map<String, Object> map = yaml.load(reader);
        return Utils.fromObject(map, YamlOps.INSTANCE);
    }

    public static byte @NotNull [] toBytes(YamlElement element) {
        Yaml yaml = new Yaml();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        yaml.dump(toMap(element), new OutputStreamWriter(stream));
        return stream.toByteArray();
    }

    public static Object toMap(@NotNull YamlElement element) {
        if(element.isObject()) {
            Map<String, Object> result = new HashMap<>();
            for(Map.Entry<String, YamlElement> entry : element.getObject().entrySet()) {
                result.put(entry.getKey(), toMap(entry.getValue()));
            }
            return result;
        }
        if(element.isArray()) {
            return element.getArray().stream().map(YamlParser::toMap).toList();
        }
        return element.get();
    }
}
