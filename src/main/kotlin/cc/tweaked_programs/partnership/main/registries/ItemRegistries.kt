package cc.tweaked_programs.partnership.main.registries

import cc.tweaked_programs.partnership.main.MOD_ID
import cc.tweaked_programs.partnership.main.entity.Kayak
import cc.tweaked_programs.partnership.main.item.*
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.*

@Suppress("JoinDeclarationAndAssignment", "MemberVisibilityCanBePrivate")
object ItemRegistries {

    val registered = mutableListOf<Item>()

    private fun <T : Item> create(
        name: String,
        category: ResourceKey<CreativeModeTab>? = null,
        categoryRegister: ((FabricItemGroupEntries, Item) -> Unit)? = null,
        itemSupplier: (FabricItemSettings) -> T): T {
        val item = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation(MOD_ID, name),
            itemSupplier.invoke(FabricItemSettings())
        )

        registered.add(item)
        if (category != null && categoryRegister != null)
            ItemGroupEvents.modifyEntriesEvent(category).register { content -> categoryRegister.invoke(content, item) }

        return item
    }

    val BOATYARD: BoatyardItem
    val KAYAK: GenericBoatItem
    val METAL_SCAFFOLDING: BlockItem
    val PADDLE: PaddleItem
    val BUOY: DescriptivePlaceOnWaterBlockItem
    val CAPTAINS_HAT: Hat
    val SAILORS_HAT: Hat
    val CANVAS_ROLL: Item
    val PLANK: Item

    init {
        BOATYARD = create(
            name = "boatyard",
            category = CreativeModeTabs.FUNCTIONAL_BLOCKS,
            categoryRegister = { content, item ->
                content.addAfter(Items.CRAFTING_TABLE, item)
            },
            itemSupplier = { properties ->
                BoatyardItem(
                    BlockRegistries.BOATYARD,
                    properties.maxCount(64))
            }
        )

        KAYAK = create(
            name = "kayak",
            category = CreativeModeTabs.TOOLS_AND_UTILITIES,
            categoryRegister = { content, item ->
                content.addAfter(Items.BAMBOO_CHEST_RAFT, item)
            },
            itemSupplier = { properties ->
                properties.maxCount(1)
                GenericBoatItem(properties) { level, x, y, z ->
                    Kayak(level, x, y, z)
                }
            }
        )

        METAL_SCAFFOLDING = create(
            name = "metal_scaffolding",
            category = CreativeModeTabs.BUILDING_BLOCKS,
            categoryRegister = { content, item ->
                content.addAfter(Items.IRON_BLOCK, item)
            },
            itemSupplier = { properties ->
                BlockItem(BlockRegistries.METAL_SCAFFOLDING, properties.maxCount(64))
            }
        )

        PADDLE = create(
            name = "paddle",
            category = CreativeModeTabs.COMBAT,
            categoryRegister = { content, item ->
                content.addAfter(Items.TRIDENT.asItem(), item)
            },
            itemSupplier = { properties ->
            PaddleItem(1, 0.25F, (-1.8f), properties
                .maxCount(1)
                .defaultDurability(Tiers.WOOD.uses)
                .durability(69))
            }
        )

        BUOY = create(
            name = "buoy",
            category = CreativeModeTabs.BUILDING_BLOCKS,
            categoryRegister = { content, item ->
                content.addAfter(METAL_SCAFFOLDING, item)
            },
            itemSupplier = { properties ->
                DescriptivePlaceOnWaterBlockItem(
                    BlockRegistries.BUOY,
                    properties.maxCount(64))
            }
        )

        CAPTAINS_HAT = create(
            name = "captains_hat",
            category = CreativeModeTabs.COMBAT,
            categoryRegister = { content, item ->
                content.addAfter(Items.TURTLE_HELMET, item)
            },
            itemSupplier = { properties ->
                Hat(BlockRegistries.CAPTAINS_HAT, SoundEvents.ARMOR_EQUIP_LEATHER, properties.maxCount(1))
            }
        )

        SAILORS_HAT = create(
            name = "sailors_hat",
            category = CreativeModeTabs.COMBAT,
            categoryRegister = { content, item ->
                content.addAfter(CAPTAINS_HAT, item)
            },
            itemSupplier = { properties ->
                Hat(BlockRegistries.SAILORS_HAT, SoundEvents.ARMOR_EQUIP_LEATHER, properties.maxCount(1))
            }
        )

        CANVAS_ROLL = create(
            name = "canvas_roll",
            category = CreativeModeTabs.INGREDIENTS,
            categoryRegister = { content, item ->
                content.addAfter(Items.EGG, item)
            },
            itemSupplier = { properties -> Item(properties.maxCount(64)) }
        )

        PLANK = create(
            name = "plank",
            category = CreativeModeTabs.INGREDIENTS,
            categoryRegister = { content, item ->
                content.addAfter(CANVAS_ROLL, item)
            },
            itemSupplier = { properties -> Item(properties.maxCount(64)) }
        )
    }
}