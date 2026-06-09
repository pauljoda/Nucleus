package com.pauljoda.nucleus.common.container.slots;

import com.pauljoda.nucleus.capabilities.item.NucleusItemResourceHandler;
import net.minecraft.world.entity.player.Player;

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
public class PhantomSlot extends ResourceSlot implements IPhantomSlot {

    /**
     * Creates a phantom slot
     *
     * @param itemHandler The ItemHandler to manage
     * @param index       The slot index
     * @param xPosition   The slot x pos
     * @param yPosition   The slot y pos
     */
    public PhantomSlot(NucleusItemResourceHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    /*******************************************************************************************************************
     * Slot                                                                                                            *
     *******************************************************************************************************************/

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }


    /*******************************************************************************************************************
     * IPhantomSlot                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Can slot change
     *
     * @return True to allow change
     */
    @Override
    public boolean canAdjust() {
        return true;
    }
}
