package com.pauljoda.nucleus.compat.jei;

import com.pauljoda.nucleus.Nucleus;
import com.pauljoda.nucleus.client.gui.MenuBase;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class NucleusJEIPlugin implements IModPlugin {

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        IModPlugin.super.registerGuiHandlers(registration);
        registration.addGenericGuiContainerHandler(MenuBase.class, new IGuiContainerHandler<MenuBase<?>>() {
            /**
             * Give JEI information about extra space that the {@link AbstractContainerScreen} takes up.
             * Used for moving JEI out of the way of extra things like gui tabs.
             *
             * @param containerScreen
             * @return the space that the gui takes up besides the normal rectangle defined by {@link AbstractContainerScreen}.
             */
            @Override
            public @NotNull List<Rect2i> getGuiExtraAreas(@NotNull MenuBase<?> containerScreen) {
                return containerScreen.getCoveredAreas();
            }
        });
    }

    /**
     * Returns the unique identifier of the plugin.
     *
     * @return The unique identifier of the plugin.
     */
    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Nucleus.MODID, "jei");
    }
}
