package io.wispforest.outthedoor.mixin;

import io.wispforest.outthedoor.misc.OutTheDoorMouseExtension;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin implements OutTheDoorMouseExtension {

    @Unique
    private boolean otd$skipUnlock = false;

    @Override
    public void otd$skipUnlockCursor() {
        this.otd$skipUnlock = true;
    }

    @Inject(method = "unlockCursor", at = @At("HEAD"), cancellable = true)
    private void skipUnlock(CallbackInfo ci) {
        if (!this.otd$skipUnlock) return;

        this.otd$skipUnlock = false;
        ci.cancel();
    }
}
