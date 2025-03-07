package cc.tweaked_programs.partnership.main.packet;

import cc.tweaked_programs.partnership.main.MOD_ID
import cc.tweaked_programs.partnership.main.block.cannon.MarineCannonBlockEntity
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

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
                val blockEntity = context.player().level().getBlockEntity(payload.pos)
                if (blockEntity is MarineCannonBlockEntity)
                    blockEntity.setRotation(payload.xRot, payload.yRot)
            }
        }
    }
}
