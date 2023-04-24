package io.wispforest.outthedoor.client;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.client.model.BackpackUnbakedModel;
import io.wispforest.outthedoor.client.screen.BackpackScreen;
import io.wispforest.outthedoor.item.BackpackItem;
import io.wispforest.outthedoor.misc.BackpackTooltipData;
import io.wispforest.outthedoor.misc.OpenBackpackPacket;
import io.wispforest.outthedoor.object.OutTheDoorBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
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

            if (openScreen) OutTheDoor.CHANNEL.clientHandle().send(new OpenBackpackPacket());
        });

        HandledScreens.register(OutTheDoor.BACKPACK_SCREEN_HANDLER, BackpackScreen::new);

        TooltipComponentCallback.EVENT.register(data -> {
            return data instanceof BackpackTooltipData backpackData
                    ? new BackpackTooltipComponent(backpackData.items())
                    : null;
        });

        for (var backpack : BackpackItem.getAll()) {
            TrinketRendererRegistry.registerRenderer(backpack, (stack, slotReference, contextModel, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch) -> {
                matrices.translate(0, .75, 0);
                if (entity.isBaby()) {
                    //noinspection unchecked
                    translateToFace(matrices, (BipedEntityModel<LivingEntity>) contextModel, entity);
                } else {
                    //noinspection unchecked
                    translateToChest(matrices, (BipedEntityModel<LivingEntity>) contextModel, entity);
                }
                matrices.translate(0, -.75, 0);

                if (!OutTheDoor.CONFIG.funkyBackpacks()) {
                    matrices.translate(0, entity.isBaby() ? .05 : -.1, entity.isBaby() ? .55 : .375);
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                }

                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.world, 0);
            });
        }
    }

    // Copied straight from TrinketRenderer
    // Pretty cool how that method requires a player
    static void translateToChest(MatrixStack matrices, BipedEntityModel<LivingEntity> model,
                                 LivingEntity entity) {

        if (entity.isInSneakingPose() && !model.riding && !entity.isSwimming()) {
            matrices.translate(0.0F, 0.12F, 0.38F);
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(model.body.pitch));
        }

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(model.body.yaw));
        matrices.translate(0.0F, 0.4F, -0.16F);
    }

    // Copied straight from TrinketRenderer and patched to respect babies
    // Pretty cool how that method requires a player
    static void translateToFace(MatrixStack matrices, BipedEntityModel<LivingEntity> model,
                                LivingEntity entity) {

        if (entity.isInSwimmingPose() || entity.isFallFlying()) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotation(model.head.roll));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(model.head.yaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-45.0F));
        } else {
            if (entity.isInSneakingPose() && !model.riding) {
                matrices.translate(0.0F, 0.25F, 0.0F);
            }

            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(model.head.yaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(model.head.pitch));
        }

        if (entity.isBaby()) {
            matrices.translate(0.0F, .45, -0.3F);
        } else {
            matrices.translate(0.0F, -0.25F, -0.3F);
        }
    }
}
