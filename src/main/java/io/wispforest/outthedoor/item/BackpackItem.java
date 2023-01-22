package io.wispforest.outthedoor.item;

import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.block.BackpackBlockEntity;
import io.wispforest.outthedoor.misc.BackpackType;
import io.wispforest.outthedoor.object.OutTheDoorBlocks;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.nbt.NbtKey;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BackpackItem extends BlockItem {

    public static final NbtKey<NbtList> ITEMS_KEY = new NbtKey.ListKey<>("Items", NbtKey.Type.COMPOUND);
    private static final Map<BackpackType, BackpackItem> KNOWN_BACKPACK_ITEMS = new HashMap<>();

    public final BackpackType type;

    public BackpackItem(BackpackType type) {
        super(
                OutTheDoorBlocks.BACKPACK,
                new OwoItemSettings()
                        .group(OutTheDoor.GROUP)
                        .maxCount(1)
                        .equipmentSlot(stack -> EquipmentSlot.HEAD)
        );

        this.type = type;
        KNOWN_BACKPACK_ITEMS.put(this.type, this);
    }

    @Override
    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var playerStack = user.getStackInHand(hand);
        this.openScreen(playerStack, user);

        return TypedActionResult.success(playerStack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getPlayer().isSneaking()) {
            this.openScreen(context.getStack(), context.getPlayer());
            return ActionResult.SUCCESS;
        } else {
            return super.useOnBlock(context);
        }
    }

    protected void openScreen(ItemStack stack, PlayerEntity player) {
        if (player.world.isClient) return;

        player.openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return BackpackItem.this.getName();
            }

            @Nullable
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return switch (BackpackItem.this.type.slots()) {
                    case 27 -> GenericContainerScreenHandler.createGeneric9x3(syncId, inv, BackpackItem.this.createTrackedInventory(stack));
                    case 54 -> GenericContainerScreenHandler.createGeneric9x6(syncId, inv, BackpackItem.this.createTrackedInventory(stack));
                    default -> null;
                };
            }
        });
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        super.postPlacement(pos, world, player, stack, state);

        if (world.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
            backpack.backpack = stack.copy();
        }

        return true;
    }

    public SimpleInventory createTrackedInventory(ItemStack stack) {
        var inventory = new SimpleInventory(this.type.slots());
        Inventories.readNbt(stack.getOrCreateNbt(), inventory.stacks);

        inventory.addListener(sender -> storeInventory(stack, inventory));
        return inventory;
    }

    public void storeInventory(ItemStack stack, SimpleInventory inventory) {
        Inventories.writeNbt(stack.getOrCreateNbt(), inventory.stacks, true);
    }

    public static BackpackItem get(BackpackType type) {
        return KNOWN_BACKPACK_ITEMS.get(type);
    }

    public static Collection<BackpackItem> getAll() {
        return Collections.unmodifiableCollection(KNOWN_BACKPACK_ITEMS.values());
    }
}
