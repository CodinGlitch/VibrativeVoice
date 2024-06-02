package com.codinglitch.vibrativevoice;

import net.minecraftforge.fml.common.Mod;

@Mod(CommonVibrativeVoice.ID)
public class VibrativeVoice {
    public VibrativeVoice() {
        CommonVibrativeVoice.initialize();
        ForgeLoader.load();
    }
}