package com.ygdevs.notjustjson.datagen;

import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.ygdevs.notjustjson.util.FileType;
import com.ygdevs.notjustjson.util.FileTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class PackTypedWorldgenReport implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DataGenerator generator;

    public PackTypedWorldgenReport(DataGenerator dataGenerator) {
        this.generator = dataGenerator;
    }

    public void run(@NotNull CachedOutput cachedOutput) {
        RegistryAccess registryaccess = RegistryAccess.BUILTIN.get();
        for (FileType<?> fileType : FileTypeRegistry.ordered()) {
            RegistryAccess.knownRegistries().forEach((registryData) -> this.dumpRegistryCap(cachedOutput, registryaccess, fileType, registryData));
        }
    }

    private <T, F> void dumpRegistryCap(CachedOutput cachedOutput, @NotNull RegistryAccess registryAccess, FileType<F> fileType, RegistryAccess.@NotNull RegistryData<T> registryData) {
        ResourceKey<? extends Registry<T>> resourcekey = registryData.key();
        Registry<T> registry = registryAccess.ownedRegistryOrThrow(resourcekey);
        DataGenerator.PathProvider datagenerator$pathprovider = this.generator.createPathProvider(DataGenerator.Target.REPORTS, net.minecraftforge.common.ForgeHooks.prefixNamespace(resourcekey.location())); // FORGE: Custom data-pack registries are prefixed with their namespace

        DynamicOps<F> dynamicOps = RegistryOps.create(fileType.ops(), registryAccess);
        for (Map.Entry<ResourceKey<T>, T> entry : registry.entrySet()) {
            dumpValue(datagenerator$pathprovider.file(entry.getKey().location(), fileType.name()), cachedOutput, dynamicOps, fileType, registryData.codec(), entry.getValue());
        }

    }

    private static <E, F> void dumpValue(Path path, CachedOutput cachedOutput, DynamicOps<F> dynamicOps, FileType<F> fileType, @NotNull Encoder<E> eEncoder, E e) {
        try {
            Optional<F> optional = eEncoder.encodeStart(dynamicOps, e).resultOrPartial((error) -> LOGGER.error("Couldn't serialize element {}: {}", path, error));
            if (optional.isPresent()) {
                saveStable(cachedOutput, optional.get(), fileType, path);
            }
        } catch (IOException ioexception) {
            LOGGER.error("Couldn't save element {}", path, ioexception);
        }

    }

    static <T> void saveStable(@NotNull CachedOutput cachedOutput, @NotNull T o, @NotNull FileType<T> fileType, Path path) throws IOException {
        byte[] bytes = fileType.serializer().apply(o);
        cachedOutput.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
    }

    public @NotNull String getName() {
        return "Typed Worldgen";
    }
}