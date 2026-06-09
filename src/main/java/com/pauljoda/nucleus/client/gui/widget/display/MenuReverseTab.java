package com.pauljoda.nucleus.client.gui.widget.display;

import com.pauljoda.nucleus.client.gui.MenuBase;
import com.pauljoda.nucleus.client.gui.widget.NinePatchRenderer;
import com.pauljoda.nucleus.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This file was created for Nucleus
 * <p>
 * Nucleus is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/13/2017
 */
public class MenuReverseTab extends MenuTab {

    /**
     * Creates a Gui Tab
     * <p>
     * IMPORTANT: Texture should be a full nine patch renderer minus the right column of cells
     * See NinePatchRenderer construction for more info
     *
     * @param parent       The parent GUI
     * @param x            The x pos
     * @param y            The y pos
     * @param u            The texture u, this is the first cell for the nine patch renderer
     * @param v            The texture v, this is the first cell for the nine patch renderer
     * @param exWidth      The expanded width
     * @param exHeight     The expanded height
     * @param displayStack The stack to display
     */
    public MenuReverseTab(MenuBase<?> parent, int x, int y, int u, int v, int exWidth, int exHeight, @Nullable
    ItemStack displayStack) {
        super(parent, x, y, u, v, exWidth, exHeight, displayStack);
        tabRenderer = new NinePatchRenderer(u, v, 8, parent.textureLocation);
    }

    /*******************************************************************************************************************
     * BaseComponent                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Called to render the component
     */
    @Override
    public void render(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();

        // Set targets to stun
        double targetWidth = isActive ? expandedWidth : FOLDED_SIZE;
        double targetHeight = isActive ? expandedHeight : FOLDED_SIZE;

        // Move size
        if (currentWidth != targetWidth)
            dWidth += (targetWidth - dWidth);
        if (currentHeight != targetHeight)
            dHeight += (targetHeight - dHeight);

        // Set size
        currentWidth = dWidth;
        currentHeight = dHeight;

        // Render the tab
        tabRenderer.render(graphics, -currentWidth, 0, currentWidth, currentHeight);

        // Render the stack, if available
        RenderUtils.restoreColor();
        if (stack != null) {
            matrixStack.pushMatrix();
            matrixStack.translate(0F, yPos);
            graphics.item(stack, -13, 2);
            matrixStack.popMatrix();
        }

        // Render the children
        if (areChildrenActive()) {
            matrixStack.translate(-expandedWidth, 0);
            children.forEach((component -> {
                component.render(graphics, -expandedWidth, 0, mouseX, mouseY);
                RenderUtils.restoreColor();
            }));
        }

        matrixStack.popMatrix();
    }

    /**
     * Called after base render, is already translated to guiLeft and guiTop, just move offset
     */
    @Override
    public void renderOverlay(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        // Render the children
        if (areChildrenActive()) {
            matrixStack.translate(-expandedWidth, 0);
            children.forEach((component -> {
                RenderUtils.prepareRenderState();
                component.renderOverlay(graphics, -expandedWidth, 0, mouseX, mouseY);
                RenderUtils.restoreColor();
                RenderUtils.restoreRenderState();
            }));
        }
    }

    /**
     * Used to check if the mouse if currently over the component
     * <p>
     * You must have the getWidth() and getHeight() functions defined for this to work properly
     *
     * @param mouseX Mouse X Position
     * @param mouseY Mouse Y Position
     * @return True if mouse if over the component
     */
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= xPos - currentWidth && mouseX < xPos && mouseY >= yPos && mouseY < yPos + getHeight();
    }
}
