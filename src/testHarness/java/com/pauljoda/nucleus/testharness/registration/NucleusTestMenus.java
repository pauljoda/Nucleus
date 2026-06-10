package com.pauljoda.nucleus.testharness.registration;

import com.pauljoda.nucleus.testharness.NucleusTestHarness;
import com.pauljoda.nucleus.testharness.common.NucleusGuiTestMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Development-only menus used by the Nucleus test harness.
 */
public final class NucleusTestMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, NucleusTestHarness.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<NucleusGuiTestMenu>> GUI_TEST_MENU = MENUS.register(
            "gui_test",
            () -> new MenuType<>(NucleusGuiTestMenu::new, FeatureFlags.VANILLA_SET)
    );

    private NucleusTestMenus() {
    }
}
