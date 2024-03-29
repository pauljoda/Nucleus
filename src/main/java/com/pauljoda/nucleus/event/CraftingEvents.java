package com.pauljoda.nucleus.event;

import com.pauljoda.nucleus.common.ICraftingListener;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class CraftingEvents {
    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        if (event.getCrafting().getItem() instanceof ICraftingListener) {
            ItemStack[] craftingList = new ItemStack[event.getInventory().getContainerSize()];
            for (int x = 0; x < craftingList.length; x++)
                craftingList[x] = event.getInventory().getItem(x);
            ((ICraftingListener) event.getCrafting().getItem()).onCrafted(craftingList, event.getCrafting());
        }
    }
}
