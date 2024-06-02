package com.codinglitch.vibrativevoice;


import net.neoforged.fml.common.Mod;

@Mod(CommonVibrativeVoice.ID)
public class VibrativeVoice {
    public VibrativeVoice() {
        CommonVibrativeVoice.initialize();
        NeoForgeLoader.load();
    }
}