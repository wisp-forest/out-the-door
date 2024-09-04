package io.wispforest.outthedoor.block;

import com.mojang.serialization.MapCodec;
import dev.emi.trinkets.api.TrinketItem;
import io.wispforest.outthedoor.item.BackpackItem;
import io.wispforest.outthedoor.misc.BackpackScreenHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BackpackBlock extends HorizontalFacingBlock implements BlockEntityProvider {

    private static final VoxelShape Z_SHAPE = Block.createCuboidShape(2.75, 0, 5.25, 12.75, 11, 11.25);
    private static final VoxelShape X_SHAPE = Block.createCuboidShape(5, 0, 3, 11, 11, 13);

    public BackpackBlock() {
        super(FabricBlockSettings.copyOf(Blocks.BROWN_WOOL).nonOpaque());
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockEntity(pos) instanceof BackpackBlockEntity backpack
                ? BackpackItem.get(backpack.type()).getDefaultStack()
                : super.getPickStack(world, pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof BackpackBlockEntity backpack && backpack.hasBackpack()) {
            if (!world.isClient) {
                if (player.isSneaking()) {
                    if (!TrinketItem.equipItem(player, backpack.backpack())) return ActionResult.PASS;
                    world.removeBlock(pos, false);
                } else {
                    player.openHandledScreen(new ExtendedScreenHandlerFactory<BackpackItem.ScreenData>() {
                        @Override
                        public BackpackItem.ScreenData getScreenOpeningData(ServerPlayerEntity player) {
                            return new BackpackItem.ScreenData(backpack.type(), false);
                        }

                        @Override
                        public Text getDisplayName() {
                            return backpack.backpack().getName();
                        }

                        @Override
                        public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                            return new BackpackScreenHandler(syncId, inv, backpack.inventory(), backpack.type(), user -> world.getBlockState(pos).isOf(BackpackBlock.this));
                        }
                    });
                }
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
                ItemScatterer.spawn(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, backpack.backpack());
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BackpackBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return null;
    }
}
