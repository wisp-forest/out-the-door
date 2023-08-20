package io.wispforest.outthedoor.mixin;

import dev.emi.trinkets.data.EntitySlotLoader;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntitySlotLoader.class)
public interface EntitySlotLoaderAccessor {

    @Invoker("getSlotsPacket")
    PacketByteBuf otd$getSlotsPacket();

}
