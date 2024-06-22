package com.codinglitch.vibrativevoice.platform;

import com.codinglitch.vibrativevoice.CommonVibrativeVoice;
import com.codinglitch.vibrativevoice.platform.services.PlatformHelper;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NeoForgePlatformHelper implements PlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    public static final Map<ResourceLocation, MemoryModuleType<?>> MEMORY_MODULE_TYPES = new HashMap<>();
    @Override
    public <U> MemoryModuleType<U> registerMemoryType(String name, Codec<U> codec) {
        MemoryModuleType<U> moduleType = new MemoryModuleType<>(Optional.of(codec));
        MEMORY_MODULE_TYPES.put(CommonVibrativeVoice.id(name), moduleType);
        return moduleType;
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }
}