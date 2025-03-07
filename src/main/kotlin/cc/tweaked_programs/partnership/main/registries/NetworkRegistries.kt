package cc.tweaked_programs.partnership.main.registries

import cc.tweaked_programs.partnership.main.packet.MarineRotC2SPacket
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object NetworkRegistries {

    init {
        PayloadTypeRegistry.playC2S().register(MarineRotC2SPacket.TYPE, MarineRotC2SPacket.CODEC)
        ServerPlayNetworking.registerGlobalReceiver(MarineRotC2SPacket.TYPE, MarineRotC2SPacket)
    }
}