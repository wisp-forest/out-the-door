package io.wispforest.outthedoor.mixin;

import io.wispforest.outthedoor.client.screen.BackpackScreen;
import io.wispforest.outthedoor.misc.OutTheDoorMouseExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Shadow
    @Final
    public Mouse mouse;

    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", opcode = Opcodes.PUTFIELD))
    private void skipMouseUnlock(Screen screen, CallbackInfo ci) {
        if (this.currentScreen == null || screen == null || (!(screen instanceof BackpackScreen)) && !(this.currentScreen instanceof BackpackScreen)) return;
        ((OutTheDoorMouseExtension) this.mouse).outTheDoor$skipUnlockCursor();
    }

}
