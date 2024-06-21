package com.codinglitch.vibrativevoice;

import com.codinglitch.vibrativevoice.platform.ForgePlatformHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = CommonVibrativeVoice.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeLoader {
    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.GAME_EVENT, helper -> {
            CommonVibrativeVoice.makeGameEvents();
            CommonVibrativeVoice.FREQUENCY_GAME_EVENTS.forEach(helper::register);
        });

        event.register(Registries.MEMORY_MODULE_TYPE, helper -> ForgePlatformHelper.MEMORY_MODULE_TYPES.forEach(helper::register));
    }

    public static void load() {

    }
}
