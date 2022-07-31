package com.ygdevs.notjustjson.xml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ygdevs.notjustjson.util.Utils;
import org.apache.commons.io.input.ReaderInputStream;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class XmlParser {
    public static @NotNull XmlElement fromReader(Reader reader) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        HashMap<String, Object> map = xmlMapper.readValue(new ReaderInputStream(reader, Charset.defaultCharset()), new TypeReference<>() {
        });
        return Utils.fromObject(map, XmlOps.INSTANCE);
    }

    @Contract(value = "_ -> new", pure = true)
    public static byte @NotNull [] toBytes(XmlElement element) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        xmlMapper.writeValue(byteArrayOutputStream, toMap(element));
        return byteArrayOutputStream.toByteArray();
    }

    public static Object toMap(@NotNull XmlElement element) {
        if (element.isObject()) {
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<String, XmlElement> entry : element.getObject().entrySet()) {
                result.put(entry.getKey(), toMap(entry.getValue()));
            }
            return result;
        }
        if (element.isArray()) {
            return element.getArray().stream().map(XmlParser::toMap).toList();
        }
        return element.get();
    }
}