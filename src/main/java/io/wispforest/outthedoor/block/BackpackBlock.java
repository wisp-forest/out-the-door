package io.wispforest.outthedoor.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BackpackBlock extends HorizontalFacingBlock implements BlockEntityProvider {

    private static final VoxelShape Z_SHAPE = Block.createCuboidShape(2.75, 0, 5.25, 12.75, 11, 11.25);
    private static final VoxelShape X_SHAPE = Block.createCuboidShape(5, 0, 3, 11, 11, 13);

    public BackpackBlock() {
        super(FabricBlockSettings.copyOf(Blocks.BROWN_WOOL).nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(FACING).getAxis() == Direction.Axis.X
                ? X_SHAPE
                : Z_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof BackpackBlockEntity backpack && backpack.hasBackpack()) {
            if (!world.isClient) {
                player.openHandledScreen(new NamedScreenHandlerFactory() {
                    @Override
                    public Text getDisplayName() {
                        return backpack.backpack.getName();
                    }

                    @Nullable
                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                        return switch (backpack.type().slots()) {
                            case 27 -> GenericContainerScreenHandler.createGeneric9x3(syncId, inv, backpack.createTrackedInventory());
                            case 54 -> GenericContainerScreenHandler.createGeneric9x6(syncId, inv, backpack.createTrackedInventory());
                            default -> null;
                        };
                    }
                });
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
                ItemScatterer.spawn(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, backpack.backpack);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BackpackBlockEntity(pos, state);
    }
}
