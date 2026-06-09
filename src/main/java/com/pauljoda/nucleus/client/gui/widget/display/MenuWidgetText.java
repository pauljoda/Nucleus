package com.pauljoda.nucleus.client.gui.widget.display;

import com.pauljoda.nucleus.util.ClientUtils;
import com.pauljoda.nucleus.client.gui.MenuBase;
import com.pauljoda.nucleus.client.gui.widget.BaseWidget;
import com.pauljoda.nucleus.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import javax.annotation.Nullable;
import java.awt.*;

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
public class MenuWidgetText extends BaseWidget {
    // Variables
    protected String label;
    protected int colorDefault = Color.DARK_GRAY.getRGB();
    protected Color color;

    protected boolean dropShadow = false;

    /**
     * Creates the text component
     *
     * @param parent The gui parent
     * @param x      The x pos
     * @param y      The y pos
     * @param label  The string to render
     * @param color  Optional color
     */
    public MenuWidgetText(MenuBase<?> parent, int x, int y, String label, @Nullable Color color) {
        super(parent, x, y);
        this.label = ClientUtils.translate(label);
        this.color = color;
    }

    /**
     * Creates the text component
     *
     * @param parent The gui parent
     * @param x      The x pos
     * @param y      The y pos
     * @param label  The string to render
     * @param color  Optional color
     * @param shadow Draw drop shadow
     */
    public MenuWidgetText(MenuBase<?> parent, int x, int y, String label, @Nullable Color color, boolean shadow) {
        super(parent, x, y);
        this.label = ClientUtils.translate(label);
        this.color = color;
        this.dropShadow = shadow;
    }

    /*******************************************************************************************************************
     * BaseComponent                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Called to render the component
     */
    @Override
    public void render(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        // Text is rendered in the overlay pass so labels appear above base widgets.
    }

    /**
     * Called after base render, is already translated to guiLeft and guiTop, just move offset
     */
    @Override
    public void renderOverlay(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();

        matrixStack.translate(xPos, yPos);
        RenderUtils.prepareRenderState();


        if (color != null)
            RenderUtils.setColor(color);
        else
            RenderUtils.restoreColor();

        RenderUtils.text(graphics, fontRenderer, label, 0, 0,
                color != null ? color.getRGB() : colorDefault, dropShadow);

        RenderUtils.restoreColor();
        RenderUtils.restoreRenderState();

        matrixStack.popMatrix();
    }

    /**
     * Used to find how wide this is
     *
     * @return How wide the component is
     */
    @Override
    public int getWidth() {
        return fontRenderer.width(label);
    }

    /**
     * Used to find how tall this is
     *
     * @return How tall the component is
     */
    @Override
    public int getHeight() {
        return 7;
    }

    /*******************************************************************************************************************
     * Accessors/Mutators                                                                                              *
     *******************************************************************************************************************/

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isDropShadow() {
        return dropShadow;
    }

    public void setDropShadow(boolean dropShadow) {
        this.dropShadow = dropShadow;
    }
}
