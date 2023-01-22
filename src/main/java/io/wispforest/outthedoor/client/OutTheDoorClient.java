package io.wispforest.outthedoor.client;

import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.client.model.BackpackUnbakedModel;
import io.wispforest.outthedoor.item.BackpackItem;
import io.wispforest.outthedoor.misc.OpenTrinketBackpackPacket;
import io.wispforest.outthedoor.object.OutTheDoorBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class OutTheDoorClient implements ClientModInitializer {

    public static final KeyBinding OPEN_BACKPACK = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.out-the-door.openBackpack", GLFW.GLFW_KEY_B, "key.categories.inventory"
    ));

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(OutTheDoorBlocks.BACKPACK, RenderLayer.getCutout());

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(resourceManager -> (resourceId, context) -> {
            if (resourceId.equals(OutTheDoor.id("block/backpack"))) {
                return new BackpackUnbakedModel();
            } else {
                return null;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean openScreen = false;
            while (OPEN_BACKPACK.wasPressed()) openScreen = true;

            if (openScreen) OutTheDoor.CHANNEL.clientHandle().send(new OpenTrinketBackpackPacket());
        });

        for (var backpack : BackpackItem.getAll()) {
            TrinketRendererRegistry.registerRenderer(backpack, (stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> {
                if (!(entity instanceof AbstractClientPlayerEntity player)) return;

                //noinspection unchecked
                TrinketRenderer.translateToChest(matrices, (PlayerEntityModel<AbstractClientPlayerEntity>) contextModel, player);
                if (!OutTheDoor.CONFIG.funkyBackpacks()) {
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    matrices.translate(0, 0, -.375);
                }

                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
            });
        }
    }
}
