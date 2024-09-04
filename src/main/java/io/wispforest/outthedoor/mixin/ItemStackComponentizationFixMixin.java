package io.wispforest.outthedoor.mixin;

import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.fix.ItemStackComponentizationFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin {

    @Unique private static final Set<String> BACKPACKS = Set.of(
        "out-the-door:leather_backpack",
        "out-the-door:hide_backpack",
        "out-the-door:pumpkin_backpack"
    );

    @Inject(method = "fixStack", at = @At("TAIL"))
    private static void fixBackpackInventories(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic, CallbackInfo ci) {
        if (data.itemMatches(BACKPACKS)) {
            List<Dynamic<?>> list = data.getAndRemove("Items")
                .asList(
                    itemsDynamic -> itemsDynamic.emptyMap()
                        .set("slot", itemsDynamic.createInt(itemsDynamic.get("Slot").asByte((byte)0) & 255))
                        .set("item", itemsDynamic.remove("Slot"))
                );

            if (!list.isEmpty()) {
                data.setComponent("minecraft:container", dynamic.createList(list.stream()));
            }
        }
    }

}
