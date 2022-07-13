package com.ygdevs.notjustjson;

import com.mojang.logging.LogUtils;
import com.ygdevs.notjustjson.datagen.PackTypedWorldgenReport;
import com.ygdevs.notjustjson.util.PackTypeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.slf4j.Logger;

@Mod(NotJustJson.MODID)
public class NotJustJson {
    public static final String MODID = "notjustjson";
    public static final Logger LOGGER = LogUtils.getLogger();
    public NotJustJson() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        PackTypeRegistry.REGISTRY.register(bus);
        bus.addListener((GatherDataEvent event) -> event.getGenerator().addProvider(event.includeReports(), new PackTypedWorldgenReport(event.getGenerator())));
    }
}
