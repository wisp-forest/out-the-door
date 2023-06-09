package io.wispforest.outthedoor;

import io.wispforest.outthedoor.misc.BackpackScreenHandler;
import io.wispforest.outthedoor.misc.BackpackType;
import io.wispforest.outthedoor.misc.OutTheDoorConfig;
import io.wispforest.outthedoor.object.OutTheDoorBackpackTypes;
import io.wispforest.outthedoor.object.OutTheDoorBlocks;
import io.wispforest.outthedoor.object.OutTheDoorItems;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class OutTheDoor implements ModInitializer {

    public static final String MOD_ID = "out-the-door";

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(id("network"));
    public static final OutTheDoorConfig CONFIG = OutTheDoorConfig.createAndLoad();

    public static final OwoItemGroup GROUP = OwoItemGroup
            .builder(id("out-the-door"), () -> Icon.of(OutTheDoorItems.LEATHER_BACKPACK))
            .build();

    public static final Registry<BackpackType> BACKPACK_REGISTRY = FabricRegistryBuilder.createSimple(RegistryKey.<BackpackType>ofRegistry(id("backpack")))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final ScreenHandlerType<BackpackScreenHandler> BACKPACK_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(BackpackScreenHandler::client);

    @Override
    public void onInitialize() {
        FieldRegistrationHandler.register(OutTheDoorBackpackTypes.class, MOD_ID, false);

        FieldRegistrationHandler.register(OutTheDoorBlocks.class, MOD_ID, true);
        FieldRegistrationHandler.register(OutTheDoorItems.class, MOD_ID, false);

        Registry.register(Registries.SCREEN_HANDLER, id("backpack"), BACKPACK_SCREEN_HANDLER);

        GROUP.initialize();
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
