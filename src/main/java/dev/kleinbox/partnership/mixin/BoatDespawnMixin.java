package dev.kleinbox.partnership.mixin;

import dev.kleinbox.partnership.main.entity.BoatData;
import dev.kleinbox.partnership.main.level.command.MarkChunkAsSeaport;
import dev.kleinbox.partnership.main.registries.GameRuleRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Mixin(Boat.class)
abstract public class BoatDespawnMixin extends VehicleEntity implements VariantHolder<Boat.Type> {
    public BoatDespawnMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V")
    public void partnership$defineDespawnTimer(EntityType<? extends Boat> entityType, Level level, CallbackInfo ci) {
        int timer = getDefinedDespawnTimer(level);
        if (timer < 0)
            timer = Integer.MAX_VALUE;

        BoatData data = getAttachedOrCreate(BoatData.Companion.getDATA_TYPE());
        data.setDespawn(timer);
        setAttached(BoatData.Companion.getDATA_TYPE(), data);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void partnership$shouldDespawn(CallbackInfo ci) {
        BoatData data = getAttachedOrCreate(BoatData.Companion.getDATA_TYPE());
        int despawnTimer = data.getDespawn();
        int definedDespawnTimer = getDefinedDespawnTimer(this.level());

        if (definedDespawnTimer >= 0 && !this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            int anchors = MarkChunkAsSeaport.INSTANCE.getAnchorsOfChunk(serverLevel, this.chunkPosition());

            if (anchors <= 0) {
                List<Entity> passengers = this.getPassengers();

                // Boats should despawn
                if (passengers.isEmpty()) {
                    // Countdown starts as boat is not being used anymore
                    if (despawnTimer > 0) {
                        despawnTimer--;

                        // Make sure to stay in bounds
                        if (despawnTimer > definedDespawnTimer)
                            despawnTimer = definedDespawnTimer;

                        // Update
                        data.setDespawn(despawnTimer);
                    } else if (despawnTimer == 0) {
                        // Goodbye :)
                        this.discard();
                    }
                } else if (despawnTimer >= 0) {
                    // Reset timer
                    data.setDespawn(getDefinedDespawnTimer(this.level()));
                }

                setAttached(BoatData.Companion.getDATA_TYPE(), data);
            }
        }
    }

    @Unique
    private int getDefinedDespawnTimer(Level level) {
        return level.getGameRules().getInt(GameRuleRegistries.INSTANCE.getBOAT_DESPAWN_TIMER());
    }
}
