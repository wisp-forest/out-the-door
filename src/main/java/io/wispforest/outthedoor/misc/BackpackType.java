package io.wispforest.outthedoor.misc;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public interface BackpackType {
    String name();

    Identifier model();

    int rows();

    int rowWidth();

    default SoundEvent openSound() {
        return SoundEvents.ITEM_BUNDLE_INSERT;
    }

    default SoundEvent equipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
    }

    // TODO block sounds
    BlockSoundGroup blockSounds();

    default int slots() {
        return this.rows() * this.rowWidth();
    }
}
