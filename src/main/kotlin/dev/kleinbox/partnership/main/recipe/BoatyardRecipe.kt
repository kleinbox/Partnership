package dev.kleinbox.partnership.main.recipe

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.kleinbox.partnership.main.block.boatyard.BoatyardBlockEntity.Companion.INV_SIZE
import dev.kleinbox.partnership.main.recipe.BoatyardRecipe.Companion.Type.Companion.CODEC
import dev.kleinbox.partnership.main.recipe.BoatyardRecipe.Companion.Type.Companion.STREAM_CODEC
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.RegistryAccess
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level


class BoatyardRecipe(private val ingredients: MutableList<ItemStack> = MutableList(INV_SIZE-1) {ItemStack.EMPTY },
                     private val output: ItemStack = ItemStack.EMPTY
) : Recipe<RecipeInput> {

    override fun matches(inv: RecipeInput, level: Level): Boolean {
        if (level.isClientSide)
            return false

        ingredients.withIndex().forEach { slot ->
            if (slot.value.count > inv.getItem(slot.index).count
                || !slot.value.item.equals(inv.getItem(slot.index).item))
                return false
        }

        return true
    }

    override fun assemble(inv: RecipeInput, registries: HolderLookup.Provider): ItemStack = output.copy()

    override fun canCraftInDimensions(i: Int, j: Int): Boolean = true

    override fun isSpecial(): Boolean = true

    override fun getIngredients(): NonNullList<Ingredient> {
        val list = NonNullList.create<Ingredient>()
        list.addAll(ingredients.map { Ingredient.of(it) })
        return list
    }

    fun getIngredientsAsItemStacks(): NonNullList<ItemStack> {
        val list = NonNullList.create<ItemStack>()
        list.addAll(ingredients)
        return list
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack = output

    fun getResultItem(): ItemStack = getResultItem(RegistryAccess.EMPTY)

    override fun getSerializer(): RecipeSerializer<*> = SERIALIZER

    override fun getType(): RecipeType<*> = Type.TYPE

    companion object {

        val SERIALIZER = Serializer()

        class Serializer : RecipeSerializer<BoatyardRecipe> {
            override fun codec(): MapCodec<BoatyardRecipe> = CODEC
            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, BoatyardRecipe>? = STREAM_CODEC
        }

        class Type : RecipeType<BoatyardRecipe> {
            companion object {
                val TYPE: Type = Type()

                val CODEC: MapCodec<BoatyardRecipe> =
                    RecordCodecBuilder.mapCodec { instance: RecordCodecBuilder.Instance<BoatyardRecipe> -> instance.group(
                        ItemStack.CODEC.listOf().fieldOf("ingredients").forGetter { it.getIngredientsAsItemStacks() },
                        ItemStack.CODEC.fieldOf("output").forGetter { it.getResultItem(RegistryAccess.EMPTY) }
                    ).apply(instance) { output, ingredient ->
                        BoatyardRecipe(output, ingredient)
                    } }

                val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BoatyardRecipe>? = StreamCodec.composite(
                    ItemStack.LIST_STREAM_CODEC, BoatyardRecipe::getIngredientsAsItemStacks,
                    ItemStack.STREAM_CODEC, BoatyardRecipe::getResultItem,
                    ::BoatyardRecipe
                )
            }
        }
    }
}
