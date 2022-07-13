package com.ygdevs.notjustjson.util;

import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.input.ReaderInputStream;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
        if(tag instanceof CompoundTag compoundTag) {
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
        if(tag instanceof CompoundTag compoundTag) {
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
}