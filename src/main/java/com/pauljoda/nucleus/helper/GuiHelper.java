package com.pauljoda.nucleus.helper;

import com.pauljoda.nucleus.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.fluids.FluidStack;

import java.awt.*;

/**
 * This file was created for Nucleus - Java
 * <p>
 * Nucleus - Java is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/9/2017
 */
public class GuiHelper {

    /*******************************************************************************************************************
     * Sound                                                                                                           *
     *******************************************************************************************************************/

    /**
     * Used to play the button sound in a GUI
     */
    public static void playButtonSound() {
        Minecraft.getInstance().getSoundManager()
                .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /*******************************************************************************************************************
     * Render Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Used to set the color of the GL stack from an int value
     *
     * @param color The color value eg 0x000000
     */
    public static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 255) / 255F;
        float green = (color >> 8 & 255) / 255F;
        float blue = (color & 255) / 255F;
        RenderUtils.setColor(new Color(red, green, blue, 1.0F));
    }

    /**
     * Draws the given icon with optional cut
     *
     * @param icon   The texture
     * @param x      X pos
     * @param y      Y pos
     * @param width  keep width of icon
     * @param height keep height of icon
     * @param cut    0 is full icon, 16 is full cut
     */
    public static void drawIconWithCut(GuiGraphicsExtractor graphics, TextureAtlasSprite icon, int x, int y,
                                       int width, int height, int cut) {
        int clippedCut = Math.max(0, Math.min(height, cut));
        if (clippedCut >= height || width <= 0) {
            return;
        }

        graphics.blit(icon.atlasLocation(), x, y + clippedCut, x + width, y + height,
                icon.getU0(), icon.getU(width), icon.getV(clippedCut), icon.getV(height));
    }

    /**
     * Renders a fluid from the given tank
     *
     * @param fluid     The fluid to render
     * @param capacity  The capacity used to scale the fluid height
     * @param x         The x pos
     * @param y         The y pos
     * @param maxHeight Max height
     * @param maxWidth  Max width
     */
    public static void renderFluid(GuiGraphicsExtractor graphics, FluidStack fluid, int capacity,
                                   int x, int y, int maxHeight, int maxWidth) {
        if (!fluid.isEmpty()) {
            if (capacity <= 0) {
                return;
            }

            int scaledHeight = Math.max(1, Math.min(maxHeight, fluid.getAmount() * maxHeight / capacity));
            Identifier fluidId = BuiltInRegistries.FLUID.getKey(fluid.getFluid());
            Identifier spriteId = Identifier.fromNamespaceAndPath(fluidId.getNamespace(), "block/" + fluidId.getPath() + "_still");
            TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, spriteId));

            int top = y - scaledHeight;
            for (int drawY = top; drawY < y; drawY += 16) {
                int tileHeight = Math.min(16, y - drawY);
                for (int drawX = x; drawX < x + maxWidth; drawX += 16) {
                    int tileWidth = Math.min(16, x + maxWidth - drawX);
                    RenderUtils.blitSprite(graphics, sprite, drawX, drawY, tileWidth, tileHeight);
                }
            }
        }
    }

    /*******************************************************************************************************************
     * Helper Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Test if location is in bounds
     *
     * @param x xLocation
     * @param y yLocation
     * @param a Rectangle point a
     * @param b Rectangle point b
     * @param c Rectangle point c
     * @param d Rectangle point d
     *          (A,B)------------------
     *          -                  -
     *          -----------------(C,D)
     * @return True if in bounds
     */
    public static boolean isInBounds(double x, double y, int a, int b, int c, int d) {
        return x >= a && x <= c && y >= b && y <= d;
    }
}
