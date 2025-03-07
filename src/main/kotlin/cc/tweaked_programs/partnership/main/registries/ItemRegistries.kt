package cc.tweaked_programs.partnership.main.registries

import cc.tweaked_programs.partnership.main.MOD_ID
import cc.tweaked_programs.partnership.main.entity.Kayak
import cc.tweaked_programs.partnership.main.entity.Lifebuoy
import cc.tweaked_programs.partnership.main.item.BoatyardItem
import cc.tweaked_programs.partnership.main.item.Hat
import cc.tweaked_programs.partnership.main.item.extendable.DescriptiveBlockItem
import cc.tweaked_programs.partnership.main.item.extendable.DescriptivePlaceOnWaterBlockItem
import cc.tweaked_programs.partnership.main.item.extendable.GenericBoatItem
import cc.tweaked_programs.partnership.main.item.extendable.GenericDescriptiveBlockItem
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.*

@Suppress("JoinDeclarationAndAssignment", "MemberVisibilityCanBePrivate")
object ItemRegistries {

    val registered = mutableListOf<Item>()

    private fun <T : Item> create(
        name: String,
        category: ResourceKey<CreativeModeTab>? = null,
        categoryRegister: ((FabricItemGroupEntries, Item) -> Unit)? = null,
        itemSupplier: (Item.Properties) -> T): T {
        val item = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
            itemSupplier.invoke(Item.Properties())
        )

        registered.add(item)
        if (category != null && categoryRegister != null)
            ItemGroupEvents.modifyEntriesEvent(category).register { content -> categoryRegister.invoke(content, item) }

        return item
    }

    val BOATYARD: BoatyardItem
    val KAYAK: GenericBoatItem
    val LIFEBUOY: GenericBoatItem
    val METAL_SCAFFOLDING: BlockItem
    val BUOY: DescriptivePlaceOnWaterBlockItem
    val CHAIN_BUOY: DescriptivePlaceOnWaterBlockItem
    val CAPTAINS_HAT: Hat
    val SAILORS_HAT: Hat
    val CANVAS_ROLL: Item
    val PLANK: Item
    val MARINE_CANNON: Item
    val CANNONBALL: Item
    val ANCHOR: Item

    init {
        KAYAK = create(
            name = "kayak",
            category = CreativeModeTabs.TOOLS_AND_UTILITIES,
            categoryRegister = { content, item ->
                content.addAfter(Items.BAMBOO_CHEST_RAFT, item)
            },
            itemSupplier = { properties ->
                properties.stacksTo(1)
                GenericBoatItem(
                    properties = properties,
                    boat = { level, x, y, z ->
                        Kayak(level, x, y, z)
                    },
                    speed = Kayak.SPEED_RANK,
                    mobility = Kayak.MOBILITY_RANK,
                    space = Kayak.SPACE_RANK
                )
            }
        )

        LIFEBUOY = create(
            name = "lifebuoy",
            category = CreativeModeTabs.TOOLS_AND_UTILITIES,
            categoryRegister = { content, item ->
                content.addAfter(KAYAK, item)
            },
            itemSupplier = { properties ->
                properties.stacksTo(1)
                GenericBoatItem(
                    properties = properties,
                    boat = { level, x, y, z ->
                        Lifebuoy(level, x, y, z)
                    },
                    speed = Lifebuoy.SPEED_RANK,
                    mobility = Lifebuoy.MOBILITY_RANK,
                    space = Lifebuoy.SPACE_RANK
                )
            }
        )

        BOATYARD = create(
            name = "boatyard",
            category = CreativeModeTabs.FUNCTIONAL_BLOCKS,
            categoryRegister = { content, item ->
                content.addAfter(Items.CRAFTING_TABLE, item)
            },
            itemSupplier = { properties ->
                BoatyardItem(
                    BlockRegistries.BOATYARD,
                    properties.stacksTo(64))
            }
        )

        METAL_SCAFFOLDING = create(
            name = "metal_scaffolding",
            category = CreativeModeTabs.BUILDING_BLOCKS,
            categoryRegister = { content, item ->
                content.addAfter(Items.IRON_BLOCK, item)
            },
            itemSupplier = { properties ->
                BlockItem(BlockRegistries.METAL_SCAFFOLDING, properties.stacksTo(64))
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
                    properties.stacksTo(64))
            }
        )

        CHAIN_BUOY = create(
            name = "chain_buoy",
            category = CreativeModeTabs.BUILDING_BLOCKS,
            categoryRegister = { content, item ->
                content.addAfter(METAL_SCAFFOLDING, item)
            },
            itemSupplier = { properties ->
                DescriptivePlaceOnWaterBlockItem(
                    BlockRegistries.CHAIN_BUOY,
                    properties.stacksTo(64))
            }
        )

        MARINE_CANNON = create(
            name = "marine_cannon",
            category = CreativeModeTabs.REDSTONE_BLOCKS,
            categoryRegister = { content, item ->
                content.addAfter(Items.TNT, item)
            },
            itemSupplier = { properties ->
                GenericDescriptiveBlockItem(
                    BlockRegistries.MARINE_CANNON,
                    properties.stacksTo(64), true)
            }
        )

        CAPTAINS_HAT = create(
            name = "captains_hat",
            category = CreativeModeTabs.COMBAT,
            categoryRegister = { content, item ->
                content.addAfter(Items.TURTLE_HELMET, item)
            },
            itemSupplier = { properties ->
                Hat(properties.stacksTo(1))
            }
        )

        SAILORS_HAT = create(
            name = "sailors_hat",
            category = CreativeModeTabs.COMBAT,
            categoryRegister = { content, item ->
                content.addAfter(CAPTAINS_HAT, item)
            },
            itemSupplier = { properties ->
                Hat(properties.stacksTo(1))
            }
        )

        CANVAS_ROLL = create(
            name = "canvas_roll",
            category = CreativeModeTabs.INGREDIENTS,
            categoryRegister = { content, item ->
                content.addAfter(Items.EGG, item)
            },
            itemSupplier = { properties -> Item(properties.stacksTo(64)) }
        )

        PLANK = create(
            name = "plank",
            category = CreativeModeTabs.INGREDIENTS,
            categoryRegister = { content, item ->
                content.addAfter(CANVAS_ROLL, item)
            },
            itemSupplier = { properties -> Item(properties.stacksTo(64)) }
        )

        CANNONBALL = create(
            name = "cannonball",
            category = CreativeModeTabs.COMBAT,
            categoryRegister = { content, item ->
                content.addAfter(PLANK, item)
            },
            itemSupplier = { properties -> Item(properties.stacksTo(16)) }
        )

        ANCHOR = create(
            name = "anchor",
            category = CreativeModeTabs.BUILDING_BLOCKS,
            categoryRegister = { content, item ->
                content.addAfter(Items.NETHERITE_BLOCK, item)
            },
            itemSupplier = { properties -> DescriptiveBlockItem(BlockRegistries.ANCHOR, properties.stacksTo(1)) }
        )
    }
}