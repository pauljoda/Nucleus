package com.pauljoda.nucleus.client.gui.widget.control;

import com.pauljoda.nucleus.helper.GuiHelper;
import com.pauljoda.nucleus.util.ClientUtils;
import com.pauljoda.nucleus.client.gui.MenuBase;
import com.pauljoda.nucleus.client.gui.widget.BaseWidget;
import com.pauljoda.nucleus.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This file was created for Nucleus
 * <p>
 * Nucleus is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * <p>
 * This class implements a button component for the menu, which includes features like
 * mouse hover detection and rendering the button and its overlay.
 *
 * @author Paul Davis - pauljoda
 * @since 2/12/2017
 */
public abstract class MenuWidgetButton extends BaseWidget {

    protected int u, v, width, height;
    protected String label;
    private boolean isOver = false;

    /**
     * Constructor for the button component
     * <p>
     * In your texture, you should put the hovered-over texture directly below the main texture passed
     *
     * @param parent The parent gui
     * @param xPos   The x position
     * @param yPos   The y position
     * @param uPos   The texture u position
     * @param vPos   The texture v position
     * @param w      The width
     * @param h      The height
     * @param text   The text to display, if any
     */
    public MenuWidgetButton(MenuBase<?> parent, int xPos, int yPos, int uPos, int vPos, int w, int h, @Nullable String text) {
        super(parent, xPos, yPos);
        u = uPos;
        v = vPos;
        width = w;
        height = h;
        label = (text != null) ? ClientUtils.translate(text) : null;
    }

    /**
     * Defines action to be taken when button is pressed
     */
    protected abstract void doAction();

    /**
     * @return Width of the button
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * @return Height of the button
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Triggers button action if the mouse click was within button boundaries
     *
     * @param mouseX Mouse X Position
     * @param mouseY Mouse Y Position
     * @param button Mouse Button
     */
    @Override
    public void mouseDown(double mouseX, double mouseY, int button) {
        if (mouseX >= xPos && mouseX < xPos + width && mouseY >= yPos && mouseY < yPos + height) {
            GuiHelper.playButtonSound();
            doAction();
        }
    }

    /**
     * Checks if the mouse is over the button and sets `isOver` accordingly
     *
     * @param mouseX Mouse X Position
     * @param mouseY Mouse Y Position
     * @return Whether the mouse is over the button
     */
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        isOver = mouseX >= xPos && mouseX < xPos + width && mouseY >= yPos && mouseY < yPos + height;
        return isOver;
    }

    /**
     * Renders the button component
     */
    @Override
    public void render(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        RenderUtils.prepareRenderState();
        RenderUtils.bindTexture(parent.textureLocation);
        matrixStack.translate(xPos, yPos);
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, parent.textureLocation, 0, 0, u,
                isOver ? v + height : v, width, height, 256, 256);
        RenderUtils.restoreRenderState();
        matrixStack.popMatrix();
    }

    /**
     * Renders the button overlay, including the label if it is not null
     */
    @Override
    public void renderOverlay(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        if (label != null) {
            var matrixStack = graphics.pose();
            matrixStack.pushMatrix();
            RenderUtils.prepareRenderState();
            RenderUtils.restoreColor();
            float size = fontRenderer.width(label);
            matrixStack.translate(xPos + (width / 2F - size / 2F), yPos + 6);
            RenderUtils.text(graphics, fontRenderer, label, 0, 0, Color.DARK_GRAY.getRGB(), false);
            RenderUtils.restoreColor();
            RenderUtils.restoreRenderState();
            matrixStack.popMatrix();
        }
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
