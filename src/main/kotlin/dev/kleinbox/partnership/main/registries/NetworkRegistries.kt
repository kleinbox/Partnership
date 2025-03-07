package dev.kleinbox.partnership.main.registries

import dev.kleinbox.partnership.main.packet.MarineRotC2SPacket
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object NetworkRegistries {

    init {
        PayloadTypeRegistry.playC2S().register(MarineRotC2SPacket.TYPE, MarineRotC2SPacket.CODEC)
        ServerPlayNetworking.registerGlobalReceiver(MarineRotC2SPacket.TYPE, MarineRotC2SPacket)
    }
}