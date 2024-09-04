package io.wispforest.outthedoor.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.block.BackpackBlockEntity;
import io.wispforest.outthedoor.misc.BackpackScreenHandler;
import io.wispforest.outthedoor.misc.BackpackTooltipData;
import io.wispforest.outthedoor.misc.BackpackType;
import io.wispforest.outthedoor.misc.OpenBackpackPacket;
import io.wispforest.outthedoor.object.OutTheDoorBlocks;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BackpackItem extends BlockItem implements Trinket, Equipment {

    private static final Map<BackpackType, BackpackItem> KNOWN_BACKPACK_ITEMS = new HashMap<>();

    public final BackpackType type;

    public BackpackItem(BackpackType type) {
        super(
            OutTheDoorBlocks.BACKPACK,
            new Settings()
                .group(OutTheDoor.GROUP)
                .maxCount(1)
//                        .equipmentSlot(stack -> EquipmentSlot.HEAD)
        );

        TrinketsApi.registerTrinket(this, this);

        this.type = type;
        KNOWN_BACKPACK_ITEMS.put(this.type, this);
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

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT) return false;

        if (!player.getWorld().isClient) {
            this.openScreen(stack, player, true);
        }

        return true;
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        super.postPlacement(pos, world, player, stack, state);

        if (world.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
            backpack.setBackpack(stack.copy());
        }

        return true;
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    @Nullable
    @Override
    public RegistryEntry<SoundEvent> getEquipSound() {
        return this.type.equipSound();
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state) {
        return this.type.blockSounds().getPlaceSound();
    }

    @Override
    public boolean canBeNested() {
        return false;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity.getWorld().isClient) return;

        entity.playSound(this.type.equipSound().value(), 1f, 1f);

        if (entity instanceof PlayerEntity player) {
            player.playSound(this.type.equipSound().value(), 1f, 1f);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        if (!OutTheDoor.CONFIG.alwaysDisplayContents() && (!Screen.hasShiftDown() && stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).streamNonEmpty().count() > 0)) {
            tooltip.add(Text.translatable("item.out-the-door.backpack.tooltip.view_contents"));
        }

        tooltip.add(Text.translatable("item.out-the-door.backpack.tooltip.slot_count", this.type.slots()));
        tooltip.add(Text.empty());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if (!OutTheDoor.CONFIG.alwaysDisplayContents() && !Screen.hasShiftDown()) return super.getTooltipData(stack);

        var containerData = stack.get(DataComponentTypes.CONTAINER);
        if (containerData == null) return Optional.empty();

        return Optional.of(new BackpackTooltipData(
            Util.make(DefaultedList.of(), displayStacks -> containerData.stream().filter($ -> !$.isEmpty()).forEach(displayStacks::add))
        ));
    }

    @Override
    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }

    protected void openScreen(ItemStack stack, PlayerEntity player) {
        this.openScreen(stack, player, false);
    }

    protected void openScreen(ItemStack stack, PlayerEntity player, boolean restoreParent) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        serverPlayer.onHandledScreenClosed();
        serverPlayer.openHandledScreen(new ExtendedScreenHandlerFactory<ScreenData>() {
            @Override
            public ScreenData getScreenOpeningData(ServerPlayerEntity player) {
                return new ScreenData(BackpackItem.this.type, restoreParent);
            }

            @Override
            public Text getDisplayName() {
                return BackpackItem.this.getName();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new BackpackScreenHandler(syncId, inv, BackpackItem.this.createTrackedInventory(stack), BackpackItem.this.type, user -> {
                    if (user.getInventory().contains(stack)) return true;

                    return TrinketsApi.getTrinketComponent(user).map(trinkets -> {
                        return !trinkets.getEquipped(trinketStack -> trinketStack.equals(stack)).isEmpty();
                    }).orElse(false);
                });
            }
        });

        serverPlayer.playerScreenHandler.enableSyncing();
    }

    public SimpleInventory createTrackedInventory(ItemStack stack) {
        var inventory = new SimpleInventory(this.type.slots());

        var container = stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
        container.copyTo(inventory.heldStacks);

        inventory.addListener(sender -> storeInventory(stack, inventory));
        return inventory;
    }

    public void storeInventory(ItemStack stack, SimpleInventory inventory) {
        stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(inventory.heldStacks));
    }

    public static BackpackItem get(BackpackType type) {
        return KNOWN_BACKPACK_ITEMS.get(type);
    }

    public static Collection<BackpackItem> getAll() {
        return Collections.unmodifiableCollection(KNOWN_BACKPACK_ITEMS.values());
    }

    static {
        OutTheDoor.CHANNEL.registerServerbound(OpenBackpackPacket.class, (message, access) -> {
            TrinketsApi.getTrinketComponent(access.player()).ifPresent(trinkets -> {
                var backpacks = trinkets.getEquipped(stack -> stack.getItem() instanceof BackpackItem);
                if (!backpacks.isEmpty()) {
                    var backpackStack = backpacks.get(0).getRight();
                    ((BackpackItem) backpackStack.getItem()).openScreen(backpackStack, access.player());
                } else if (access.player().getEquippedStack(EquipmentSlot.HEAD).getItem() instanceof BackpackItem backpack) {
                    backpack.openScreen(access.player().getEquippedStack(EquipmentSlot.HEAD), access.player());
                }
            });
        });
    }

    public record ScreenData(BackpackType type, boolean restoreParent) {
        public static final StructEndec<ScreenData> ENDEC = StructEndecBuilder.of(
            MinecraftEndecs.ofRegistry(OutTheDoor.BACKPACK_REGISTRY).fieldOf("type", ScreenData::type),
            Endec.BOOLEAN.fieldOf("restore_parent", ScreenData::restoreParent),
            ScreenData::new
        );
    }
}
