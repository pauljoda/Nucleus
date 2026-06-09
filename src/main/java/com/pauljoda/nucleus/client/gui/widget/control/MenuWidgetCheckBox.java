package com.pauljoda.nucleus.client.gui.widget.control;

import com.pauljoda.nucleus.util.ClientUtils;
import com.pauljoda.nucleus.client.gui.MenuBase;
import com.pauljoda.nucleus.client.gui.widget.BaseWidget;
import com.pauljoda.nucleus.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

import java.awt.*;

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
public abstract class MenuWidgetCheckBox extends BaseWidget {
    // Variables
    protected int u, v;
    protected boolean selected;
    protected String label;

    /**
     * Main constructor for check boxes
     * <p>
     * IMPORTANT: You must put the selected texture directly to the right of this one in the texture for it to work
     *
     * @param parent The parent
     * @param x      The x pos
     * @param y      The y pos
     * @param u      The texture u pos
     * @param v      The texture v pos
     * @param text   The text to display to the right
     */
    public MenuWidgetCheckBox(MenuBase<?> parent, int x, int y, int u, int v, boolean initialValue, String text) {
        super(parent, x, y);
        this.u = u;
        this.v = v;
        selected = initialValue;
        label = ClientUtils.translate(text);
    }

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * Called when there is a change in state, use this to set the value on what this controls
     *
     * @param value The current value of this component
     */
    protected abstract void setValue(boolean value);

    /*******************************************************************************************************************
     * BaseComponent                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Called when the mouse is pressed
     *
     * @param x      Mouse X Position
     * @param y      Mouse Y Position
     * @param button Mouse Button
     */
    @Override
    public void mouseDown(double x, double y, int button) {
        if (x > xPos && x < xPos + 10 && y > yPos && y < yPos + 10) {
            selected = !selected;
            setValue(selected);
        }
    }

    /**
     * Called to render the component
     */
    @Override
    public void render(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(xPos, yPos);
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, parent.textureLocation, 0, 0,
                selected ? u + 10 : u, v, 10, 10, 256, 256);
        matrixStack.popMatrix();
    }

    /**
     * Called after base render, is already translated to guiLeft and guiTop, just move offset
     */
    @Override
    public void renderOverlay(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(xPos + 10, yPos);
        RenderUtils.setColor(Color.darkGray);//Minecraft doesn't play nice with GL, so we will just set our own color
        RenderUtils.text(graphics, fontRenderer, label, 2, 1, Color.darkGray.getRGB(), false);
        RenderUtils.restoreColor();
        matrixStack.popMatrix();
    }

    /**
     * Used to find how wide this is
     *
     * @return How wide the component is
     */
    @Override
    public int getWidth() {
        return 10 + fontRenderer.width(label);
    }

    /**
     * Used to find how tall this is
     *
     * @return How tall the component is
     */
    @Override
    public int getHeight() {
        return 10;
    }

    /*******************************************************************************************************************
     * Accessors/Mutators                                                                                              *
     *******************************************************************************************************************/

    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
