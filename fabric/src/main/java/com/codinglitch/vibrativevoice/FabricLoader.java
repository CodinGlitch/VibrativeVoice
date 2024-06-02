package com.codinglitch.vibrativevoice;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FabricLoader {
    public static void load() {
        CommonVibrativeVoice.makeGameEvents();
        CommonVibrativeVoice.FREQUENCY_GAME_EVENTS.forEach((location, gameEvent) -> {
            Registry.register(BuiltInRegistries.GAME_EVENT, location, gameEvent);
        });
    }
}
