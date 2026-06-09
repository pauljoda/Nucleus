package com.pauljoda.nucleus.common;

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

}
