package dev.kleinbox.partnership.main.block.cannon

import com.mojang.serialization.MapCodec
import dev.kleinbox.partnership.main.Partnership
import dev.kleinbox.partnership.main.registries.ItemRegistries
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape


class MarineCannonBlock(properties: Properties) : BaseEntityBlock(properties), EntityBlock {

    override fun codec(): MapCodec<out BaseEntityBlock> = BlockBehaviour.simpleCodec(::MarineCannonBlock)

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
            .setValue(TRIGGERED, false)
        )
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity =
        MarineCannonBlockEntity(blockPos, blockState)

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide())
            Partnership.proxy.openMarineScreen(pos)
        return InteractionResult.SUCCESS
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity !is MarineCannonBlockEntity)
            return ItemInteractionResult.FAIL

        if (player.mainHandItem.item != ItemRegistries.CANNONBALL)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

        if (level.isClientSide) return ItemInteractionResult.FAIL

        if (blockEntity.balls >= MarineCannonBlockEntity.MAX_LOAD)
            return ItemInteractionResult.SUCCESS

        if (!player.isCreative)
            player.mainHandItem.count--
        blockEntity.balls++
        blockEntity.setChanged()

        level.playSound(null, pos, SoundEvents.DECORATED_POT_PLACE, SoundSource.BLOCKS, 0.6f, 0.3F)

        return ItemInteractionResult.SUCCESS
    }

    override fun neighborChanged(state: BlockState, level: Level, pos: BlockPos, block: Block, blockPos2: BlockPos, bl: Boolean) {
        val powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above())
        val triggered = state.getValue(TRIGGERED) as Boolean
        if (powered && !triggered) {
            level.scheduleTick(pos, this, 4)
            level.setBlock(pos, state.setValue(TRIGGERED, true), 2)
        } else if (!powered && triggered)
            level.setBlock(pos, state.setValue(TRIGGERED, false), 2)
    }

    override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is MarineCannonBlockEntity && blockEntity.balls > 0) {
            blockEntity.shoot()
            level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1f, 1.6F)
            blockEntity.balls--
            blockEntity.setChanged()
        }
    }

    override fun isSignalSource(state: BlockState): Boolean = true

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) { builder
        .add(BlockStateProperties.HORIZONTAL_FACING)
        .add(TRIGGERED)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? = defaultBlockState()
        .setValue(BlockStateProperties.HORIZONTAL_FACING, if (ctx.player?.isCrouching == true) ctx.horizontalDirection.opposite else ctx.horizontalDirection)
        .setValue(TRIGGERED, false)

    override fun triggerEvent(blockState: BlockState, level: Level, blockPos: BlockPos, i: Int, j: Int): Boolean {
        super.triggerEvent(blockState, level, blockPos, i, j)
        val blockEntity = level.getBlockEntity(blockPos) ?: return false
        return blockEntity.triggerEvent(i, j)
    }

    override fun rotate(blockState: BlockState, rotation: Rotation): BlockState {
        return blockState.setValue(HorizontalDirectionalBlock.FACING, rotation.rotate(blockState.getValue(HorizontalDirectionalBlock.FACING) as Direction)) as BlockState
    }

    override fun mirror(blockState: BlockState, mirror: Mirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.getValue(HorizontalDirectionalBlock.FACING) as Direction))
    }

    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.INVISIBLE

    override fun getShape(blockState: BlockState, blockGetter: BlockGetter, blockPos: BlockPos,
                          collisionContext: CollisionContext
    ): VoxelShape = shape

    companion object {
        val shape: VoxelShape = Shapes.empty()
            .let { Shapes.or(it, Shapes.create(0.31875, 0.31875, 0.31875, 0.68125, 0.68125, 0.68125)) }
        val TRIGGERED: BooleanProperty = BlockStateProperties.TRIGGERED
    }
}