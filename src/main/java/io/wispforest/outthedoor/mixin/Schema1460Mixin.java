package io.wispforest.outthedoor.mixin;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.Schema1460;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(Schema1460.class)
public class Schema1460Mixin {

    @Inject(method = "registerBlockEntities", at = @At("TAIL"))
    private void injectBackpackTypeRef(Schema schema, CallbackInfoReturnable<Map<String, Supplier<TypeTemplate>>> cir) {
        schema.register(
            cir.getReturnValue(),
            "out-the-door:backpack",
            () -> DSL.optionalFields(
                "Backpack",
                TypeReferences.ITEM_STACK.in(schema)
            )
        );
    }

}
