package com.pauljoda.nucleus.common;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IToolTipProvider {

    /**
     * Used to get the tip to display
     *
     * @return The tip to display
     */
    @Nullable
    List<String> getToolTip(@Nonnull ItemStack stack);
}
