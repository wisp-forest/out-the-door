package io.wispforest.outthedoor.object;

import io.wispforest.outthedoor.block.BackpackBlock;
import io.wispforest.outthedoor.block.BackpackBlockEntity;
import io.wispforest.owo.registration.reflect.BlockEntityRegistryContainer;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

public class OutTheDoorBlocks implements BlockRegistryContainer {

    @NoBlockItem
    public static final Block BACKPACK = new BackpackBlock();

    public static class Entities implements BlockEntityRegistryContainer {

        public static final BlockEntityType<BackpackBlockEntity> BACKPACK
                = FabricBlockEntityTypeBuilder.create(BackpackBlockEntity::new, OutTheDoorBlocks.BACKPACK).build();

    }
}
