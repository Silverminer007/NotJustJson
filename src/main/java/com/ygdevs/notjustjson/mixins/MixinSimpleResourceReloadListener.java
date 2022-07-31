package com.ygdevs.notjustjson.mixins;

import com.google.gson.JsonElement;
import com.ygdevs.notjustjson.util.FileType;
import com.ygdevs.notjustjson.util.FileTypeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

@Mixin(SimpleJsonResourceReloadListener.class)
public class MixinSimpleResourceReloadListener {
    @Shadow
    @Final
    private String directory;
    @Shadow
    @Final
    private static Logger LOGGER;

    @SuppressWarnings({"unchecked", "raw"})
    @Inject(at = @At(value = "RETURN"), method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/Map;", locals = LocalCapture.CAPTURE_FAILSOFT)
    private void notjustjson_inject_prepare(ResourceManager p_10771_, ProfilerFiller p_10772_, CallbackInfoReturnable<Map<ResourceLocation, JsonElement>> cir, Map map) {
        for (FileType<?> fileType : FileTypeRegistry.ordered()) {
            if(fileType.name().equals("json")) {
                continue;
            }
            String suffix = "." + fileType.name();
            int pathSuffixLength = suffix.length();
            for (Map.Entry<ResourceLocation, Resource> entry : p_10771_.listResources(this.directory, (p_215600_) -> p_215600_.getPath().endsWith(suffix)).entrySet()) {
                ResourceLocation key = entry.getKey();
                String path = key.getPath();
                ResourceLocation keyWithoutSuffix = new ResourceLocation(key.getNamespace(), path.substring(0, path.length() - pathSuffixLength));

                try {
                    Reader resourceReader = entry.getValue().openAsReader();
                    try {
                        if(map.containsKey(keyWithoutSuffix)) {
                            LOGGER.error("Failed to add data file {} from {}, because the key already existed with another suffix", keyWithoutSuffix, key);
                        } else {
                            map.put(keyWithoutSuffix, fileType.parse(resourceReader));
                        }
                    } catch (Throwable throwable1) {
                        try {
                            resourceReader.close();
                        } catch (Throwable throwable) {
                            throwable1.addSuppressed(throwable);
                        }
                        throw throwable1;
                    }

                    resourceReader.close();
                } catch (IllegalArgumentException | IOException exception) {
                    LOGGER.error("Couldn't parse data file {} from {}", keyWithoutSuffix, key, exception);
                }
            }
        }
    }
}