package com.pauljoda.nucleus.client;

import com.pauljoda.nucleus.Nucleus;
import com.pauljoda.nucleus.client.events.ToolTipEvent;
import com.pauljoda.nucleus.util.TimeUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ClientEvents {

    @SubscribeEvent
    public static void onClientLoad(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new ToolTipEvent());
    }
}
