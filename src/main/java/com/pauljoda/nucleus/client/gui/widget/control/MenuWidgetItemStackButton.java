package com.pauljoda.nucleus.client.gui.widget.control;

import com.pauljoda.nucleus.client.gui.MenuBase;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

/**
 * This file was created for Nucleus
 * <p>
 * Nucleus is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/12/2017
 */
public abstract class MenuWidgetItemStackButton extends MenuWidgetButton {
    // Variables
    protected ItemStack displayStack;

    /**
     * Constructor for itemstack button
     *
     * @param stack The stack to display
     */
    public MenuWidgetItemStackButton(MenuBase<?> parent, int x, int y, int u, int v, int w, int h, ItemStack stack) {
        super(parent, x, y, u, v, w, h, null);
        displayStack = stack;
    }

    /**
     * Called after base render, is already translated to guiLeft and guiTop, just move offset
     */
    @Override
    public void renderOverlay(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        super.renderOverlay(graphics, guiLeft, guiTop, mouseX, mouseY);
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(xPos, yPos);

        graphics.item(displayStack, (width / 2) - 8, (height / 2) - 8);

        matrixStack.popMatrix();
    }

    /*******************************************************************************************************************
     * Accessors/Mutators                                                                                              *
     *******************************************************************************************************************/

    public ItemStack getDisplayStack() {
        return displayStack;
    }

    public void setDisplayStack(ItemStack displayStack) {
        this.displayStack = displayStack;
    }
}
