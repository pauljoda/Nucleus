package com.pauljoda.nucleus.client.gui.widget.display;

import com.pauljoda.nucleus.client.gui.widget.listeners.IMouseEventListener;
import com.pauljoda.nucleus.client.gui.MenuBase;
import com.pauljoda.nucleus.client.gui.widget.BaseWidget;
import com.pauljoda.nucleus.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
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
 * @since 2/13/2017
 */
public class MenuTabCollection extends BaseWidget {
    // Variables
    protected List<MenuTab> tabs;
    protected MenuTab activeTab;

    /**
     * Main constructor for all components
     *
     * @param parentGui The parent Gui
     * @param x         The x position
     */
    public MenuTabCollection(MenuBase<?> parentGui, int x) {
        super(parentGui, x, 2);
        tabs = new ArrayList<>();
        setMouseEventListener(new TabMouseListener());
    }

    /**
     * Adds a tab to the collection
     *
     * @param components The components to add to the tab
     * @param maxWidth   The max width of the tab
     * @param maxHeight  The max height of the tab
     * @param textureU   The tabs u texture
     * @param textureV   The tabs v texture
     * @param stack      The display stack, can be null
     * @return This, to enable chaining
     */
    public MenuTabCollection addTab(List<BaseWidget> components, int maxWidth, int maxHeight,
                                    int textureU, int textureV, @Nullable ItemStack stack) {
        MenuTab tab = new MenuTab(parent, xPos - 5, yPos + (yPos + (tabs.size()) * 24),
                textureU, textureV, maxWidth, maxHeight, stack);
        components.forEach((tab::addChild));
        tabs.add(tab);
        return this;
    }

    /**
     * Adds a reverse tab to the collection
     *
     * @param components The components to add to the tab
     * @param maxWidth   The max width of the tab
     * @param maxHeight  The max height of the tab
     * @param textureU   The tabs u texture
     * @param textureV   The tabs v texture
     * @param stack      The display stack, can be null
     * @return This, to enable chaining
     */
    public MenuTabCollection addReverseTab(List<BaseWidget> components, int maxWidth, int maxHeight,
                                           int textureU, int textureV, @Nullable ItemStack stack) {
        MenuTab tab = new MenuReverseTab(parent, xPos + 5, yPos + (yPos + (tabs.size()) * 24),
                textureU, textureV, maxWidth, maxHeight, stack);
        components.forEach((tab::addChild));
        tabs.add(tab);
        return this;
    }

    /**
     * Move the tabs to fit the expansion of one
     */
    private void realignTabsVertically() {
        int y = yPos;
        for (MenuTab tab : tabs) {
            tab.setYPos(y);
            y += tab.getHeight();
        }
    }

    /**
     * Gets the areas covered by the tab collection
     *
     * @param guiLeft The gui left of the parent
     * @param guiTop  The gui top of the parent
     * @return A list of covered areas
     */
    public List<Rect2i> getAreasCovered(int guiLeft, int guiTop) {
        List<Rect2i> list = new ArrayList<>();
        tabs.forEach((menuTab -> {
            if (menuTab instanceof MenuReverseTab)
                list.add(new Rect2i(guiLeft + menuTab.getXPos() - getWidth(), guiTop + menuTab.getYPos(),
                        menuTab.getWidth(), menuTab.getHeight()));
            else
                list.add(new Rect2i(guiLeft + menuTab.getXPos(), guiTop + menuTab.getYPos(),
                        menuTab.getWidth(), menuTab.getHeight()));
        }));
        return list;
    }

    /*******************************************************************************************************************
     * BaseComponent                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Called when the mouse is scrolled
     *
     * @param dir 1 for positive, -1 for negative
     */
    @Override
    public void mouseScrolled(int dir) {
        tabs.forEach((menuTab -> menuTab.mouseScrolledTab(dir)));
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
        for (MenuTab tab : tabs) {
            if (tab.isMouseOver(mouseX, mouseY))
                return true;
        }
        return false;
    }

    /**
     * Used when a key is pressed
     *
     * @param letter  The letter
     * @param keyCode The code
     */
    @Override
    public void keyTyped(char letter, int keyCode) {
        tabs.forEach((menuTab -> menuTab.keyTyped(letter, keyCode)));
    }

