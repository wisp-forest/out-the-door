package io.wispforest.outthedoor.mixin;

import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.*;
import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.object.OutTheDoorItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {

    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initEquipment", at = @At("TAIL"))
    private void addHeadpacc(Random random, LocalDifficulty localDifficulty, CallbackInfo ci) {
        if (random.nextFloat() > OutTheDoor.CONFIG.zombieBackpackChance() / 100f) return;
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        var lootTable = serverWorld.getServer().getReloadableRegistries().getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, OutTheDoor.id("gameplay/zombie_backpack")));

        var parameterSet = new LootContextParameterSet.Builder(serverWorld)
                .add(LootContextParameters.THIS_ENTITY, this)
                .add(LootContextParameters.ORIGIN, this.getPos()).build(LootContextTypes.CHEST);

        var backpack = OutTheDoorItems.LEATHER_BACKPACK.getDefaultStack();
        var inventory = OutTheDoorItems.LEATHER_BACKPACK.createTrackedInventory(backpack);

        lootTable.supplyInventory(inventory, parameterSet, 0);
        otd$equipItem(this, backpack);
    }

    // Copied straight from TrinketItem
    // Pretty cool how that method requires a player
    private static boolean otd$equipItem(LivingEntity user, ItemStack stack) {
        var optional = TrinketsApi.getTrinketComponent(user);
        if (optional.isPresent()) {
            TrinketComponent comp = optional.get();
            for (var group : comp.getInventory().values()) {
                for (TrinketInventory inv : group.values()) {
                    for (int i = 0; i < inv.size(); i++) {
                        if (inv.getStack(i).isEmpty()) {
                            SlotReference ref = new SlotReference(inv, i);
                            if (TrinketSlot.canInsert(stack, ref, user)) {
                                ItemStack newStack = stack.copy();
                                inv.setStack(i, newStack);
                                Trinket trinket = TrinketsApi.getTrinket(stack.getItem());
                                RegistryEntry<SoundEvent> soundEvent = trinket.getEquipSound(stack, ref, user);
                                if (!stack.isEmpty() && soundEvent != null) {
                                    user.emitGameEvent(GameEvent.EQUIP);
                                    user.playSound(soundEvent.value(), 1.0F, 1.0F);
                                }
                                stack.setCount(0);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
