package com.codinglitch.vibrativevoice.platform;

import com.codinglitch.vibrativevoice.CommonVibrativeVoice;
import com.codinglitch.vibrativevoice.platform.services.PlatformHelper;
import com.mojang.serialization.Codec;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.nio.file.Path;
import java.util.Optional;

public class FabricPlatformHelper implements PlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public <U> MemoryModuleType<U> registerMemoryType(String name, Codec<U> codec) {
        return Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, CommonVibrativeVoice.id(name), new MemoryModuleType<>(Optional.of(codec)));
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
