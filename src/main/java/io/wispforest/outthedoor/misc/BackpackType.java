package io.wispforest.outthedoor.misc;

import net.minecraft.util.Identifier;

public interface BackpackType {
    String name();

    Identifier model();

    int rows();

    int rowWidth();

    default int slots() {
        return rows() * rowWidth();
    }
}
