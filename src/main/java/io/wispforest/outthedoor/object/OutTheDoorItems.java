package io.wispforest.outthedoor.object;

import io.wispforest.outthedoor.item.BackpackItem;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;

public class OutTheDoorItems implements ItemRegistryContainer {

    public static final BackpackItem PUMPKIN_BACKPACK = new BackpackItem(OutTheDoorBackpackTypes.PUMPKIN);
    public static final BackpackItem LEATHER_BACKPACK = new BackpackItem(OutTheDoorBackpackTypes.LEATHER);
    public static final BackpackItem HIDE_BACKPACK = new BackpackItem(OutTheDoorBackpackTypes.HIDE);

}
