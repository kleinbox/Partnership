package dev.kleinbox.partnership.main.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.kleinbox.partnership.main.block.boatyard.BoatyardBlockEntity.Companion.INV_SIZE
import dev.kleinbox.partnership.main.recipe.BoatyardRecipe.Companion.Type.Companion.CODEC
import dev.kleinbox.partnership.main.recipe.BoatyardRecipe.Companion.Type.Companion.TYPE
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level


class BoatyardRecipe(private val ingredients: MutableList<ItemStack> = MutableList(INV_SIZE-1) {ItemStack.EMPTY },
                     private val output: ItemStack = ItemStack.EMPTY
) : Recipe<RecipeInput> {

    override fun matches(input: RecipeInput, level: Level): Boolean {
        ingredients.withIndex().forEach { slot ->
            if (slot.value.count > input.getItem(slot.index).count || !slot.value.item.equals(input.getItem(slot.index).item))
                return false
        }

        return true
    }

    override fun assemble(input: RecipeInput, registries: HolderLookup.Provider): ItemStack = getResultItem(registries).copy()

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack = output

    override fun getSerializer(): RecipeSerializer<BoatyardRecipe> = SERIALIZER

    override fun getType(): RecipeType<BoatyardRecipe> = TYPE

    fun getIngredientsAsItemStacks(): MutableList<ItemStack> {
        return ingredients
    }

    companion object {
        val SERIALIZER = Serializer()

        class Serializer : RecipeSerializer<BoatyardRecipe> {
            override fun codec(): MapCodec<BoatyardRecipe> = CODEC
            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, BoatyardRecipe>? = StreamCodec.of(::toNetwork, ::fromNetwork)

            fun fromNetwork(buffer: RegistryFriendlyByteBuf): BoatyardRecipe {
                val ingredients: MutableList<ItemStack> = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buffer)
                val output = ItemStack.STREAM_CODEC.decode(buffer)

                return BoatyardRecipe(ingredients, output)
            }

            fun toNetwork(buffer: RegistryFriendlyByteBuf, recipe: BoatyardRecipe) {
                ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buffer, recipe.getIngredientsAsItemStacks())
                ItemStack.STREAM_CODEC.encode(buffer, recipe.getResultItem(RegistryAccess.EMPTY))
            }
        }

        class Type : RecipeType<BoatyardRecipe> {
            companion object {
                val TYPE: Type = Type()

                val CODEC: MapCodec<BoatyardRecipe> =
                    RecordCodecBuilder.mapCodec { instance: RecordCodecBuilder.Instance<BoatyardRecipe> -> instance.group(
                        ItemStack.CODEC.orElse(ItemStack.EMPTY).sizeLimitedListOf(INV_SIZE-1).fieldOf("ingredients").forGetter { it.getIngredientsAsItemStacks() },
                        ItemStack.CODEC.fieldOf("output").forGetter { it.getResultItem(RegistryAccess.EMPTY) }
                    ).apply(instance) { output, ingredient ->
                        BoatyardRecipe(output, ingredient)
                    } }
            }
        }
    }
}
