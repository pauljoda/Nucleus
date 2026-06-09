package com.pauljoda.nucleus.client.gui;

import com.pauljoda.nucleus.client.gui.widget.BaseWidget;
import com.pauljoda.nucleus.client.gui.widget.display.MenuWidgetText;
import com.pauljoda.nucleus.client.gui.widget.display.MenuTabCollection;
import com.pauljoda.nucleus.util.ClientUtils;
import com.pauljoda.nucleus.util.RenderUtils;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.awt.*;
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
 * @since 2/12/2017
 */
public abstract class MenuBase<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    // Variables
    protected MenuWidgetText titleComponent;

    public Identifier textureLocation;

    protected List<BaseWidget> components = new ArrayList<>();
    protected MenuTabCollection rightTabs;
    protected MenuTabCollection leftTabs;

    /**
     * Main constructor for Guis
     *
     * @param inventory The container
     * @param width     The width of the gui
     * @param height    The height of the gui
     * @param title     The title of the gui
     * @param texture   The location of the background texture
     */
    public MenuBase(T inventory, Inventory playerInventory, Component title, int width, int height, Identifier texture) {
        super(inventory, playerInventory, title, width, height);
        this.textureLocation = texture;

        rightTabs = new MenuTabCollection(this, imageWidth + 1);
        leftTabs = new MenuTabCollection(this, -1);

        titleComponent = new MenuWidgetText(this,
                imageWidth / 2 - (font.width(ClientUtils.translate(title.getString())) / 2),
                3, title.getString(), null);
        components.add(titleComponent);

        addComponents();
        addRightTabs(rightTabs);
        addLeftTabs(leftTabs);

        components.add(rightTabs);
        components.add(leftTabs);
    }

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * This will be called after the GUI has been initialized and should be where you add all components.
     */
    protected abstract void addComponents();

    /*******************************************************************************************************************
     * GuiBase Methods                                                                                                 *
     *******************************************************************************************************************/

    /**
     * Adds the tabs to the right. Overwrite this if you want tabs on your GUI
     *
     * @param tabs List of tabs, put GuiTabs in
     */
    protected void addRightTabs(MenuTabCollection tabs) {
    }

    /**
     * Add the tabs to the left. Overwrite this if you want tabs on your GUI
     *
     * @param tabs List of tabs, put GuiReverseTabs in
     */
    protected void addLeftTabs(MenuTabCollection tabs) {
    }

    /*******************************************************************************************************************
     * Gui                                                                                                             *
     *******************************************************************************************************************/

    /**
     * Called when the mouse is clicked
     *
     * @param event       The mouse button event
     * @param doubleClick Whether this click is a double-click
     */
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int mouseButton = event.button();
        components.forEach((baseComponent -> {
            if (baseComponent.isMouseOver(mouseX - leftPos, mouseY - topPos)) {
                baseComponent.mouseDown(mouseX - leftPos, mouseY - topPos, mouseButton);
            }
        }));
        return super.mouseClicked(event, doubleClick);
    }

    /**
     * Called when the mouse releases a button
     *
     * @param event The mouse button event
     */
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        double mouseX = event.x();
        double mouseY = event.y();
        int state = event.button();
        components.forEach((baseComponent -> {
            if (baseComponent.isMouseOver(mouseX - leftPos, mouseY - topPos)) {
                baseComponent.mouseUp(mouseX - leftPos, mouseY - topPos, state);
            }
        }));
        return super.mouseReleased(event);
    }

    /**
     * Used to track when the mouse is clicked and dragged
     *
     * @param event       The mouse button event
     * @param xDragAmount The horizontal drag amount
     * @param yDragAmount The vertical drag amount
     */
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double xDragAmount, double yDragAmount) {
        double mouseX = event.x();
        double mouseY = event.y();
        int clickedMouseButton = event.button();
        components.forEach((baseComponent -> {
            if (baseComponent.isMouseOver(mouseX - leftPos, mouseY - topPos)) {
                baseComponent.mouseDrag(mouseX - leftPos, mouseY - topPos, clickedMouseButton, xDragAmount, yDragAmount);
            }
        }));
        return super.mouseDragged(event, xDragAmount, yDragAmount);
    }

    /**
     * Handle the mouse input
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double mouseDelta, double i) {
        if (mouseDelta != 0)
            components.forEach((baseComponent -> baseComponent.mouseScrolled(mouseDelta > 0 ? 1 : -1)));
        return super.mouseScrolled(mouseX, mouseY, mouseDelta, i);
    }

    /**
     * Called when a key is typed
     *
     * @param event The typed character event
     */
    @Override
    public boolean charTyped(CharacterEvent event) {
        char typedChar = (char) event.codepoint();
        components.forEach((baseComponent -> baseComponent.keyTyped(typedChar, event.codepoint())));
        return super.charTyped(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        drawTopLayer(graphics, mouseX, mouseY);
    }

    /**
     * Override to prevent vanilla label writing
     */
    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int p_97809_, int p_97810_) {

    }

    /**
     * Called to draw the background
     * <p>
     * Usually used to create the base on which to render things
     *
     * @param partialTicks partial ticks
     * @param mouseX       The mouse X
     * @param mouseY       The mouse Y
     */
    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        RenderUtils.prepareRenderState();
        matrixStack.translate(leftPos, topPos);

        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, this.textureLocation, 0, 0, 0, 0,
                imageWidth + 1, imageHeight + 1, 256, 256);

        components.forEach((baseComponent -> {
            RenderUtils.prepareRenderState();
            baseComponent.render(graphics, leftPos, topPos, mouseX - leftPos, mouseY - topPos);
            RenderUtils.restoreRenderState();
            RenderUtils.restoreColor();
        }));
        RenderUtils.restoreRenderState();
        matrixStack.popMatrix();
        super.extractContents(graphics, mouseX, mouseY, partialTicks);
    }

    /**
     * Used mainly to attach tool tips as they will always be on the top
     *
     * @param mouseX The Mouse X Position
     * @param mouseY The mouse Y Position
     */
    public void drawTopLayer(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        components.forEach((baseComponent -> {
            RenderUtils.prepareRenderState();

            // Render the base overlay
            matrixStack.pushMatrix();
            matrixStack.translate(leftPos, topPos);
            baseComponent.renderOverlay(graphics, leftPos, topPos, mouseX, mouseY);
            matrixStack.popMatrix();

            // Render the tooltip
            if (baseComponent.isMouseOver(mouseX - leftPos, mouseY - topPos))
                baseComponent.renderToolTip(graphics, mouseX, mouseY);

            RenderUtils.restoreColor();
            RenderUtils.restoreRenderState();
        }));
        matrixStack.popMatrix();
    }

    /*******************************************************************************************************************
     * Accessors/Mutators                                                                                              *
     *******************************************************************************************************************/

    /**
     * Used to get the leftPos
     *
     * @return Where the gui starts
     */
    public int screenLeft() {
        return leftPos;
    }

    /**
     * Used to get topPos
     *
     * @return Where the gui starts
     */
    public int screenTop() {
        return topPos;
    }

    /**
     * For some reason this isn't in vanilla
     *
     * @return The size of the gui
     */
    public int screenWidth() {
        return imageWidth;
    }

    /**
     * For some reason this isn't in vanilla
     *
     * @return The size of the gui
     */
    public int screenHeight() {
        return imageHeight;
    }

    /*******************************************************************************************************************
     * JEI                                                                                                             *
     *******************************************************************************************************************/

    /**
     * Returns a list of Rectangles that represent the areas covered by the GUI
     *
     * @return A list of covered areas
     */
    public List<Rect2i> getCoveredAreas() {
        List<Rect2i> areas = new ArrayList<>();
        areas.add(new Rect2i(leftPos, topPos, imageWidth, imageHeight));
        components.forEach((baseComponent -> {
            if (baseComponent instanceof MenuTabCollection tabCollection) {
                areas.addAll(tabCollection.getAreasCovered(leftPos, topPos));
            } else
                areas.add(baseComponent.getArea(leftPos, topPos));
        }));
        return areas;
    }
}
