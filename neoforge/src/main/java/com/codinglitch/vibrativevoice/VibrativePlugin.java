package com.codinglitch.vibrativevoice;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;

@ForgeVoicechatPlugin
public class VibrativePlugin implements VoicechatPlugin {
    private CommonVibrativePlugin common;

    public VibrativePlugin() {
        common = new CommonVibrativePlugin();
    }

    @Override
    public String getPluginId() {
        return common.getPluginId();
    }

    @Override
    public void initialize(VoicechatApi api) {
        common.initialize(api);
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        common.registerEvents(registration);
    }
}
