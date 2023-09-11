package io.wispforest.outthedoor.client.screen;

import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.misc.BackpackScreenHandler;
import io.wispforest.outthedoor.misc.BackpackType;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public void close() {
        if (this.handler.restoreParent && OutTheDoor.CONFIG.returnToInventory()) {
            this.client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(this.client.player.currentScreenHandler.syncId));
            this.client.setScreen(new InventoryScreen(this.client.player));
        } else {
            super.close();
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        this.renderBackground(context);

        var owoContext = OwoUIDrawContext.of(context);
        owoContext.drawPanel(this.x, this.y, this.backgroundWidth, this.backgroundHeight, false);

        owoContext.recordQuads();
        for (var slot : this.handler.slots) {
            owoContext.drawTexture(GENERIC_54_TEXTURE, this.x + slot.x - 1, this.y + slot.y - 1, 7, 17, 18, 18, 256, 256);
        }

        owoContext.submitQuads();
    }
}
