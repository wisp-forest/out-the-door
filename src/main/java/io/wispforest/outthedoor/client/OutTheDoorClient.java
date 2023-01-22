package io.wispforest.outthedoor.client;

import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.client.model.BackpackModel;
import io.wispforest.outthedoor.client.model.BackpackUnbakedModel;
import io.wispforest.outthedoor.object.OutTheDoorBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class OutTheDoorClient implements ClientModInitializer {

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
    }
}
