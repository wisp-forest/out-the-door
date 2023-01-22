package io.wispforest.outthedoor.object;

import io.wispforest.outthedoor.item.BackpackItem;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.item.Item;

public class OutTheDoorItems implements ItemRegistryContainer {

    public static final Item LEATHER_BACKPACK = new BackpackItem(OutTheDoorBackpackTypes.LEATHER);
    public static final Item HIDE_BACKPACK = new BackpackItem(OutTheDoorBackpackTypes.HIDE);
    public static final Item PUMPKIN_BACKPACK = new BackpackItem(OutTheDoorBackpackTypes.PUMPKIN);

}
