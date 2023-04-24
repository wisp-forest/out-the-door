package io.wispforest.outthedoor.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.misc.BackpackScreenHandler;
import io.wispforest.outthedoor.misc.BackpackType;
import io.wispforest.owo.ui.util.Drawer;
import io.wispforest.owo.ui.util.OwoNinePatchRenderers;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BackpackScreen extends HandledScreen<BackpackScreenHandler> {

    public static final Identifier GENERIC_54_TEXTURE = new Identifier("textures/gui/container/generic_54.png");

    public static final int PLAYER_INVENTORY_HEIGHT = 76;
    public static final int SEPARATOR_HEIGHT = 14;
    public static final int TOP_PADDING = 17;
    public static final int SIDE_PADDING = 7;

    protected final BackpackType type;

    public BackpackScreen(BackpackScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.type = handler.type;

        this.backgroundWidth = SIDE_PADDING + Math.max(9 * 18, type.rowWidth() * 18) + SIDE_PADDING;
        this.backgroundHeight = TOP_PADDING + (18 * type.rows()) + SEPARATOR_HEIGHT + PLAYER_INVENTORY_HEIGHT + 7;
        this.playerInventoryTitleY = this.backgroundHeight - 94;

        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public void close() {
        if (this.handler.restoreParent && OutTheDoor.CONFIG.returnToInventory()) {
            this.client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(this.client.player.currentScreenHandler.syncId));
            this.client.setScreen(new InventoryScreen(this.client.player));
        } else {
            super.close();
        }

        // I absolutely detest this fix, but I don't have infinite time to
        // try and figure out why the inventory desyncs after using a
        // backpack but *fixes itself* when setting a breakpoint
        //
        // glisco, 24.04.2023
        this.client.interactionManager.clickSlot(this.client.player.playerScreenHandler.syncId, -999, 0, SlotActionType.CLONE, this.client.player);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        OwoNinePatchRenderers.LIGHT_PANEL.draw(matrices, this.x, this.y, this.backgroundWidth, this.backgroundHeight);

        Drawer.recordQuads();
        for (var slot : this.handler.slots) {
            Drawer.drawTexture(matrices, this.x + slot.x - 1, this.y + slot.y - 1, 7, 17, 18, 18, 256, 256);
        }

        RenderSystem.setShaderTexture(0, GENERIC_54_TEXTURE);
        Drawer.submitQuads();
    }
}
