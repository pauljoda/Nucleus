package com.pauljoda.nucleus.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * The Savable interface represents an object that is capable of saving and loading data using Value I/O.
 */
public interface Savable {

    /**
     * Loads the data from the given ValueInput.
     *
     * @param input The input containing the data to be loaded.
     */
    void load(ValueInput input);

    /**
     * Saves the data of the object into the specified ValueOutput.
     *
     * @param output The output to store the data into.
     */
    void save(ValueOutput output);

    /**
     * @deprecated Use {@link #load(ValueInput)} with the caller's registry context.
     */
    @Deprecated(forRemoval = true)
    default void load(CompoundTag tag) {
        load(TagValueInput.create(ProblemReporter.DISCARDING, net.minecraft.core.RegistryAccess.EMPTY, tag));
    }

    /**
     * @deprecated Use {@link #save(ValueOutput)} with the caller's registry context.
     */
    @Deprecated(forRemoval = true)
    default CompoundTag save(CompoundTag tag) {
        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        save(output);
        tag.merge(output.buildResult());
        return tag;
    }
}
