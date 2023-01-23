package io.wispforest.outthedoor.misc;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = "out-the-door")
@Config(name = "out-the-door", wrapperName = "OutTheDoorConfig")
public class OutTheDoorConfigModel {

    public boolean returnToInventory = true;

    public boolean funkyBackpacks = false;

}
