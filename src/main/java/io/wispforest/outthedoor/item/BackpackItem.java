package io.wispforest.outthedoor.item;

import io.wispforest.outthedoor.OutTheDoor;
import io.wispforest.outthedoor.block.BackpackBlockEntity;
import io.wispforest.outthedoor.misc.BackpackType;
import io.wispforest.outthedoor.object.OutTheDoorBlocks;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BackpackItem extends BlockItem {

    public final BackpackType type;

    public BackpackItem(BackpackType type) {
        super(
                OutTheDoorBlocks.BACKPACK,
                new OwoItemSettings().group(OutTheDoor.GROUP).maxCount(1)
        );

        this.type = type;
    }

    @Override
    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getPlayer().isSneaking()) {
            return ActionResult.PASS;
        } else {
            return super.useOnBlock(context);
        }
    }

    @Override
    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        super.postPlacement(pos, world, player, stack, state);

        if (world.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
            backpack.backpack = stack.copy();
        }

        return true;
    }
}
