package dev.kleinbox.partnership.main.block.boatyard

import dev.kleinbox.partnership.main.Partnership.logger
import dev.kleinbox.partnership.main.menu.BoatyardMenu
import dev.kleinbox.partnership.main.menu.inventory.ImplementedInventory
import dev.kleinbox.partnership.main.recipe.BoatyardRecipe
import dev.kleinbox.partnership.main.registries.BlockEntityRegistries.BOATYARD
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.ContainerHelper
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class BoatyardBlockEntity(val pos: BlockPos, val state: BlockState) : BlockEntity(BOATYARD, pos, state),
    MenuProvider, ImplementedInventory, CraftingContainer, WorldlyContainer {

    val isDummy: Boolean = state.getValue(BlockStateProperties.EXTENDED)
    override val inventory: NonNullList<ItemStack> = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY)
    private val recipeInput = object : RecipeInput {
        override fun getItem(index: Int): ItemStack = inventory[index]

        override fun size(): Int = inventory.size
    }

    override fun saveAdditional(compoundTag: CompoundTag, registries: HolderLookup.Provider) {
        if (isDummy) return

        ContainerHelper.saveAllItems(compoundTag, inventory, registries)
        super.saveAdditional(compoundTag, registries)
    }

    override fun loadAdditional(compoundTag: CompoundTag, registries: HolderLookup.Provider) {
        if (isDummy) return

        super.loadAdditional(compoundTag, registries)
        ContainerHelper.loadAllItems(compoundTag, inventory, registries)
    }

    override fun removeItem(slot: Int, count: Int): ItemStack {
        if (slot == INV_SIZE-1 && hasLevel()) {
            val optional = level!!.recipeManager.getRecipeFor(BoatyardRecipe.Companion.Type.TYPE, inventory as RecipeInput, level!!)

            if (optional.isPresent) {
                val recipe = optional.get().value
                recipe.getIngredientsAsItemStacks().withIndex().forEach { (index, slot) ->
                    removeItem(index, slot.count)
                }
            }
        }
        val output = super.removeItem(slot, count)
        updateRecipeOutput(false)
        return output
    }

    fun rawRemoveItem(slot: Int, count: Int) = super.removeItem(slot, count)

    override fun getSlotsForFace(direction: Direction): IntArray = IntArray(INV_SIZE-1).mapIndexed { index, _ -> index }.toIntArray()

    override fun canPlaceItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction?): Boolean = false

    override fun canTakeItemThroughFace(i: Int, itemStack: ItemStack, direction: Direction): Boolean = false

    fun updateRecipeOutput(remove: Boolean = true) {
        if (!hasLevel() || level!!.isClientSide)
            return

        val optional = level!!.recipeManager.getRecipeFor(BoatyardRecipe.Companion.Type.TYPE, recipeInput, level!!)

        if (optional.isPresent) {
            val recipe = optional.get().value
            if (recipe.matches(recipeInput, level!!)) {
                val output = recipe.assemble(recipeInput, level!!.registryAccess())
                setItem(INV_SIZE-1, output)
                return
            }
        }

        if (remove)
            setItem(INV_SIZE-1, ItemStack.EMPTY)
    }

    override fun fillStackedContents(stackedContents: StackedContents) {
        for (itemStack in this.items)
            stackedContents.accountSimpleStack(itemStack)
    }

    override fun getWidth(): Int = 4

    override fun getHeight(): Int = 3

    override fun getItems(): MutableList<ItemStack> = inventory

    override fun createMenu(syncId: Int, inventory: Inventory, player: Player): AbstractContainerMenu
            = BoatyardMenu(syncId, inventory, this)

    override fun getDisplayName(): Component = Component.translatable("block.partnership.boatyard")

    companion object {
        fun getMainBE(part: BoatyardBlockEntity): BoatyardBlockEntity? {
            if (part.isDummy) {
                val mainBE = part.level?.getBlockEntity(BoatyardBlock.getOtherSide(part.pos, part.state))
                if (mainBE is BoatyardBlockEntity && !mainBE.isDummy)
                    return mainBE
            } else return part

            logger.error("Could not get the main blockentity from the Boatyard at ${part.pos}")
            return null
        }

        const val INV_SIZE: Int = 13
    }
}