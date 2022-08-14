/*
 * Silverminer007
 * Copyright (c) 2022.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.ygdevs.notjustjson;

import com.mojang.logging.LogUtils;
import com.ygdevs.notjustjson.datagen.PackTypedWorldgenReport;
import com.ygdevs.notjustjson.util.FileTypeRegistry;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(NotJustJson.MODID)
public class NotJustJson {
    public static final String MODID = "notjustjson";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NotJustJson() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        FileTypeRegistry.REGISTRY.register(bus);
        bus.addListener((GatherDataEvent event) -> event.getGenerator().addProvider(event.includeReports(), new PackTypedWorldgenReport(event.getGenerator())));
    }
}
