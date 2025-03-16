package dev.kleinbox.partnership.main.entity

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.kleinbox.partnership.main.MOD_ID
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.minecraft.resources.ResourceLocation

@Suppress("UnstableApiUsage")
data class BoatData(var despawn: Int) {
    companion object {
        val CODEC: Codec<BoatData> = RecordCodecBuilder.create {
            it.group(
                Codec.INT.fieldOf("despawn").forGetter(BoatData::despawn),
            ).apply(it, ::BoatData)
        }

        val BOAT_DATA_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "boat_despawn_timer")

        val DATA_TYPE = AttachmentRegistry.create(BOAT_DATA_ID) { builder ->
            builder.copyOnDeath()
                .initializer { BoatData(-1) }
                .persistent(CODEC)
        }
    }
}