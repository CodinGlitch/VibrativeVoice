package com.codinglitch.vibrativevoice;

import net.fabricmc.api.ModInitializer;

public class VibrativeVoice implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonVibrativeVoice.initialize();

        FabricLoader.load();
    }
}
