package com.pauljoda.nucleus.testharness.common;

import com.pauljoda.nucleus.testharness.NucleusTestHarness;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Development-only block that opens a Nucleus GUI test screen.
 */
public class NucleusGuiTestBlock extends Block {
    public NucleusGuiTestBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (containerId, inventory, menuPlayer) -> new NucleusGuiTestMenu(containerId, inventory),
                    Component.translatable("screen." + NucleusTestHarness.MODID + ".gui_test")
            ));
        }
        return InteractionResult.SUCCESS.withoutItem();
    }
}
