package io.wispforest.outthedoor.block;

import io.wispforest.outthedoor.item.BackpackItem;
import io.wispforest.outthedoor.misc.BackpackType;
import io.wispforest.outthedoor.object.OutTheDoorBlocks;
import io.wispforest.owo.ops.WorldOps;
import io.wispforest.owo.serialization.endec.BuiltInEndecs;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BackpackBlockEntity extends BlockEntity implements RenderDataBlockEntity {

    public static final KeyedEndec<ItemStack> BACKPACK = BuiltInEndecs.ITEM_STACK.keyed("Backpack", ItemStack.EMPTY);

    private ItemStack backpack = ItemStack.EMPTY;
    private SimpleInventory inventory = new SimpleInventory(0);

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

    public SimpleInventory inventory() {
        return this.inventory;
    }

    public BackpackItem cast() {
        return ((BackpackItem) this.backpack.getItem());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.setBackpack(nbt.get(BACKPACK));
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
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(this.world, this.pos);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void setBackpack(ItemStack backpack) {
        this.backpack = backpack;
        this.markDirty();

        this.inventory = this.cast().createTrackedInventory(this.backpack);
        this.inventory.addListener(sender -> this.markDirty());
    }

    public ItemStack backpack() {
        return this.backpack;
    }

    @Override
    public @Nullable Object getRenderData() {
        return this.backpack.getItem() instanceof BackpackItem backpackItem
                ? backpackItem.type.model()
                : null;
    }

    static {
        ItemStorage.SIDED.registerForBlockEntity((backpack, direction) -> {
            return InventoryStorage.of(backpack.inventory(), direction);
        }, OutTheDoorBlocks.Entities.BACKPACK);
    }
}
