package com.pauljoda.nucleus.client.gui.widget;

import com.pauljoda.nucleus.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

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
public class NinePatchRenderer {
    // Variables
    protected int u, v, cellSize;
    protected Identifier patchLocation;

    /**
     * Creates a renderer with given options
     * <p>
     * Texture must be in the following pattern. Cell size is how many pixels each box is (relative to x256)
     * <p>
     * *---*---*---*
     * |   |   |   |
     * |   |   |   |
     * *---*---*---*
     * |   |   |   |
     * |   |   |   |
     * *---*---*---*
     * |   |   |   |
     * |   |   |   |
     * *---*---*---*
     * <p>
     * Corners will render one to one
     * Edges will be stretched on their axis
     * Middle will be expanded in both axis (must be solid color)
     *
     * @param U       The texture U location
     * @param V       The texture V location
     * @param size    The cell size
     * @param texture The texture location
     */
    public NinePatchRenderer(int U, int V, int size, Identifier texture) {
        u = U;
        v = V;
        cellSize = size;
        patchLocation = texture;
    }

    /**
     * Partial Rendering Code
     * <p>
     * This can be overwritten in a new INSTANCE of the class to disable certain parts from rendering or to give them a
     * different behavior. One INSTANCE would be for a tab, you can prevent the left edge from rendering in that way
     */

    // Corners
    protected void renderTopLeftCorner(GuiGraphicsExtractor graphics) {
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, patchLocation, 0, 0, u, v, cellSize, cellSize, 256, 256);
    }

    protected void renderTopRightCorner(GuiGraphicsExtractor graphics, int width) {
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, patchLocation, width - cellSize, 0, u + cellSize + cellSize, v, cellSize, cellSize, 256, 256);
    }

    protected void renderBottomLeftCorner(GuiGraphicsExtractor graphics, int height) {
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, patchLocation, 0, height - cellSize, u, v + cellSize + cellSize, cellSize, cellSize, 256, 256);
    }

    protected void renderBottomRightCorner(GuiGraphicsExtractor graphics, int width, int height) {
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, patchLocation, width - cellSize, height - cellSize, u + cellSize + cellSize, v + cellSize + cellSize, cellSize, cellSize, 256, 256);
    }

    // Edges
    protected void renderTopEdge(GuiGraphicsExtractor graphics, int width) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(cellSize, 0);
        matrixStack.scale(width - (cellSize * 2), 1);
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, patchLocation, 0, 0, u + cellSize, v, 1, cellSize, 256, 256);
        matrixStack.popMatrix();
    }

    protected void renderBottomEdge(GuiGraphicsExtractor graphics, int width, int height) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(cellSize, height - cellSize);
        matrixStack.scale(width - (cellSize * 2), 1);
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, patchLocation, 0, 0, u + cellSize, v + cellSize + cellSize, 1, cellSize, 256, 256);
        matrixStack.popMatrix();
    }

    protected void renderLeftEdge(GuiGraphicsExtractor graphics, int height) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(0, cellSize);
        matrixStack.scale(1, height - (cellSize * 2));
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, patchLocation, 0, 0, u, v + cellSize, cellSize, 1, 256, 256);
        matrixStack.popMatrix();
    }

    protected void renderRightEdge(GuiGraphicsExtractor graphics, int width, int height) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(width - cellSize, cellSize);
        matrixStack.scale(1, height - (cellSize * 2));
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, patchLocation, 0, 0, u + cellSize + cellSize, v + cellSize, cellSize, 1, 256, 256);
        matrixStack.popMatrix();
    }

    // Background
    protected void renderBackground(GuiGraphicsExtractor graphics, int width, int height) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(cellSize - 1, cellSize - 1);
        matrixStack.scale(width - (cellSize * 2) + 2, height - (cellSize * 2) + 2);
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, patchLocation, 0, 0, u + cellSize, v + cellSize, 1, 1, 256, 256);
        matrixStack.popMatrix();
    }

    public void render(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        render(graphics, x, y, width, height, null);
    }

    /**
     * Main render call. This must be called in the parent gui to render the box.
     * <p>
     * WARNING: Will bind texture to sheet, make sure you rebind afterwards or do this first
     *
     * @param x      Screen X Position
     * @param y      Screen Y Position
     * @param width  Width
     * @param height Height
     * @param color  Color to render
     */
    public void render(GuiGraphicsExtractor graphics, int x, int y, int width, int height, Color color) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        if (color != null)
            RenderUtils.setColor(color);
        if (patchLocation != null)
            RenderUtils.bindTexture(patchLocation);
        matrixStack.translate(x, y);
        renderBackground(graphics, width, height);
        renderTopEdge(graphics, width);
        renderBottomEdge(graphics, width, height);
        renderRightEdge(graphics, width, height);
        renderLeftEdge(graphics, height);
        renderTopLeftCorner(graphics);
        renderTopRightCorner(graphics, width);
        renderBottomLeftCorner(graphics, height);
        renderBottomRightCorner(graphics, width, height);
        matrixStack.popMatrix();
    }
}
