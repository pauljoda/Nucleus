package com.pauljoda.nucleus.helper;

import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import org.joml.Vector3f;

public class ModelHelper {
    public static ItemTransforms DEFAULT_ITEM_STATE;

    public static ItemTransform createTransform(float rotX, float rotY, float rotZ,
                                                float transX, float transY, float transZ,
                                                float scaleX, float scaleY, float scaleZ) {
        return new ItemTransform(new Vector3f(rotX / 16F, rotY / 16F, rotZ / 16F),
                new Vector3f(transX, transY, transZ),
                new Vector3f(scaleX, scaleY, scaleZ));
    }

    public static ItemTransform createTransform(float translationX, float translationY, float translationZ,
                                                float rotationX, float rotationY, float rotationZ,
                                                float scale) {
        return new ItemTransform(
                new Vector3f(rotationX, rotationY, rotationZ),
                new Vector3f(translationX / 16F, translationY / 16F, translationZ / 16F),
                new Vector3f(scale, scale, scale));
    }


    static {
        DEFAULT_ITEM_STATE = new ItemTransforms(
                createTransform(0, 3, 1, 0, 0, 0, 0.55f),
                createTransform(0, 3, 1, 0, 0, 0, 0.55f),
                createTransform(1.13f, 3.2f, 1.13f, 0, 90, -25, 0.68f),
                createTransform(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f),
                createTransform(0, 13, 7, 0, 180, 0, 1),
                ItemTransform.NO_TRANSFORM,
                createTransform(0, 2, 0, 0, 0, 0, 0.5f),
                ItemTransform.NO_TRANSFORM,
                ItemTransform.NO_TRANSFORM
        );
    }
}
