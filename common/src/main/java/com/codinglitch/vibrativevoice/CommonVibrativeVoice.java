package com.codinglitch.vibrativevoice;

import com.codinglitch.lexiconfig.LexiconfigApi;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

public class CommonVibrativeVoice {
    public static VibrativeVoiceApi API = new VibrativeVoiceApiImpl();

    public static final String ID = "vibrativevoice";
    public static ResourceLocation id(String... arguments) {
        return id("", arguments);
    }
    public static ResourceLocation id(CharSequence delimiter, String... arguments) {
        return new ResourceLocation(CommonVibrativeVoice.ID, String.join(delimiter, arguments));
    }

    public static final Map<ResourceLocation, GameEvent> FREQUENCY_GAME_EVENTS = new HashMap<>();
    public static GameEvent WEAK_VIBRATION_EVENT;
    public static GameEvent STRONG_VIBRATION_EVENT;

    public static final MemoryModuleType<Double> LOUDEST_PLAYER = registerMemoryType("loudest_player", Codec.doubleRange(0, 50));

    public static <T> T loadService(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        CommonVibrativeVoice.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

    public static void registerFrequencyGameEvent(ResourceLocation location, GameEvent gameEvent) {
        FREQUENCY_GAME_EVENTS.put(location, gameEvent);
    }

    private static <U> MemoryModuleType<U> registerMemoryType(String name, Codec<U> codec) {
        return Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, id(name), new MemoryModuleType<>(Optional.of(codec)));
    }

    // -- Logging -- \\
    private static Logger LOGGER = LogManager.getLogger(ID);
    public static void info(Object object, Object... substitutions) {
        LOGGER.info(String.valueOf(object), substitutions);
    }
    public static void debug(Object object, Object... substitutions) {
        LOGGER.debug(String.valueOf(object), substitutions);
    }
    public static void warn(Object object, Object... substitutions) {
            LOGGER.warn(String.valueOf(object), substitutions);
    }
    public static void error(Object object, Object... substitutions) {
        LOGGER.error(String.valueOf(object), substitutions);
    }

    public static void makeGameEvents() {
        WEAK_VIBRATION_EVENT = new GameEvent(16);
        STRONG_VIBRATION_EVENT = new GameEvent(16);

        registerFrequencyGameEvent(id("weak_voice_vibration"), WEAK_VIBRATION_EVENT);
        registerFrequencyGameEvent(id("strong_voice_vibration"), STRONG_VIBRATION_EVENT);

        final Object2IntFunction<GameEvent> map = (Object2IntFunction<GameEvent>) VibrationSystem.VIBRATION_FREQUENCY_FOR_EVENT;
        FREQUENCY_GAME_EVENTS.forEach((location, gameEvent) -> {
            map.put(gameEvent, CONFIG.frequency);
        });
    }

    public static VibrativeVoiceConfig CONFIG;
    public static void initialize() {
        CONFIG = new VibrativeVoiceConfig();
        LexiconfigApi.register(CONFIG);
    }
}