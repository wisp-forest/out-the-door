package io.wispforest.outthedoor.block;

import io.wispforest.outthedoor.item.BackpackItem;
import io.wispforest.outthedoor.misc.BackpackType;
import io.wispforest.outthedoor.object.OutTheDoorBlocks;
import io.wispforest.owo.nbt.NbtKey;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BackpackBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {

    public static final NbtKey<ItemStack> BACKPACK = new NbtKey<>("Backpack", NbtKey.Type.ITEM_STACK);

    public ItemStack backpack = ItemStack.EMPTY;

    public BackpackBlockEntity(BlockPos pos, BlockState state) {
        super(OutTheDoorBlocks.Entities.BACKPACK, pos, state);
    }

    public boolean hasBackpack() {
        return this.backpack.getItem() instanceof BackpackItem;
    }

    public BackpackType type() {
        return this.hasBackpack()
                ? ((BackpackItem) this.backpack.getItem()).type
                : null;
    }

    public SimpleInventory createTrackedInventory() {
        var backpackItem = (BackpackItem) this.backpack.getItem();

        var inventory = backpackItem.createTrackedInventory(this.backpack);
        inventory.addListener(sender -> this.markDirty());

        return inventory;
    }

    public BackpackItem cast() {
        return ((BackpackItem) this.backpack.getItem());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.backpack = nbt.get(BACKPACK);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put(BACKPACK, this.backpack);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        var nbt = super.toInitialChunkDataNbt();
        this.writeNbt(nbt);
        return nbt;
    }

    @Override
    public @Nullable Object getRenderAttachmentData() {
        return this.backpack.getItem() instanceof BackpackItem backpackItem
                ? backpackItem.type.model()
                : null;
    }
}
