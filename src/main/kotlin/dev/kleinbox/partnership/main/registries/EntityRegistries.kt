package dev.kleinbox.partnership.main.registries

import dev.kleinbox.partnership.main.MOD_ID
import dev.kleinbox.partnership.main.entity.CannonballEntity
import dev.kleinbox.partnership.main.entity.Kayak
import dev.kleinbox.partnership.main.entity.Lifebuoy
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory

object EntityRegistries {
    private fun <E: Entity, T: EntityType<E>> create(name: String, type: T): T {
        return Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, name),
            type
        )
    }

    val KAYAK: EntityType<Kayak> = create(
        name = "kayak",
        type = EntityType.Builder.of(::Kayak, MobCategory.MISC)
            .sized(1.6F, 0.6F)
            .build()
    )

    val LIFEBUOY: EntityType<Lifebuoy> = create(
        name = "lifebuoy",
        type = EntityType.Builder.of(::Lifebuoy, MobCategory.MISC)
            .sized(1.5F, 0.5F)
            .build()
    )

    val CANNONBALL: EntityType<CannonballEntity> = create(
        name = "cannonball",
        type = EntityType.Builder.of(::CannonballEntity, MobCategory.MISC)
            .sized(0.25f, 0.25f)
            .clientTrackingRange(32)
            .updateInterval(10)
            .build()
    )
}