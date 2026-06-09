package com.pauljoda.nucleus.client.gui.widget.display;

import com.pauljoda.nucleus.client.gui.MenuBase;
import com.pauljoda.nucleus.client.gui.widget.BaseWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.inventory.Slot;

/**
 * This file was created for Nucleus
 * <p>
 * Nucleus is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/16/2017
 */
public class MenuWidgetTabSlotHolder extends BaseWidget {
    protected Slot heldSlot;
    protected int shownX, shownY;
    protected MenuTab parentTab;
    protected MenuWidgetTexture slotTexture;
    public boolean doRender;

    /**
     * Creates an object that will move the physical slot when should render
     * <p>
     * This object will move the container slot, but also needs the texture to render
     *
     * @param parentGui The parent gui
     * @param x         The component x
     * @param y         The component y
     * @param heldSlot  The slot to move about
     */
    public MenuWidgetTabSlotHolder(MenuBase<?> parentGui, int x, int y, Slot heldSlot, int slotX, int slotY, int u, int v, MenuTab parentTab) {
        super(parentGui, x, y);
        this.heldSlot = heldSlot;
        this.shownX = slotX;
        this.shownY = slotY;
        this.parentTab = parentTab;
        slotTexture = new MenuWidgetTexture(parent, x - 1, y - 1, u, v, 18, 18);
    }

//    /**
//     * Called by parent tab to move around
//     * @param doRender Do the render
//     */
//    public void moveSlots(boolean doRender) {
//        this.doRender = doRender;
//        if(doRender) {
//            heldSlot.xPos = shownX;
//            heldSlot.yPos = shownY;
//        } else {
//            heldSlot.xPos = -10000;
//            heldSlot.yPos = -10000;
//        }
//    }

    /**
     * Called to render the component
     */
    @Override
    public void render(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        if (doRender) {
            slotTexture.render(graphics, guiLeft, guiTop, mouseX, mouseY);
        }
    }

    /**
     * Called after base render, is already translated to guiLeft and guiTop, just move offset
     */
    @Override
    public void renderOverlay(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        // No op
    }

    /**
     * Used to find how wide this is
     *
     * @return How wide the component is
     */
    @Override
    public int getWidth() {
        return 18;
    }

    /**
     * Used to find how tall this is
     *
     * @return How tall the component is
     */
    @Override
    public int getHeight() {
        return 18;
    }
}
