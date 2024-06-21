package com.codinglitch.vibrativevoice;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import static com.codinglitch.vibrativevoice.CommonVibrativeVoice.API;
import static com.codinglitch.vibrativevoice.VibrativeVoiceLibrary.CONFIG;

public class CommonVibrativePlugin {
    public static VoicechatApi api;
    @Nullable
    public static VoicechatServerApi serverApi;

    public static CommonVibrativePlugin INSTANCE;

    private OpusDecoder decoder;

    public String getPluginId() {
        return CommonVibrativeVoice.ID;
    }

    public void initialize(VoicechatApi api) {
        CommonVibrativePlugin.api = api;

        INSTANCE = this;
    }

    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicPacket);
    }

    private void onServerStarted(VoicechatServerStartedEvent event) {
        serverApi = event.getVoicechat();
    }

    private void onMicPacket(MicrophonePacketEvent event) {
        VoicechatConnection senderConnection = event.getSenderConnection();
        if (senderConnection == null) return;

        float multiplier = 1;

        if (!API.applyRestriction(VibrativeVoiceApi.RestrictionType.GROUPS, senderConnection.isInGroup())) return;
        if (!API.applyRestriction(VibrativeVoiceApi.RestrictionType.WHISPERING, event.getPacket().isWhispering())) {
            multiplier *= CONFIG.restrictions.whisperFactor;
        }

        byte[] data = event.getPacket().getOpusEncodedData();
        if (data.length == 0) return;

        if (senderConnection.getPlayer().getPlayer() instanceof ServerPlayer player) {
            if (!API.applyRestriction(VibrativeVoiceApi.RestrictionType.SNEAKING, player.isCrouching())) {
                multiplier *= CONFIG.restrictions.whisperFactor;
            }
            if (multiplier <= 0) return;

            double volume = API.getVolume(data) * multiplier;
            API.setPlayerLoudness(player, volume, player.level().getGameTime());
            VibrativeVoiceApi.VibrationType type = API.getQualifyingType(volume);
            if (type == null) return;

            API.trySendVibration(player.getUUID(), player, player.level(), type, VibrativeVoiceLibrary.CONFIG.cooldownDuration);
        } else {
            //
        }
    }

    public OpusDecoder getDecoder() {
        if (decoder == null) decoder = api.createDecoder();
        return decoder;
    }
}
