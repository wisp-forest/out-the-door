package io.wispforest.outthedoor.client.model;

import com.google.common.collect.ImmutableMap;
import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.misc.BackpackType;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Function;

public class BackpackUnbakedModel implements UnbakedModel {

    @Override
    public Collection<Identifier> getModelDependencies() {
        return OutTheDoor.BACKPACK_REGISTRY.stream().map(BackpackType::model).toList();
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
        OutTheDoor.BACKPACK_REGISTRY.stream().map(BackpackType::model).forEach(identifier -> {
            modelLoader.apply(identifier).setParents(modelLoader);
        });
    }

    @Override
    public @Nullable BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings bakeSettings) {
        var models = ImmutableMap.<Identifier, BakedModel>builder();

        for (var backpackType : OutTheDoor.BACKPACK_REGISTRY) {
            models.put(backpackType.model(), baker.bake(backpackType.model(), bakeSettings));
        }

        return new BackpackModel(models.build());
    }

}
