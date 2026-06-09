package com.pauljoda.nucleus.common.blocks.entity.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

class NucleusFluidResourceHandler extends SnapshotJournal<FluidStack[]> implements ResourceHandler<FluidResource> {
    private final Supplier<FluidTank[]> tanks;
    private final Supplier<int[]> inputTanks;
    private final Supplier<int[]> outputTanks;
    private final Consumer<FluidTank> changedCallback;

    NucleusFluidResourceHandler(Supplier<FluidTank[]> tanks, Supplier<int[]> inputTanks,
                                Supplier<int[]> outputTanks, Consumer<FluidTank> changedCallback) {
        this.tanks = tanks;
        this.inputTanks = inputTanks;
        this.outputTanks = outputTanks;
        this.changedCallback = changedCallback;
    }

    @Override
    public int size() {
        return tanks().length;
    }

    @Override
    public FluidResource getResource(int index) {
        FluidTank tank = tank(index);
        return FluidResource.of(tank.getFluid());
    }

    @Override
    public long getAmountAsLong(int index) {
        return tank(index).getFluidAmount();
    }

    @Override
    public long getCapacityAsLong(int index, FluidResource resource) {
        FluidTank tank = tank(index);
        return resource.isEmpty() || isValid(index, resource) ? tank.getCapacity() : 0;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        FluidTank tank = tank(index);
        return resource.isEmpty() || tank.isFluidValid(resource.toStack(1));
    }

    @Override
    public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0 || !contains(inputTanks.get(), index) || !isValid(index, resource))
            return 0;

        FluidTank tank = tank(index);
        int inserted = tank.fill(resource.toStack(amount), IFluidHandler.FluidAction.SIMULATE);
        if (inserted > 0) {
            updateSnapshots(transaction);
            tank.fill(resource.toStack(inserted), IFluidHandler.FluidAction.EXECUTE);
        }
        return inserted;
    }

    @Override
    public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty() || amount <= 0 || !contains(outputTanks.get(), index))
            return 0;

        FluidTank tank = tank(index);
        if (!resource.matches(tank.getFluid()))
            return 0;

        FluidStack extractedStack = tank.drain(resource.toStack(amount), IFluidHandler.FluidAction.SIMULATE);
        int extracted = extractedStack.getAmount();
        if (extracted > 0) {
            updateSnapshots(transaction);
            tank.drain(resource.toStack(extracted), IFluidHandler.FluidAction.EXECUTE);
        }
        return extracted;
    }

    @Override
    protected FluidStack[] createSnapshot() {
        FluidTank[] current = tanks();
        FluidStack[] snapshot = new FluidStack[current.length];
        for (int i = 0; i < current.length; i++) {
            snapshot[i] = current[i] == null ? FluidStack.EMPTY : current[i].getFluid().copy();
        }
        return snapshot;
    }

    @Override
    protected void revertToSnapshot(FluidStack[] snapshot) {
        FluidTank[] current = tanks();
        for (int i = 0; i < current.length && i < snapshot.length; i++) {
            if (current[i] != null)
                current[i].setFluid(snapshot[i].copy());
        }
    }

    @Override
    protected void onRootCommit(FluidStack[] originalState) {
        FluidTank[] current = tanks();
        for (int i = 0; i < current.length && i < originalState.length; i++) {
            FluidTank tank = current[i];
            if (tank != null && !FluidStack.matches(originalState[i], tank.getFluid()))
                changedCallback.accept(tank);
        }
    }

    private FluidTank[] tanks() {
        return Objects.requireNonNull(tanks.get(), "Fluid tanks have not been initialized");
    }

    private FluidTank tank(int index) {
        FluidTank[] current = tanks();
        Objects.checkIndex(index, current.length);
        return Objects.requireNonNull(current[index], "Fluid tank " + index + " has not been initialized");
    }

    private static boolean contains(int[] slots, int index) {
        for (int slot : slots) {
            if (slot == index)
                return true;
        }
        return false;
    }
}
