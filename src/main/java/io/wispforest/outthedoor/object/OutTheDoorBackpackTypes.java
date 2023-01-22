package io.wispforest.outthedoor.object;

import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.misc.BackpackType;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class OutTheDoorBackpackTypes implements AutoRegistryContainer<BackpackType> {

    public static final BackpackType LEATHER = new Type("leather", OutTheDoor.id("item/leather_backpack"), 27);
    public static final BackpackType HIDE = new Type("hide", OutTheDoor.id("item/hide_backpack"), 54);
    public static final BackpackType PUMPKIN = new Type("pumpkin", OutTheDoor.id("item/pumpkin_backpack"), 27);

    @Override
    public Registry<BackpackType> getRegistry() {
        return OutTheDoor.BACKPACK_REGISTRY;
    }

    @Override
    public Class<BackpackType> getTargetFieldType() {
        return BackpackType.class;
    }

    public record Type(String name, Identifier model, int slots) implements BackpackType {}
}
