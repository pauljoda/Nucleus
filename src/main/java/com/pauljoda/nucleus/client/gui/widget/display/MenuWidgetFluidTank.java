package com.pauljoda.nucleus.client.gui.widget.display;

import com.pauljoda.nucleus.helper.GuiHelper;
import com.pauljoda.nucleus.client.gui.MenuBase;
import com.pauljoda.nucleus.client.gui.widget.BaseWidget;
import com.pauljoda.nucleus.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

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
public class MenuWidgetFluidTank extends BaseWidget {
    // Variables
    protected int width, height;
    protected Supplier<FluidStack> fluidSupplier;
    protected IntSupplier capacitySupplier;

    /**
     * Creates a fluid tank renderer
     *
     * @param parent           The parent GUI
     * @param x                The x pos
     * @param y                The y pos
     * @param w                The width
     * @param h                The height
     * @param fluidSupplier    Supplies the fluid to render
     * @param capacitySupplier Supplies the capacity used to scale the fluid height
     */
    public MenuWidgetFluidTank(MenuBase<?> parent, int x, int y, int w, int h,
                               Supplier<FluidStack> fluidSupplier, IntSupplier capacitySupplier) {
        super(parent, x, y);
        this.width = w;
        this.height = h;
        this.fluidSupplier = fluidSupplier;
        this.capacitySupplier = capacitySupplier;
    }

    /*******************************************************************************************************************
     * BaseComponent                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Called to render the component
     */
    @Override
    public void render(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        // Fluid is rendered in the overlay pass so it appears above the base GUI texture.
    }

    /**
     * Called after base render, is already translated to guiLeft and guiTop, just move offset
     */
    @Override
    public void renderOverlay(GuiGraphicsExtractor graphics, int guiLeft, int guiTop, int mouseX, int mouseY) {
        var matrixStack = graphics.pose();
        matrixStack.pushMatrix();
        matrixStack.translate(xPos, yPos);
        GuiHelper.renderFluid(graphics, fluidSupplier.get(), capacitySupplier.getAsInt(), 0, height, height, width);
        RenderUtils.bindTexture(parent.textureLocation);
        matrixStack.popMatrix();
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

}
