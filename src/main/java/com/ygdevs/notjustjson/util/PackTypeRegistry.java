package com.ygdevs.notjustjson.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.ygdevs.notjustjson.NotJustJson;
import com.ygdevs.notjustjson.toml.TomlElement;
import com.ygdevs.notjustjson.toml.TomlOps;
import com.ygdevs.notjustjson.toml.TomlParser;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class PackTypeRegistry {
    public static final DeferredRegister<PackType<?>> REGISTRY = DeferredRegister.create(PackType.REGISTRY_KEY, NotJustJson.MODID);
    public static final Supplier<IForgeRegistry<PackType<?>>> REGISTRY_SUPPLIER = REGISTRY.makeRegistry(RegistryBuilder::new);
    public static final RegistryObject<PackType<TomlElement>> TOML = REGISTRY.register("toml", () -> new PackType<>(TomlOps.INSTANCE, "toml", TomlParser::fromReader, TomlParser::toBytes));
    public static final RegistryObject<PackType<JsonElement>> JSON = REGISTRY.register("json", () -> new PackType<>(JsonOps.INSTANCE, "json", JsonParser::parseReader, Utils::serializeJson));
    public static final RegistryObject<PackType<Tag>> NBT = REGISTRY.register("nbt", () -> new PackType<>(NbtOps.INSTANCE, "nbt", Utils::deserializeNbt, Utils::serializeNbt));
    public static final RegistryObject<PackType<Tag>> SNBT = REGISTRY.register("snbt", () -> new PackType<>(NbtOps.INSTANCE, "snbt", Utils::deserializeSNbt, Utils::serializeSNbt));

    public static Iterable<PackType<?>> ordered() {
        return REGISTRY_SUPPLIER.get().getValues().stream().sorted().toList();
    }
}
