package com.pauljoda.nucleus.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * <a href="http://creativecommons.org/licenses/by-nc-sa/4.0/">License</a>
 * <p>
 * Helper class to help manage energy, based off TeslaUtils but with Forge Energy
 *
 * @author Paul Davis - pauljoda
 * @since 3/1/2017
 */
public class EnergyUtils {

    /**
     * Converts the given number into a readable energy number. Also adds the suffix
     *
     * @param energy The number
     * @return A readable number
     */
    public static String getEnergyDisplay(int energy) {
        // If shift is press, give normal amount
        if (ClientUtils.isShiftPressed())
            return ClientUtils.formatNumber(energy) + " E";

        // No formatting, just add E
        if (energy < 1000)
            return energy + " E";

        // Exponent of 1000
        final int exp = (int) (Math.log(energy) / Math.log(1000));
        // Converts into the right prefix
        final char unitType = "KMGTPE".charAt(exp - 1);
        // Returns string with energy trimmed below the 1000, and adding the energy unit
        DecimalFormat format = new DecimalFormat("#.#");
        format.setRoundingMode(RoundingMode.FLOOR);
        return format.format(energy / Math.pow(1000, exp)) + " " + unitType + "E";
    }

    /**
     * Transfers power from one storage to another, either can be null if you are not sure if it is capable
     *
     * @param source      The source energy storage
     * @param destination The destination energy storage
     * @param maxAmount   Max amount to transfer
     * @param simulate    True to only simulate, not actually transfer
     * @return The amount moved or would be moved
     */
    public static int transferPower(@Nullable IEnergyStorage source, @Nullable IEnergyStorage destination,
                                    int maxAmount, boolean simulate) {
        if (source == null || destination == null)
            return 0;

        int amount = source
                .extractEnergy(destination.receiveEnergy(maxAmount, true), true);
        // Try move power
        return destination
                .receiveEnergy(source
                .extractEnergy(amount, simulate), simulate);
    }

    public static int transferPower(@Nullable EnergyHandler source, @Nullable EnergyHandler destination,
                                    int maxAmount, boolean simulate) {
        if (source == null || destination == null)
            return 0;

        try (Transaction transaction = Transaction.openRoot()) {
            int moved = EnergyHandlerUtil.move(source, destination, maxAmount, transaction);
            if (!simulate)
                transaction.commit();
            return moved;
        }
    }

    /**
     * Sends power to all faces connected
     *
     * @param source        The energy source
     * @param level         The world
     * @param pos           The position
     * @param amountPerFace How much per face
     * @param simulated     True to just simulate
     * @return How much energy consumed
     */
    public static int distributePowerToFaces(IEnergyStorage source, Level level, BlockPos pos,
                                             int amountPerFace, boolean simulated) {
        int consumedPower = 0;

        for (Direction dir : Direction.values()) {
            EnergyHandler target = level.getCapability(Capabilities.Energy.BLOCK, pos.relative(dir), dir.getOpposite());
            if (target != null) {
                int available = source.extractEnergy(amountPerFace, true);
                try (Transaction transaction = Transaction.openRoot()) {
                    int inserted = target.insert(available, transaction);
                    int extracted = source.extractEnergy(inserted, true);
                    if (!simulated) {
                        source.extractEnergy(extracted, false);
                        transaction.commit();
                    }
                    consumedPower += extracted;
                }
            }
        }

        return consumedPower;
    }

    /**
     * Sends power to all faces connected
     *
     * @param source        The energy source
     * @param level         The world
     * @param pos           The position
     * @param amountPerFace How much per face
     * @param simulated     True to just simulate
     * @return How much energy consumed
     */
    public static int consumePowerFromFaces(IEnergyStorage source, Level level, BlockPos pos,
                                            int amountPerFace, boolean simulated) {
        int receivedPower = 0;

        for (Direction dir : Direction.values()) {
            EnergyHandler sourceHandler = level.getCapability(Capabilities.Energy.BLOCK, pos.relative(dir), dir.getOpposite());
            if (sourceHandler != null) {
                int receivable = source.receiveEnergy(amountPerFace, true);
                try (Transaction transaction = Transaction.openRoot()) {
                    int extracted = sourceHandler.extract(receivable, transaction);
                    int inserted = source.receiveEnergy(extracted, true);
                    if (!simulated) {
                        source.receiveEnergy(inserted, false);
                        transaction.commit();
                    }
                    receivedPower += inserted;
                }
            }
        }

        return receivedPower;
    }

