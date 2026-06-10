package com.pauljoda.nucleus.testharness.client;

import com.pauljoda.nucleus.client.gui.MenuBase;
import com.pauljoda.nucleus.client.gui.widget.BaseWidget;
import com.pauljoda.nucleus.client.gui.widget.display.MenuTabCollection;
import com.pauljoda.nucleus.client.gui.widget.display.MenuWidgetText;
import com.pauljoda.nucleus.testharness.NucleusTestHarness;
import com.pauljoda.nucleus.testharness.common.NucleusGuiTestMenu;
import com.pauljoda.nucleus.testharness.registration.NucleusTestBlocks;
import java.awt.Color;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Development-only screen that exercises Nucleus GUI widgets and expanding tabs.
 */
public class NucleusGuiTestScreen extends MenuBase<NucleusGuiTestMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            NucleusTestHarness.MODID,
            "textures/gui/gui_test.png"
    );

    public NucleusGuiTestScreen(NucleusGuiTestMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166, TEXTURE);
    }

    @Override
    protected void addComponents() {
        components.add(new MenuWidgetText(this, 8, 20, "gui.nucleus_test.instructions", Color.DARK_GRAY));
        components.add(new MenuWidgetText(this, 8, 35, "gui.nucleus_test.right_tab_hint", new Color(0x285A8C)));
        components.add(new MenuWidgetText(this, 8, 50, "gui.nucleus_test.left_tab_hint", new Color(0x6A3F1F)));
        components.add(new MenuWidgetText(this, 8, 68, "gui.nucleus_test.status_ready", new Color(0x2F7D32)));
    }

    @Override
    protected void addRightTabs(MenuTabCollection tabs) {
        tabs.addTab(
                List.of(
                        text(32, 8, "gui.nucleus_test.right_tab_title", new Color(0x123A5A)),
                        text(32, 24, "gui.nucleus_test.right_tab_line_one", Color.DARK_GRAY),
                        text(32, 38, "gui.nucleus_test.right_tab_line_two", Color.DARK_GRAY)
                ),
                150,
                72,
                176,
                0,
                new ItemStack(NucleusTestBlocks.GUI_TEST_BLOCK_ITEM.get())
        );
    }

    @Override
    protected void addLeftTabs(MenuTabCollection tabs) {
        tabs.addReverseTab(
                List.of(
                        text(8, 8, "gui.nucleus_test.left_tab_title", new Color(0x5A2F12)),
                        text(8, 24, "gui.nucleus_test.left_tab_line_one", Color.DARK_GRAY),
                        text(8, 38, "gui.nucleus_test.left_tab_line_two", Color.DARK_GRAY)
                ),
                150,
                72,
                176,
                88,
                new ItemStack(Items.REDSTONE_TORCH)
        );
    }

    private BaseWidget text(int x, int y, String translationKey, Color color) {
        return new MenuWidgetText(this, x, y, translationKey, color);
    }
}
