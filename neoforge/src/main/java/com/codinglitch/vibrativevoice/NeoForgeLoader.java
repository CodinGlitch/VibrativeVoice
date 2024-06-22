package com.codinglitch.vibrativevoice;

import com.codinglitch.vibrativevoice.platform.NeoForgePlatformHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = CommonVibrativeVoice.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NeoForgeLoader {
    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.GAME_EVENT, helper -> {
            CommonVibrativeVoice.makeGameEvents();
            CommonVibrativeVoice.FREQUENCY_GAME_EVENTS.forEach(helper::register);
        });

        event.register(Registries.MEMORY_MODULE_TYPE, helper -> NeoForgePlatformHelper.MEMORY_MODULE_TYPES.forEach(helper::register));
    }

    public static void load() {

    }
}
