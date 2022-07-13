package com.ygdevs.notjustjson.mixins;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.ygdevs.notjustjson.NotJustJson;
import com.ygdevs.notjustjson.util.PackType;
import com.ygdevs.notjustjson.util.PackTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.RegistryResourceAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(RegistryOps.class)
public class MixinRegistryOps {
    @Redirect(method = "createAndLoad(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/core/RegistryAccess$Writable;Lnet/minecraft/server/packs/resources/ResourceManager;)Lnet/minecraft/resources/RegistryOps;", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/RegistryResourceAccess;forResourceManager(Lnet/minecraft/server/packs/resources/ResourceManager;)Lnet/minecraft/resources/RegistryResourceAccess;"))
    private static RegistryResourceAccess notjustjson_createAndLoad(ResourceManager p_195882_) {
        return new RegistryResourceAccess() {

            public <E> @NotNull Map<ResourceKey<E>, RegistryResourceAccess.EntryThunk<E>> listResources(@NotNull ResourceKey<? extends Registry<E>> key) {
                String s = registryDirPath(key.location());
                Map<ResourceKey<E>, RegistryResourceAccess.EntryThunk<E>> map = new HashMap<>();
                for (PackType<?> packType : PackTypeRegistry.ordered()) {
                    String suffix = "." + packType.name();
                    p_195882_.listResources(s, (p_214262_) -> p_214262_.getPath().endsWith(suffix)).forEach((resourceLocation, resource) -> {
                        String s1 = resourceLocation.getPath();
                        String s2 = s1.substring(s.length() + 1, s1.length() - suffix.length());
                        ResourceKey<E> resourcekey = ResourceKey.create(key, new ResourceLocation(resourceLocation.getNamespace(), s2));
                        if (map.containsKey(resourcekey)) {
                            NotJustJson.LOGGER.error("Detected Duplicate Resource, with different type in data pack! Affected Resource: {}", resourceLocation);
                        } else {
                            map.put(resourcekey, (jsonOps, decoder) -> {
                                try {
                                    Reader reader = resource.openAsReader();

                                    DataResult<ParsedEntry<E>> dataresult;
                                    try {
                                        dataresult = this.decodeElement(jsonOps, packType, decoder, reader, key);
                                    } catch (Throwable throwable1) {
                                        try {
                                            reader.close();
                                        } catch (Throwable throwable) {
                                            throwable1.addSuppressed(throwable);
                                        }

                                        throw throwable1;
                                    }

                                    reader.close();

                                    return dataresult;
                                } catch (JsonIOException | JsonSyntaxException | IOException ioexception) {
                                    return DataResult.error("Failed to parse " + resourceLocation + " file: " + ioexception.getMessage());
                                }
                            });
                        }
                    });
                }
                return map;
            }

            public <E> @NotNull Optional<RegistryResourceAccess.EntryThunk<E>> getResource(@NotNull ResourceKey<E> resourceKey) {
                for (PackType<?> packType : PackTypeRegistry.ordered()) {
                    ResourceLocation resourcelocation = elementPath(resourceKey, "." + packType.name());
                    Optional<Resource> optionalResource = p_195882_.getResource(resourcelocation);
                    if (optionalResource.isPresent()) {
                        return optionalResource.map((resource) -> (jsonOps, decoder) -> {
                            try {
                                Reader reader = resource.openAsReader();

                                DataResult<ParsedEntry<E>> dataresult;
                                try {
                                    dataresult = this.decodeElement(jsonOps, packType, decoder, reader, resourceKey);
                                } catch (Throwable throwable1) {
                                    try {
                                        reader.close();
                                    } catch (Throwable throwable) {
                                        throwable1.addSuppressed(throwable);
                                    }

                                    throw throwable1;
                                }

                                reader.close();

                                return dataresult;
                            } catch (JsonIOException | JsonSyntaxException | IOException ioexception) {
                                return DataResult.error("Failed to parse " + resourcelocation + " file: " + ioexception.getMessage());
                            }
                        });
                    }
                }
                return Optional.empty();
            }

            private <E> DataResult<RegistryResourceAccess.ParsedEntry<E>> decodeElement(DynamicOps<JsonElement> jsonOps, @NotNull PackType<?> packType, Decoder<E> decoder, Reader reader, ResourceKey<?> key) throws IOException {
                try {
                    JsonElement jsonelement = packType.parse(reader);
                    if (jsonelement != null)
                        jsonelement.getAsJsonObject().addProperty("forge:registry_name", key.location().toString());
                    return decoder.parse(jsonOps, jsonelement).map(RegistryResourceAccess.ParsedEntry::createWithoutId);
                } catch(RuntimeException e) {
                    return DataResult.error("Failed to load [" + key.location() + "] due to [" + e + "]");
                }
            }

            private static String registryDirPath(ResourceLocation p_214240_) {
                return net.minecraftforge.common.ForgeHooks.prefixNamespace(p_214240_); // FORGE: add non-vanilla registry namespace to loader directory, same format as tag directory (see net.minecraft.tags.TagManager#getTagDir(ResourceKey))
            }

            @Contract("_, _ -> new")
            private static <E> @NotNull ResourceLocation elementPath(@NotNull ResourceKey<E> resourceKey, String suffix) {
                return new ResourceLocation(resourceKey.location().getNamespace(), registryDirPath(resourceKey.registry()) + "/" + resourceKey.location().getPath() + suffix);
            }

            public String toString() {
                return "ResourceAccess[" + p_195882_ + "]";
            }
        };
    }
}
