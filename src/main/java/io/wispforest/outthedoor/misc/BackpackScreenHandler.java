package io.wispforest.outthedoor.misc;

import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.client.screen.BackpackScreen;
import io.wispforest.outthedoor.item.BackpackItem;
import io.wispforest.owo.client.screens.ScreenUtils;
import io.wispforest.owo.client.screens.SlotGenerator;
import io.wispforest.owo.client.screens.ValidatingSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.Predicate;

public class BackpackScreenHandler extends ScreenHandler {

    public final BackpackType type;
    public final boolean restoreParent;

    protected final SimpleInventory backpackInventory;
    protected final Predicate<PlayerEntity> canUse;

    public static BackpackScreenHandler client(int syncId, PlayerInventory playerInventory, BackpackItem.ScreenData data) {
        return new BackpackScreenHandler(syncId, playerInventory, new SimpleInventory(data.type().slots()), data.type(), player -> true, data.restoreParent());
    }

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory backpackInventory, BackpackType type, Predicate<PlayerEntity> canUse) {
        this(syncId, playerInventory, backpackInventory, type, canUse, false);
    }

    protected BackpackScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory backpackInventory, BackpackType type, Predicate<PlayerEntity> canUse, boolean restoreParent) {
        super(OutTheDoor.BACKPACK_SCREEN_HANDLER, syncId);
        this.backpackInventory = backpackInventory;
        this.type = type;
        this.restoreParent = restoreParent;
        this.canUse = canUse;

        if (playerInventory.player instanceof ServerPlayerEntity player) {
            player.playSound(type.openSound(), 1f, 1f);
            player.playSound(type.openSound(), 1f, 1f);
        }

        SlotGenerator.begin(this::addSlot, BackpackScreen.SIDE_PADDING + Math.max(0, (9 - type.rowWidth()) * 18 / 2) + 1, BackpackScreen.TOP_PADDING + 1)
                .slotFactory((inventory, index, x, y) -> new NoBackpackSlot(inventory, index, x, y, stack -> stack.getItem().canBeNested()))
                .grid(backpackInventory, 0, type.rowWidth(), type.rows())
                .moveTo(BackpackScreen.SIDE_PADDING + Math.max(0, (type.rowWidth() - 9) * 18 / 2) + 1, BackpackScreen.TOP_PADDING + type.rows() * 18 + BackpackScreen.SEPARATOR_HEIGHT + 1)
                .slotFactory(NoBackpackSlot::new)
                .playerInventory(playerInventory);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ScreenUtils.handleSlotTransfer(this, slot, this.backpackInventory.size());
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.canUse.test(player);
    }

    protected static class NoBackpackSlot extends ValidatingSlot {

        public NoBackpackSlot(Inventory inventory, int index, int x, int y) {
            this(inventory, index, x, y, stack -> true);
        }

        public NoBackpackSlot(Inventory inventory, int index, int x, int y, Predicate<ItemStack> insertCondition) {
            super(inventory, index, x, y, insertCondition);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return !(this.getStack().getItem() instanceof BackpackItem) && stack.getItem().canBeNested() && super.canInsert(stack);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return !(this.getStack().getItem() instanceof BackpackItem) && super.canTakeItems(playerEntity);
        }

        @Override
        public boolean canTakePartial(PlayerEntity player) {
            return !(this.getStack().getItem() instanceof BackpackItem) && super.canTakePartial(player);
        }
    }
}
