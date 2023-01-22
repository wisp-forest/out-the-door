package io.wispforest.outthedoor.misc;

import net.minecraft.util.Identifier;

public interface BackpackType {
    String name();

    Identifier model();

    int slots();
}
