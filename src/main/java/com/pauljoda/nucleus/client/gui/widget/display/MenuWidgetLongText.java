package com.pauljoda.nucleus.client.gui.widget.display;

import com.pauljoda.nucleus.helper.GuiHelper;
import com.pauljoda.nucleus.util.ClientUtils;
import com.pauljoda.nucleus.client.gui.MenuBase;
import com.pauljoda.nucleus.client.gui.widget.BaseWidget;
import com.pauljoda.nucleus.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

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
public class MenuWidgetLongText extends BaseWidget {
    // Variables
    protected int width, height, u, v, textScale;
    protected boolean upSelected, downSelected = false;
    protected int colorDefault = 4210752;
    protected int lineWidth, currentLine;
    protected List<String> lines;

    /**
     * Creates the long text object
     * <p>
     * IMPORTANT: The up and down arrows should be together, up on top down on bottom. Provide u and v for top left of top
     * Arrow size should be 15x8 pixels
     *
     * @param parent    The parent GUI
     * @param x         The x pos
     * @param y         The y pos
     * @param w         The width
     * @param h         The height
     * @param u         The arrows u
     * @param v         The arrows v
     * @param text      The text to display
     * @param textScale The text scale, default size is 100
     */
    public MenuWidgetLongText(MenuBase<?> parent, int x, int y, int w, int h, int u, int v, String text, int textScale) {
        super(parent, x, y);
        this.width = w;
        this.height = h;
        this.u = u;
        this.v = v;
        this.textScale = textScale;

        currentLine = 0;
        lineWidth = (100 / textScale) * (width - 18);

        lines = new ArrayList<>();

        // Setup Lines
        text = ClientUtils.translate(text);
        if (fontRenderer.width(text) < lineWidth) {
            lines.add(text);
        } else {
            String string = text;
            while (fontRenderer.width(string) > lineWidth) {
                String trimmed = fontRenderer.plainSubstrByWidth(string, lineWidth);

                int lastSpace = trimmed.lastIndexOf(" "); // Ensure full words
                if (lastSpace != -1)
                    trimmed = trimmed.substring(0, lastSpace);

                int newLine = trimmed.indexOf("\n"); // Break for new lines
                if (newLine != -1)
                    trimmed = trimmed.substring(0, newLine);

                lines.add(trimmed);

                string = string.substring(trimmed.length());

                if (string.charAt(0) == '\n') // Clear leading new lines
                    string = string.replaceFirst("\n", "");
                if (string.charAt(0) == ' ') // Clear leading spaces
                    string = string.replaceFirst(" ", "");
            }
            lines.add(string);
        }
    }

    /**
     * Used to get the last line to render on screen
     *
     * @return The last index to render
     */
    private int getLastLineToRender() {
        int maxOnScreen = height / ((textScale * 9) / 100);
        return Math.max(lines.size() - maxOnScreen, 0);
    }

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
    public boolean mouseClicked(double x, double y, int button) {
        if (GuiHelper.isInBounds(x, y, xPos + width - 15, yPos, xPos + width, yPos + 8)) {
            upSelected = true;
            currentLine -= 1;
            if (currentLine < 0)
                currentLine = 0;
            GuiHelper.playButtonSound();
        } else if (GuiHelper.isInBounds(x, y, xPos + width - 15, yPos + height - 8, (int) (x + width), yPos + height)) {
            downSelected = true;
            currentLine += 1;
            if (currentLine > getLastLineToRender())
                currentLine = getLastLineToRender();
            GuiHelper.playButtonSound();
        }
        return false;
    }

    /**
     * Called when the mouse button is over the component and released
     *
     * @param x      Mouse X Position
     * @param y      Mouse Y Position
     * @param button Mouse Button
     */
    @Override
    public boolean mouseReleased(double x, double y, int button) {
        upSelected = downSelected = false;
        return false;
    }

    /**
     * Called when the mouse is scrolled
     *
     * @param dir 1 for positive, -1 for negative
     */
    @Override
    public void mouseScrolled(int dir) {
        currentLine -= dir;
        if (currentLine < 0)
            currentLine = 0;
        if (currentLine > getLastLineToRender())
            currentLine = getLastLineToRender();
    }

    /**
     * Called to render the component
     */
    @Override
    public void render(GuiGraphics graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xPos, yPos, 0);
        RenderUtils.bindTexture(parent.textureLocation);

        graphics.blit(parent.textureLocation, width - 15, 0, u, v, 15, 8);
        graphics.blit(parent.textureLocation, width - 15, height - 7, u, v + 8, 15, 8);
        matrixStack.popPose();
    }

    /**
     * Called after base render, is already translated to guiLeft and guiTop, just move offset
     */
    @Override
    public void renderOverlay(GuiGraphics graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        matrixStack.pushPose();

        matrixStack.translate(xPos, yPos, 0);
        RenderUtils.prepareRenderState();

        int yPos = -9;
        int actualY = 0;
        matrixStack.scale(textScale / 100F, textScale / 100F, textScale / 100F);
        for (int x = currentLine; x < lines.size(); x++) {
            if (actualY + ((textScale * 9) / 100) > height)
                break;
            RenderUtils.restoreColor();
            String label = lines.get(x);

            graphics.drawString(font, label, 0, yPos + 9, 0xFFFFFF);
            yPos += 9;
            actualY += (textScale * 9) / 100;
        }

        matrixStack.popPose();
    }

    /**
     * Used to find how wide this is
     *
     * @return How wide the component is
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Used to find how tall this is
     *
     * @return How tall the component is
     */
    @Override
    public int getHeight() {
        return height;
    }


    /*******************************************************************************************************************
     * Accessors/Mutators                                                                                              *
     *******************************************************************************************************************/

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

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

    public int getTextScale() {
        return textScale;
    }

    public void setTextScale(int textScale) {
        this.textScale = textScale;
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(int currentLine) {
        this.currentLine = currentLine;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }
}
