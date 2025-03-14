package dev.kleinbox.partnership.main.packet

import dev.kleinbox.partnership.main.MOD_ID
import dev.kleinbox.partnership.main.block.cannon.MarineCannonBlockEntity
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.phys.Vec3


data class MarineRotC2SPacket(val pos: BlockPos, val xRot: Float, val yRot: Float) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object : ServerPlayNetworking.PlayPayloadHandler<MarineRotC2SPacket> {
        val TYPE = CustomPacketPayload.Type<MarineRotC2SPacket>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "marine_cannon_apply_rotation"))
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, MarineRotC2SPacket> = StreamCodec.composite(
            BlockPos.STREAM_CODEC, MarineRotC2SPacket::pos,
            ByteBufCodecs.FLOAT, MarineRotC2SPacket::xRot,
            ByteBufCodecs.FLOAT, MarineRotC2SPacket::yRot,
            ::MarineRotC2SPacket
        )

        override fun receive(payload: MarineRotC2SPacket, context: ServerPlayNetworking.Context) {
            context.server().execute {
                val playerPos = context.player().position()
                val blockPos = Vec3(payload.pos.center.x, payload.pos.center.y, payload.pos.center.z)

                val distance: Double = playerPos.distanceToSqr(blockPos)
                val range = context.player().getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE)

                if (distance <= range+1) {
                    val blockEntity = context.player().level().getBlockEntity(payload.pos)

                    if (blockEntity is MarineCannonBlockEntity)
                        blockEntity.setRotation(payload.xRot, payload.yRot)
                }
            }
        }
    }
}
