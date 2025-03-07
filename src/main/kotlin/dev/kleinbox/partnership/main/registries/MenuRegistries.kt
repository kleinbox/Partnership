package dev.kleinbox.partnership.main.registries

import dev.kleinbox.partnership.main.MOD_ID
import dev.kleinbox.partnership.main.menu.BoatyardMenu
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.MenuType

object MenuRegistries {
    val BOATYARD: MenuType<BoatyardMenu> = Registry.register(
        BuiltInRegistries.MENU,
        ResourceLocation.fromNamespaceAndPath(MOD_ID, "boatyard_menu"),
        MenuType(::BoatyardMenu, FeatureFlagSet.of())
    )
}