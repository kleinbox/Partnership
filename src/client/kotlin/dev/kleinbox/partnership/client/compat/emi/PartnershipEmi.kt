package dev.kleinbox.partnership.client.compat.emi

import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiStack
import dev.kleinbox.partnership.main.MOD_ID
import dev.kleinbox.partnership.main.recipe.BoatyardRecipe
import dev.kleinbox.partnership.main.registries.ItemRegistries
import net.minecraft.resources.ResourceLocation

class PartnershipEmi : EmiPlugin {
    override fun register(registry: EmiRegistry) {
        registry.addCategory(BOATYARD_CATEGORY)

        registry.addWorkstation(BOATYARD_CATEGORY, BOATYARD_WORKSTATION)

        registry.recipeManager.getAllRecipesFor(BoatyardRecipe.Companion.Type.TYPE).forEach {
            registry.addRecipe(BoatyardEmiRecipe(it.value))
        }
    }

    companion object {
        private val SPRITES = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/emi_simplified_containers.png")

        val BOATYARD_WORKSTATION: EmiStack = EmiStack.of(ItemRegistries.BOATYARD)
        val BOATYARD_CATEGORY = EmiRecipeCategory(ResourceLocation.fromNamespaceAndPath(MOD_ID, "boatyard"), BOATYARD_WORKSTATION,
            EmiTexture(SPRITES, 0, 0, 16, 16))
    }
}