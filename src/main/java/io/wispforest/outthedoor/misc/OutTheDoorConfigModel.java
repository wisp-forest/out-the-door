package io.wispforest.outthedoor.misc;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;

@Modmenu(modId = "out-the-door")
@Config(name = "out-the-door", wrapperName = "OutTheDoorConfig")
public class OutTheDoorConfigModel {

    public boolean returnToInventory = true;

    public boolean funkyBackpacks = false;

    public boolean alwaysDisplayContents = false;

    @RangeConstraint(min = 0, max = 100)
    public float zombieBackpackChance = 0.5f;
}
