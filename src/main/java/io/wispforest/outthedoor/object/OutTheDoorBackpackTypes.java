package io.wispforest.outthedoor.object;

import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.misc.BackpackType;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class OutTheDoorBackpackTypes implements AutoRegistryContainer<BackpackType> {

    public static final BlockSoundGroup LEATHER_SOUND_GROUP = new BlockSoundGroup(
            1f, 1f,
            SoundEvents.BLOCK_WOOL_BREAK,
            SoundEvents.BLOCK_WOOL_STEP,
            SoundEvents.ITEM_BUNDLE_DROP_CONTENTS,
            SoundEvents.BLOCK_WOOL_HIT,
            SoundEvents.BLOCK_WOOL_STEP
    );

    public static final BackpackType PUMPKIN = new Type("pumpkin", OutTheDoor.id("item/pumpkin_backpack"), 4, 5, BlockSoundGroup.WOOD);
    public static final BackpackType LEATHER = new Type("leather", OutTheDoor.id("item/leather_backpack"), 4, 5, LEATHER_SOUND_GROUP);
    public static final BackpackType HIDE = new Type("hide", OutTheDoor.id("item/hide_backpack"), 5, 7, LEATHER_SOUND_GROUP);

    @Override
    public Registry<BackpackType> getRegistry() {
        return OutTheDoor.BACKPACK_REGISTRY;
    }

    @Override
    public Class<BackpackType> getTargetFieldType() {
        return BackpackType.class;
    }

    public record Type(String name, Identifier model, int rows, int rowWidth, BlockSoundGroup blockSounds) implements BackpackType {}
}
