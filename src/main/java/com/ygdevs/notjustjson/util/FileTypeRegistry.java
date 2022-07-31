package com.ygdevs.notjustjson.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.ygdevs.notjustjson.NotJustJson;
import com.ygdevs.notjustjson.toml.TomlElement;
import com.ygdevs.notjustjson.toml.TomlOps;
import com.ygdevs.notjustjson.toml.TomlParser;
import com.ygdevs.notjustjson.xml.XmlElement;
import com.ygdevs.notjustjson.xml.XmlOps;
import com.ygdevs.notjustjson.xml.XmlParser;
import com.ygdevs.notjustjson.yaml.YamlElement;
import com.ygdevs.notjustjson.yaml.YamlOps;
import com.ygdevs.notjustjson.yaml.YamlParser;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class FileTypeRegistry {
    public static final DeferredRegister<FileType<?>> REGISTRY = DeferredRegister.create(FileType.REGISTRY_KEY, NotJustJson.MODID);
    public static final Supplier<IForgeRegistry<FileType<?>>> REGISTRY_SUPPLIER = REGISTRY.makeRegistry(() -> new RegistryBuilder<FileType<?>>().disableSaving().disableSync().allowModification());
    public static final RegistryObject<FileType<TomlElement>> TOML = REGISTRY.register("toml", () -> new FileType<>(TomlOps.INSTANCE, "toml", TomlParser::fromReader, TomlParser::toBytes));
    public static final RegistryObject<FileType<JsonElement>> JSON = REGISTRY.register("json", () -> new FileType<>(JsonOps.INSTANCE, "json", JsonParser::parseReader, Utils::serializeJson));
    public static final RegistryObject<FileType<Tag>> NBT = REGISTRY.register("nbt", () -> new FileType<>(NbtOps.INSTANCE, "nbt", Utils::deserializeNbt, Utils::serializeNbt));
    public static final RegistryObject<FileType<Tag>> SNBT = REGISTRY.register("snbt", () -> new FileType<>(NbtOps.INSTANCE, "snbt", Utils::deserializeSNbt, Utils::serializeSNbt));
    public static final RegistryObject<FileType<XmlElement>> XML = REGISTRY.register("xml", () -> new FileType<>(XmlOps.INSTANCE, "xml", XmlParser::fromReader, XmlParser::toBytes));
    public static final RegistryObject<FileType<YamlElement>> YAML = REGISTRY.register("yaml", () -> new FileType<>(YamlOps.INSTANCE, "yaml", YamlParser::fromReader, YamlParser::toBytes));

    public static Iterable<FileType<?>> ordered() {
        return REGISTRY_SUPPLIER.get().getValues().stream().sorted().toList();
    }
}
