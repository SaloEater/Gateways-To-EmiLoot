package com.saloeater.gateways_to_emiloot.mixin;

import com.saloeater.gateways_to_emiloot.TypeAccessor;
import dev.shadowsoffire.gateways.gate.WaveEntity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = WaveEntity.StandardWaveEntity.class, remap = false)
public abstract class StandardWaveEntityMixin implements TypeAccessor {
    @Final
    @Shadow
    protected EntityType<?> type;

    public EntityType<?> getType() {
        return type;
    }
}
