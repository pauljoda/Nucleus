package com.pauljoda.nucleus;

import com.mojang.logging.LogUtils;
import com.pauljoda.nucleus.client.ClientEvents;
import com.pauljoda.nucleus.common.CommonEvents;
import com.pauljoda.nucleus.manager.NetworkManager;
import com.pauljoda.nucleus.manager.EventManager;
import com.pauljoda.nucleus.registration.NucleusDataComponents;
import com.pauljoda.nucleus.registration.NucleusLootFunctions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Nucleus.MODID)
public class Nucleus {
    public static final String MODID = "nucleus_pauljoda";
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * The INSTANCE of the proxy
     */
    public static CommonEvents proxy;


    public Nucleus(IEventBus modEventBus) {
        NucleusLootFunctions.LOOT_FUNCTIONS.register(modEventBus);
        NucleusDataComponents.COMPONENTS.register(modEventBus);
        modEventBus.addListener(Config::onLoad);
        modEventBus.addListener(NetworkManager::init);
        modEventBus.addListener(ClientEvents::onClientLoad);
        EventManager.init();
    }
}