    /**
     * Adds the info needed to display held energy
     *
     * @param stack   The stack
     * @param toolTip The tip list
     */
    public static void addToolTipInfo(ItemStack stack, List<Component> toolTip) {
        EnergyHandler energyHandler = ItemAccess.forStack(stack).getCapability(Capabilities.Energy.ITEM);
        if (energyHandler != null) {
            addToolTipInfo(energyHandler, toolTip,
                    energyHandler.getCapacityAsInt() - energyHandler.getAmountAsInt(),
                    energyHandler.getAmountAsInt());
        }
    }

    /**
     * Adds the energy storage info
     *
     * @param energyStorage The energy storage object
     * @param toolTip       The list to add to
     * @param insert        The max insert, -1 to skip
     * @param extract       The max extract, -1 to skip
     */
    public static void addToolTipInfo(IEnergyStorage energyStorage, List<Component> toolTip, int insert, int extract) {
        toolTip.add(Component.translatable(ChatFormatting.GOLD + ClientUtils.translate("nucleus.energy.energyStored")));
        toolTip.add(Component.translatable("  " + EnergyUtils.getEnergyDisplay(energyStorage.getEnergyStored()) + " / " +
                EnergyUtils.getEnergyDisplay(energyStorage.getMaxEnergyStored())));
        if (!ClientUtils.isShiftPressed()) {
            toolTip.add(Component.translatable(""));
            toolTip.add(Component.translatable(ChatFormatting.GRAY + "" + ChatFormatting.ITALIC + ClientUtils.translate("nucleus.text.shift_info")));
        } else {
            if (insert > -1) {
                toolTip.add(Component.translatable(""));
                toolTip.add(Component.translatable(ChatFormatting.GREEN + ClientUtils.translate("nucleus.energy.energyIn")));
                toolTip.add(Component.translatable("  " + EnergyUtils.getEnergyDisplay(insert)));
            }
            if (extract > -1) {
                toolTip.add(Component.translatable(ChatFormatting.DARK_RED + ClientUtils.translate("nucleus.energy.energyOut")));
                toolTip.add(Component.translatable("  " + EnergyUtils.getEnergyDisplay(extract)));
            }
        }
    }

    public static void addToolTipInfo(EnergyHandler energyHandler, List<Component> toolTip, int insert, int extract) {
        toolTip.add(Component.translatable(ChatFormatting.GOLD + ClientUtils.translate("nucleus.energy.energyStored")));
        toolTip.add(Component.translatable("  " + EnergyUtils.getEnergyDisplay(energyHandler.getAmountAsInt()) + " / " +
                EnergyUtils.getEnergyDisplay(energyHandler.getCapacityAsInt())));
        if (!ClientUtils.isShiftPressed()) {
            toolTip.add(Component.translatable(""));
            toolTip.add(Component.translatable(ChatFormatting.GRAY + "" + ChatFormatting.ITALIC + ClientUtils.translate("nucleus.text.shift_info")));
        } else {
            if (insert > -1) {
                toolTip.add(Component.translatable(""));
                toolTip.add(Component.translatable(ChatFormatting.GREEN + ClientUtils.translate("nucleus.energy.energyIn")));
                toolTip.add(Component.translatable("  " + EnergyUtils.getEnergyDisplay(insert)));
            }
            if (extract > -1) {
                toolTip.add(Component.translatable(ChatFormatting.DARK_RED + ClientUtils.translate("nucleus.energy.energyOut")));
                toolTip.add(Component.translatable("  " + EnergyUtils.getEnergyDisplay(extract)));
            }
        }
    }

}
