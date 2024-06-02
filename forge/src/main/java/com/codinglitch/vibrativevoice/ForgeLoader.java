package com.codinglitch.vibrativevoice;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = CommonVibrativeVoice.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeLoader {
    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.GAME_EVENT, helper -> {
            CommonVibrativeVoice.makeGameEvents();
            CommonVibrativeVoice.FREQUENCY_GAME_EVENTS.forEach(helper::register);
        });
    }

    public static void load() {

    }
}
