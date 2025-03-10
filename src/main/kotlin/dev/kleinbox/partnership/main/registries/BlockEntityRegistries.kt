package dev.kleinbox.partnership.main.registries

import dev.kleinbox.partnership.main.MOD_ID
import dev.kleinbox.partnership.main.block.boatyard.BoatyardBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

object BlockEntityRegistries {

    private fun <T : Block, U : BlockEntity> create(
        name: String,
        block: T,
        blockEntitySupplier: (BlockPos, BlockState) -> U): BlockEntityType<U> {

        return Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
            BlockEntityType.Builder.of(blockEntitySupplier, block).build()
        )
    }

    val BOATYARD = create(
        name = "boatyard_block_entity",
        block = BlockRegistries.BOATYARD
    ) { pos, state -> BoatyardBlockEntity(pos, state) }

    val MARINE_CANNON = create(
        name = "marine_cannon_block_entity",
        block = BlockRegistries.MARINE_CANNON
    ) { pos, state -> dev.kleinbox.partnership.main.block.cannon.MarineCannonBlockEntity(pos, state) }
}