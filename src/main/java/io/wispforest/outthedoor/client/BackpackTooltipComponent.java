package io.wispforest.outthedoor.client;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

public class BackpackTooltipComponent implements TooltipComponent {

    private final GridLayout grid;

    public BackpackTooltipComponent(DefaultedList<ItemStack> stacks) {
        this.grid = Containers.grid(Sizing.content(), Sizing.content(), MathHelper.ceilDiv(stacks.size(), 9), 9);

        for (int i = 0; i < stacks.size(); i++) {
            this.grid.padding(Insets.bottom(5));

            var stack = stacks.get(i);
            this.grid.child(
                    Components.item(stack).showOverlay(true).margins(Insets.of(1)), i / 9, i % 9
            );
        }
        this.grid.inflate(Size.of(1000, 1000));
        this.grid.mount(null, 0, 0);
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        context = OwoUIDrawContext.of(context);
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 1000);

        this.grid.moveTo(x, y);
        this.grid.draw((OwoUIDrawContext) context, 0, 0, 0, 0);

        context.getMatrices().pop();
    }

    @Override
    public int getHeight() {
        return this.grid.height();
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return this.grid.width();
    }
}
