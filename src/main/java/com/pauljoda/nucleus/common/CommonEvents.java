package com.pauljoda.nucleus.common;

import com.pauljoda.nucleus.Nucleus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod.EventBusSubscriber(modid = Nucleus.MODID, value = Dist.DEDICATED_SERVER)
public class CommonEvents {

    /**
     * This method is called on initialization.
     */
    @SubscribeEvent
    public static void ServerLoad(ServerStartingEvent event) {
    }
}