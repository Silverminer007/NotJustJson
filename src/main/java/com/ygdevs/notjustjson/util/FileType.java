/*
 * Silverminer007
 * Copyright (c) 2022.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.ygdevs.notjustjson.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.ygdevs.notjustjson.NotJustJson;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Reader;

public record FileType<T>(DynamicOps<T> ops, String name, IOFunction<Reader, T> deserializer, IOFunction<T, byte[]> serializer) implements Comparable<FileType<?>>{
    public static final ResourceKey<? extends Registry<FileType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(NotJustJson.MODID, "pack_type"));

    public JsonElement parse(Reader reader) throws IOException {
        return this.ops().convertTo(JsonOps.INSTANCE, this.deserializer().apply(reader));
    }

    @Override
    public int compareTo(@NotNull FileType<?> o) {
        return this.name().compareTo(o.name());
    }
}
