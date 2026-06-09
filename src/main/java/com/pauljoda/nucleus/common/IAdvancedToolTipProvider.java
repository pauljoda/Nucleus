package com.pauljoda.nucleus.common;

import com.pauljoda.nucleus.util.ClientUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public interface IAdvancedToolTipProvider extends IToolTipProvider {

    /**
     * Get the tool tip to present when shift is pressed
     *
     * @param stack The itemstack
     * @return The list to display
     */
    @Nullable
    List<String> getAdvancedToolTip(@Nonnull ItemStack stack);

    /**
     * Defines if the tooltip will add the press shift for more info text
     * <p>
     * Override this to false if you just want it to show up on shift, useful if press shift for info may already be
     * present
     *
     * @return True to display
     */
    default boolean displayShiftForInfo(ItemStack stack) {
        return true;
    }

    /**
     * Used to get the tip to display
     *
     * @param stack
     * @return The tip to display
     */
    @Nullable
    @Override
    default List<String> getToolTip(@Nonnull ItemStack stack) {
        return ClientUtils.isShiftPressed() ?
                getAdvancedToolTip(stack) :
                displayShiftForInfo(stack) ?
                        Collections.singletonList(ChatFormatting.GREEN + ClientUtils.translate("nucleus.text.shift_info")) :
                        null;
    }
}
