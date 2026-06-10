package com.pauljoda.nucleus.testharness.client;

import com.pauljoda.nucleus.testharness.registration.NucleusTestMenus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * Client-only registrations for the development test harness.
 */
public final class NucleusTestClientSetup {
    private NucleusTestClientSetup() {
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(NucleusTestMenus.GUI_TEST_MENU.get(), NucleusGuiTestScreen::new);
    }
}
