package dev.kleinbox.partnership.main.registries

import dev.kleinbox.partnership.main.MOD_ID
import dev.kleinbox.partnership.main.recipe.BoatyardRecipe
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation

object RecipeRegistries {

    init {
        Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "boatyard_construct_serializer"),
            BoatyardRecipe.SERIALIZER
        )

        Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "boatyard_construct_recipe"),
            BoatyardRecipe.Companion.Type.TYPE
        )
    }
}