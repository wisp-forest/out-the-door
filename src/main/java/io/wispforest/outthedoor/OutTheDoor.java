package io.wispforest.outthedoor;

import io.wispforest.outthedoor.misc.BackpackType;
import io.wispforest.outthedoor.object.OutTheDoorBackpackTypes;
import io.wispforest.outthedoor.object.OutTheDoorBlocks;
import io.wispforest.outthedoor.object.OutTheDoorItems;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class OutTheDoor implements ModInitializer {

    public static final String MOD_ID = "out-the-door";

    public static final OwoItemGroup GROUP = OwoItemGroup
            .builder(id("out-the-door"), () -> Icon.of(OutTheDoorItems.LEATHER_BACKPACK))
            .build();

    public static final Registry<BackpackType> BACKPACK_REGISTRY = FabricRegistryBuilder.createSimple(BackpackType.class, id("backpack"))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(OutTheDoorBackpackTypes.class, MOD_ID, false);

        FieldRegistrationHandler.register(OutTheDoorBlocks.class, MOD_ID, true);
        FieldRegistrationHandler.register(OutTheDoorItems.class, MOD_ID, false);

        GROUP.initialize();
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