    /**
     * Render the tooltip if you can
     *
     * @param mouseX Mouse X
     * @param mouseY Mouse Y
     */
    @Override
    public void renderToolTip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        tabs.forEach((menuTab -> {
            if (menuTab.isMouseOver(mouseX - parent.screenLeft(), mouseY - parent.screenTop()))
                menuTab.renderToolTip(graphics, mouseX, mouseY);
        }));
    }

    /**
     * Called to render the component
     */
    @Override
    public void render(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        realignTabsVertically();
        for (MenuTab tab : tabs) {
            matrixStack.pushMatrix();
            matrixStack.translate(tab.getXPos(), tab.getYPos());
            tab.render(graphics, guiLeft, guiTop, mouseX - tab.getXPos(), mouseY - tab.getYPos());
            RenderUtils.restoreColor();
            matrixStack.popMatrix();
        }
    }

    /**
     * Called after base render, is already translated to guiLeft and guiTop, just move offset
     */
    @Override
    public void renderOverlay(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        for (MenuTab tab : tabs) {
            matrixStack.pushMatrix();
            RenderUtils.prepareRenderState();
            matrixStack.translate(tab.getXPos(), tab.getYPos());
            tab.renderOverlay(graphics, 0, 0, mouseX, mouseY);
            RenderUtils.restoreRenderState();
            RenderUtils.restoreColor();
            matrixStack.popMatrix();
        }
    }

    /**
     * Used to find how wide this is
     *
     * @return How wide the component is
     */
    @Override
    public int getWidth() {
        return 24;
    }

    /**
     * Used to find how tall this is
     *
     * @return How tall the component is
     */
    @Override
    public int getHeight() {
        return 5 + (tabs.size() * 24);
    }

    /*******************************************************************************************************************
     * Accessors/Mutators                                                                                              *
     *******************************************************************************************************************/

    public List<MenuTab> getTabs() {
        return tabs;
    }

    public void setTabs(List<MenuTab> tabs) {
        this.tabs = tabs;
    }

    /*******************************************************************************************************************
     * Classes                                                                                                         *
     *******************************************************************************************************************/

    /**
     * Private class to hold all mouse event logic
     */
    private class TabMouseListener implements IMouseEventListener {

        /**
         * Called when the mouse clicks on the component
         *
         * @param component The component to be clicked
         * @param mouseX    X position of the mouse
         * @param mouseY    Y position of the mouse
         * @param button    Which button was clicked
         */
        @Override
        public void onMouseDown(BaseWidget component, double mouseX, double mouseY, int button) {
            for (int i = 0; i < tabs.size(); i++) {
                MenuTab tab = tabs.get(i);
                if (tab.isMouseOver(mouseX, mouseY)) {
                    if (tab.getMouseEventListener() == null) {
                        if (!tab.mouseDownActivated(
                                (tab instanceof MenuReverseTab) ? mouseX + tab.expandedWidth - 5 : mouseX - parent.screenWidth() + 5,
                                mouseY - (i * 24) - 2, button)) {
                            if (activeTab != null &&
                                    activeTab != tab) {
                                if (activeTab != null)
                                    activeTab.setActive(false);
                                activeTab = tab;
                                activeTab.setActive(true);
                                return;
                            } else if (activeTab == tab && tab.areChildrenActive()) {
                                activeTab.setActive(false);
                                activeTab = null;
                                return;
                            } else {
                                activeTab = tab;
                                activeTab.setActive(true);
                                return;
                            }
                        }
                    } else
                        tab.mouseDown(mouseX, mouseY, button);
                    return;
                }
            }
        }

        /**
         * Called when the mouse releases the component
         *
         * @param component The component to be clicked
         * @param mouseX    X position of the mouse
         * @param mouseY    Y position of the mouse
         * @param button    Which button was clicked
         */
        @Override
        public void onMouseUp(BaseWidget component, double mouseX, double mouseY, int button) {
            for (int i = 0; i < tabs.size(); i++) {
                MenuTab tab = tabs.get(i);
                if (tab.isMouseOver(mouseX, mouseY)) {
                    tab.mouseUpActivated((tab instanceof MenuReverseTab) ? mouseX + tab.expandedWidth - 5 : mouseX - parent.screenWidth() + 5,
                            mouseY - (i * 24) - 2, button);
                    return;
                }
            }
        }

        /**
         * Called when the mouse drags an item
         *
         * @param component The component to be clicked
         * @param mouseX    X position of the mouse
         * @param mouseY    Y position of the mouse
         * @param button    Which button was clicked
         */
        @Override
        public void onMouseDrag(BaseWidget component, double mouseX, double mouseY, int button, double xAmount, double yAmount) {
            for (int i = 0; i < tabs.size(); i++) {
                MenuTab tab = tabs.get(i);
                if (tab.isMouseOver(mouseX, mouseY)) {
                    tab.mouseDragActivated((tab instanceof MenuReverseTab) ? mouseX + tab.expandedWidth - 5 : mouseX - parent.screenWidth() + 5,
                            mouseY - (i * 24) - 2, button, xAmount, yAmount);
                    return;
                }
            }
        }
    }
}
