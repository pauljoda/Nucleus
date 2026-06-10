package com.pauljoda.nucleus.testharness.common;

import com.pauljoda.nucleus.testharness.registration.NucleusTestMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/**
 * Slotless menu used to exercise Nucleus GUI rendering/widgets.
 */
public class NucleusGuiTestMenu extends AbstractContainerMenu {
    public NucleusGuiTestMenu(int containerId, Inventory inventory) {
        super(NucleusTestMenus.GUI_TEST_MENU.get(), containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
