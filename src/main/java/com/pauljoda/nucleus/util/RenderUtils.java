package com.pauljoda.nucleus.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This file was created for Nucleus - Java
 * <p>
 * Nucleus - Java is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * <a href="http://creativecommons.org/licenses/by-nc-sa/4.0/">License</a>
 *
 * @author Paul Davis - pauljoda
 * @since 2/6/2017
 */
public class RenderUtils {
    private static final int WHITE = Color.WHITE.getRGB();
    private static final ThreadLocal<Deque<Integer>> COLOR_STACK = ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<Integer> CURRENT_COLOR = ThreadLocal.withInitial(() -> WHITE);

    // Resource Locations
    public static final Identifier MC_BLOCKS_RESOURCE_LOCATION =
            TextureAtlas.LOCATION_BLOCKS;
    /*public static final Identifier MC_ITEMS_RESOURCE_LOCATION =
            Identifier.parse("textures/atlas/items.png");*/

    /*******************************************************************************************************************
     * Render Helpers                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Used to bind a texture to the render manager
     *
     * @param resource The resource to bind
     */
    public static void bindTexture(Identifier resource) {
        Minecraft.getInstance().getTextureManager().getTexture(resource);
    }

    /**
     * Used to bind the MC item sheet
     * No longer used as items are now part of Block Sheet
     */
    @Deprecated
    public static void bindMinecraftItemSheet() {
        bindTexture(MC_BLOCKS_RESOURCE_LOCATION);
    }

    /**
     * Used to bind the MC blocks sheet
     */
    public static void bindMinecraftBlockSheet() {
        bindTexture(MC_BLOCKS_RESOURCE_LOCATION);
    }

    /**
     * Set the current GUI tint color. Minecraft 26.1 records GUI render state objects instead of reading global GL
     * color, so callers must render through the helpers below for this tint to affect extracted elements.
     *
     * @param color The color to set
     */
    public static void setColor(Color color) {
        CURRENT_COLOR.set(color.getRGB());
    }

    /**
     * Sets the color back to full white (normal)
     */
    public static void restoreColor() {
        setColor(new Color(255, 255, 255));
    }

    /**
     * Used to prepare the rendering state. For basic stuff that you want things to behave on
     */
    public static void prepareRenderState() {
        COLOR_STACK.get().push(CURRENT_COLOR.get());
    }

    /**
     * Un-does the prepare state
     */
    public static void restoreRenderState() {
        Deque<Integer> colorStack = COLOR_STACK.get();
        CURRENT_COLOR.set(colorStack.isEmpty() ? WHITE : colorStack.pop());
    }

    public static int currentColor() {
        return CURRENT_COLOR.get();
    }

    public static int opaque(int color) {
        return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }

    public static void blit(GuiGraphicsExtractor graphics, RenderPipeline renderPipeline, Identifier texture,
                            int x, int y, float u, float v, int width, int height,
                            int textureWidth, int textureHeight) {
        graphics.blit(renderPipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight,
                currentColor());
    }

    public static void blit(GuiGraphicsExtractor graphics, RenderPipeline renderPipeline, Identifier texture,
                            int x, int y, float u, float v, int width, int height,
                            int srcWidth, int srcHeight, int textureWidth, int textureHeight) {
        graphics.blit(renderPipeline, texture, x, y, u, v, width, height, srcWidth, srcHeight,
                textureWidth, textureHeight, currentColor());
    }

    public static void blitSprite(GuiGraphicsExtractor graphics, TextureAtlasSprite sprite,
                                  int x, int y, int width, int height) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height, currentColor());
    }

    public static void text(GuiGraphicsExtractor graphics, Font font, String text, int x, int y,
                            int color, boolean dropShadow) {
        graphics.text(font, text, x, y, opaque(color), dropShadow);
    }
}
