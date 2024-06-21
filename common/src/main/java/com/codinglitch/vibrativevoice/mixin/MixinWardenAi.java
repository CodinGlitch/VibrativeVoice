package com.codinglitch.vibrativevoice.mixin;

import com.codinglitch.vibrativevoice.CommonVibrativeVoice;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(targets = "net.minecraft.world.entity.monster.warden.WardenAi")
public abstract class MixinWardenAi {
    @Mutable
    @Shadow @Final private static List<MemoryModuleType<?>> MEMORY_TYPES;

    static {
        List<MemoryModuleType<?>> memoryModuleTypes = new ArrayList<>(MEMORY_TYPES);
        memoryModuleTypes.add(CommonVibrativeVoice.LOUDEST_PLAYER);
        MEMORY_TYPES = Collections.unmodifiableList(memoryModuleTypes);
    }
}
