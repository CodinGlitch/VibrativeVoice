package com.codinglitch.vibrativevoice.platform;

import com.codinglitch.vibrativevoice.CommonVibrativeVoice;
import com.codinglitch.vibrativevoice.platform.services.PlatformHelper;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class ForgePlatformHelper implements PlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    public static final Map<String, MemoryModuleType<?>> MEMORY_MODULE_TYPES = new HashMap<>();
    @Override
    public <U> MemoryModuleType<U> registerMemoryType(String name, Codec<U> codec) {
        MemoryModuleType<U> moduleType = new MemoryModuleType<>(Optional.of(codec));
        MEMORY_MODULE_TYPES.put(name, moduleType);
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