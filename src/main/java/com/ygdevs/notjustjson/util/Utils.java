/*
 * Silverminer007
 * Copyright (c) 2022.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.ygdevs.notjustjson.util;

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.input.ReaderInputStream;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.data.DataProvider.KEY_COMPARATOR;

public class Utils {
    public static byte @NotNull [] serializeJson(JsonElement jsonElement) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(bytearrayoutputstream, StandardCharsets.UTF_8);
        JsonWriter jsonwriter = new JsonWriter(writer);
        jsonwriter.setSerializeNulls(false);
        jsonwriter.setIndent("  ");
        GsonHelper.writeValue(jsonwriter, jsonElement, KEY_COMPARATOR);
        jsonwriter.close();
        return bytearrayoutputstream.toByteArray();
    }

    public static byte @NotNull [] serializeNbt(Tag tag) throws IOException {
        CompoundTag compound;
        if (tag instanceof CompoundTag compoundTag) {
            compound = compoundTag;
        } else {
            compound = new CompoundTag();
            compound.put("value", tag);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        NbtIo.writeCompressed(compound, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static @NotNull Tag deserializeNbt(Reader reader) throws IOException {
        return NbtIo.readCompressed(new ReaderInputStream(reader, Charset.defaultCharset()));
    }

    public static byte @NotNull [] serializeSNbt(Tag tag) throws IOException {
        CompoundTag compound;
        if (tag instanceof CompoundTag compoundTag) {
            compound = compoundTag;
        } else {
            compound = new CompoundTag();
            compound.put("value", tag);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        NbtIo.write(compound, new DataOutputStream(byteArrayOutputStream));
        return byteArrayOutputStream.toByteArray();
    }

    public static @NotNull Tag deserializeSNbt(Reader reader) throws IOException {
        return NbtIo.read(new DataInputStream(new ReaderInputStream(reader, Charset.defaultCharset())));
    }

    public static <T> @NotNull T fromObject(Object object, DynamicOps<T> dynamicOps) {
        if (object == null) {
            return dynamicOps.empty();
        }
        if (object instanceof Number number) {
            return dynamicOps.createNumeric(number);
        }
        if (object instanceof String string) {
            return dynamicOps.createString(string);
        }
        if (object instanceof Boolean bool) {
            return dynamicOps.createBoolean(bool);
        }
        if (object instanceof List<?> list) {
            return dynamicOps.createList(list.stream().map(o -> fromObject(o, dynamicOps)));
        }
        if (object instanceof Map map) {
            Map<T, T> result = new HashMap<>();
            for (Object entry : map.keySet()) {
                if (entry instanceof String key) {
                    result.put(dynamicOps.createString(key), fromObject(map.get(key), dynamicOps));
                } else {
                    throw new IllegalStateException("Only string are allowed keys for Yaml files!");
                }
            }
            return dynamicOps.createMap(result);
        }
        throw new IllegalStateException("Don't know how to handle Yaml file: " + object);
    }

    public static boolean canParseToNumber(String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public static boolean canParseToBoolean(String string) {
        return "true".equalsIgnoreCase(string) || "false".equalsIgnoreCase(string);
    }
}