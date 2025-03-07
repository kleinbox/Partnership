package dev.kleinbox.partnership.main.registries

import dev.kleinbox.partnership.main.MOD_ID
import dev.kleinbox.partnership.main.block.AnchorBlock
import dev.kleinbox.partnership.main.block.MetalScaffoldingBlock
import dev.kleinbox.partnership.main.block.boatyard.BoatyardBlock
import dev.kleinbox.partnership.main.block.buoy.BuoyBlock
import dev.kleinbox.partnership.main.block.cannon.MarineCannonBlock
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.PushReaction


object BlockRegistries {

    private fun <T : Block> create(
        name: String,
        blockSupplier: (BlockBehaviour.Properties) -> T): T {

        return Registry.register(
            BuiltInRegistries.BLOCK,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
            blockSupplier.invoke(BlockBehaviour.Properties.of())
        )
    }

    val BOATYARD: BoatyardBlock = create(
        name = "boatyard"
    ) { properties ->
        BoatyardBlock(
            properties
                .strength(1F)
                .sound(SoundType.STONE)
        )
    }

    val METAL_SCAFFOLDING: MetalScaffoldingBlock = create(
        name = "metal_scaffolding"
    ) { properties ->
        MetalScaffoldingBlock(
            properties
                .strength(0.9F)
                .sound(SoundType.METAL)
                .noOcclusion()
        )
    }

    val BUOY: BuoyBlock = create(
        name = "buoy"
    ) { properties ->
        BuoyBlock(properties
            .strength(0.1F)
            .noOcclusion()
            .sound(SoundType.CHAIN))
    }

    val CHAIN_BUOY: dev.kleinbox.partnership.main.block.buoy.ChainBuoyBlock = create(
        name = "chain_buoy"
    ) { properties ->
        dev.kleinbox.partnership.main.block.buoy.ChainBuoyBlock(
            properties
                .strength(0.075F)
                .noOcclusion()
                .sound(SoundType.CHAIN)
        )
    }

    val MARINE_CANNON = create(
        name = "marine_cannon"
    ) { properties ->
        MarineCannonBlock(properties
            .sound(SoundType.STONE)
            .strength(1.25F)
            .noOcclusion())
    }

    val ANCHOR = create(
        name = "anchor"
    ) { properties ->
        AnchorBlock(
            properties
                .sound(SoundType.METAL)
                .strength(6F)
                .pushReaction(PushReaction.IGNORE)
                .noOcclusion()
        )
    }
}