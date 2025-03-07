package dev.kleinbox.partnership.main.block.cannon

import dev.kleinbox.partnership.main.entity.CannonballEntity
import dev.kleinbox.partnership.main.registries.BlockEntityRegistries
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.util.Mth
import net.minecraft.util.Mth.clamp
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import org.joml.Math.toRadians

class MarineCannonBlockEntity(pos: BlockPos, val state: BlockState) : BlockEntity(BlockEntityRegistries.MARINE_CANNON, pos, state) {

    var xRot: Float = 0F
    var yRot: Float = 0F

    var balls: Int = 0

    override fun saveAdditional(compoundTag: CompoundTag, registries: HolderLookup.Provider) {
        compoundTag.putFloat("xRot", xRot)
        compoundTag.putFloat("yRot", yRot)

        compoundTag.putInt("balls", balls)

        super.saveAdditional(compoundTag, registries)
    }

    override fun loadAdditional(compoundTag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(compoundTag, registries)

        xRot = compoundTag.getFloat("xRot")
        yRot = compoundTag.getFloat("yRot")

        balls = compoundTag.getInt("balls")
    }

    @Environment(EnvType.CLIENT)
    fun rotate(x: Float, y: Float) {
        val (newXRot, newYRot) = clampRot(xRot + x, yRot + y)
        xRot = newXRot
        yRot = newYRot
    }

    fun setRotation(x: Float, y: Float) {
        val (newXRot, newYRot) = clampRot(x, y)
        xRot = newXRot
        yRot = newYRot
        setChanged()
    }

    private fun clampRot(xRot: Float, yRot: Float): Pair<Float, Float> {
        return Pair(
            clamp(xRot, -75F, 75F),
            clamp(yRot, -45F, 45F)
        )
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag? {
        return saveWithoutMetadata(registries)
    }

    override fun setChanged() {
        super.setChanged()
        if (hasLevel() && !level!!.isClientSide)
            level!!.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL)
    }

    fun shoot() {
        if (!hasLevel()) return

        val newXRot = xRot + when (blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction.NORTH -> 180F
            Direction.EAST -> 270F
            Direction.SOUTH -> 0F
            Direction.WEST -> 90F
            else -> 0F
        }

        val x = -Mth.sin(toRadians(newXRot)) * Mth.cos(toRadians(yRot)).toDouble()
        val y = -Mth.sin(toRadians(yRot)).toDouble()
        val z = Mth.cos(toRadians(newXRot)) * Mth.cos(toRadians(yRot)).toDouble()

        val spawn = blockPos.center.add(Vec3(x, y, z))

        val entity = CannonballEntity(level!!, spawn.x, spawn.y, spawn.z)
        val actualPower = 3 * 0.5
        entity.setDeltaMovement(x * actualPower, y * actualPower, z * actualPower)
        level!!.addFreshEntity(entity)

        level!!.addParticle(ParticleTypes.SMOKE, blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())
    }

    companion object {
        const val MAX_LOAD: Int = 16
    }
}
