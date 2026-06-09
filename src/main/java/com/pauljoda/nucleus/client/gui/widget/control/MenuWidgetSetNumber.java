package com.pauljoda.nucleus.client.gui.widget.control;

import com.pauljoda.nucleus.helper.GuiHelper;
import com.pauljoda.nucleus.client.gui.MenuBase;
import com.pauljoda.nucleus.client.gui.widget.BaseWidget;
import com.pauljoda.nucleus.util.RenderUtils;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;

import static org.apache.commons.lang3.StringUtils.isNumeric;

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
public abstract class MenuWidgetSetNumber extends BaseWidget {
    // Variables
    protected int width, u, v, value, floor, ceiling;
    protected int height = 16;
    protected EditBox textField;
    protected boolean upSelected, downSelected = false;

    /**
     * Creates the set number object
     * <p>
     * IMPORTANT: You must create the up and down arrow and pass the u and v or the top left corner
     * It should look like the following in the texture sheet:
     * UN|US
     * DN|DS
     * <p>
     * With UN and DN being the normal up and down and US and DS being the selected versions (when clicked)
     * The arrow buttons should be 11x8 pixels and all touching to form one big rectangle
     *
     * @param parent       The parent GUI
     * @param x            The x pos
     * @param y            The y pos
     * @param texU         The texture u location
     * @param texV         The texture v location
     * @param value        The starting value
     * @param lowestValue  The lowest value
     * @param highestValue The highest value
     */
    public MenuWidgetSetNumber(MenuBase<?> parent, int x, int y, int texU, int texV, int width, int value, int lowestValue, int highestValue) {
        super(parent, x, y);
        this.u = texU;
        this.v = texV;
        this.value = value;
        this.floor = lowestValue;
        this.ceiling = highestValue;
        this.width = width;

        textField = new EditBox(fontRenderer, 0, 0, width - 12, height, Component.translatable("Test"));
        textField.setValue(String.valueOf(value));
    }

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * Called when the user sets the value or when the value is changed
     *
     * @param value The value set by the user
     */
    protected abstract void setValue(int value);

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
        if (GuiHelper.isInBounds(x, y, xPos + width - 8, yPos - 1, xPos + width + 2, yPos + 7)) {
            upSelected = true;

            if (value < ceiling)
                value += 1;

            GuiHelper.playButtonSound();
            setValue(value);
            textField.setValue(String.valueOf(value));
        } else if (GuiHelper.isInBounds(x, y, xPos + width - 8, yPos + 9, xPos + width + 2, yPos + 17)) {
            downSelected = true;

            if (value > floor)
                value -= 1;

            GuiHelper.playButtonSound();
            setValue(value);
            textField.setValue(String.valueOf(value));
        }
        textField.mouseClicked(new MouseButtonEvent(x, y, new MouseButtonInfo(button, 0)), false);
        super.mouseDown(x, y, button);
    }

    /**
     * Called when the mouse button is over the component and released
     *
     * @param x      Mouse X Position
     * @param y      Mouse Y Position
     * @param button Mouse Button
     */
    @Override
    public void mouseUp(double x, double y, int button) {
        upSelected = downSelected = false;
    }

    /**
     * Used when a key is pressed
     *
     * @param letter  The letter
     * @param keyCode The code
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public void keyTyped(char letter, int keyCode) {
        if (Character.isLetter(letter) && (keyCode != 8 && keyCode != 109)) return;
        if (textField.isFocused())
            textField.charTyped(new CharacterEvent(letter));
        if (textField.getValue() == null || (textField.getValue().equals("")) || !isNumeric(textField.getValue())) {
            textField.setTextColor(RenderUtils.opaque(0xE62E00));
            return;
        }
        if (keyCode == 13)
            textField.setFocused(false);
        textField.setTextColor(RenderUtils.opaque(0xFFFFFF));
        if (Integer.parseInt(textField.getValue()) > ceiling)
            textField.setValue(String.valueOf(ceiling));
        else if (Integer.parseInt(textField.getValue()) < floor)
            textField.setValue(String.valueOf(floor));
        value = Integer.parseInt(textField.getValue());
        setValue(value);
    }

    /**
     * Called to render the component
     */
    @Override
    public void render(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(xPos, yPos);
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, parent.textureLocation, width - 10, -1,
                upSelected ? u + 12 : u + 1, v, 11, 8, 256, 256);
        RenderUtils.blit(graphics, RenderPipelines.GUI_TEXTURED, parent.textureLocation, width - 10, 9,
                downSelected ? u + 12 : u + 1, v + 7, 11, 8, 256, 256);
        textField.extractRenderState(graphics, mouseX, mouseY, 0.0F);
        matrixStack.popMatrix();
    }

    /**
     * Called after base render, is already translated to guiLeft and guiTop, just move offset
     */
    @Override
    public void renderOverlay(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        // Number input has no overlay layer.
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

    public int getValue() {
        return value;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getCeiling() {
        return ceiling;
    }

    public void setCeiling(int ceiling) {
        this.ceiling = ceiling;
    }

    public EditBox getTextField() {
        return textField;
    }

    public void setTextField(EditBox textField) {
        this.textField = textField;
    }
}
