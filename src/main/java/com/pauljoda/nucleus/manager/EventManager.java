package com.pauljoda.nucleus.manager;

import com.pauljoda.nucleus.event.CraftingEvents;
import com.pauljoda.nucleus.common.CommonEvents;
import com.pauljoda.nucleus.util.TimeUtils;
import net.neoforged.neoforge.common.NeoForge;

public class EventManager {
    /**
     * Registers the events
     */
    public static void init() {
        registerEvent(new TimeUtils());
        registerEvent(new CraftingEvents());
        registerEvent(new CommonEvents());
    }

    /**
     * Registers an event to the event registry
     *
     * @param event The event to register
     */
    public static void registerEvent(Object event) {
        NeoForge.EVENT_BUS.register(event);
    }
}
