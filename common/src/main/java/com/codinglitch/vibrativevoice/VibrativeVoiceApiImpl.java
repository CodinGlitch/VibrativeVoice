package com.codinglitch.vibrativevoice;

import com.codinglitch.lexiconfig.classes.LexiconHolding;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VibrativeVoiceApiImpl extends VibrativeVoiceApi {
    private final ConcurrentHashMap<UUID, Long> timestamps = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<UUID, Peak> loudnesses = new ConcurrentHashMap<>();

    @Override
    public boolean applyRestriction(RestrictionType restrictionType, boolean flag) {
        Object restriction = CommonVibrativeVoice.CONFIG.restrictions.getEntry(restrictionType.toString().toLowerCase());
        if (restriction == null) {
            CommonVibrativeVoice.warn("Invalid restriction type {}!", restrictionType);
            return false;
        }

        return !((boolean) restriction) || flag;
    }

    @Override
    public <T> T getConfigEntry(String path, T defaultReturn) {
        LexiconHolding currentHolder = CommonVibrativeVoice.CONFIG;
        String[] paths = path.split("\\.");
        for (String name : paths) {
            Object entry = currentHolder.getEntry(name);
            if (entry instanceof LexiconHolding holding) currentHolder = holding;
            else return (T) entry;
        }
        return defaultReturn;
    }

    @Override
    public void trySendVibration(UUID uuid, BlockPos location, Level level, VibrationType type) {
        trySendVibration(uuid, location, level, type, CommonVibrativeVoice.CONFIG.cooldownDuration);
    }
    @Override
    public void trySendVibration(UUID uuid, Entity entity, Level level, VibrationType type) {
        trySendVibration(uuid, entity, level, type, CommonVibrativeVoice.CONFIG.cooldownDuration);
    }
    @Override
    public void trySendVibration(Entity entity, Level level, VibrationType type) {
        trySendVibration(entity.getUUID(), entity, level, type, CommonVibrativeVoice.CONFIG.cooldownDuration);
    }

    @Override
    public void trySendVibration(UUID uuid, BlockPos location, Level level, VibrationType type, int cooldown) {
        if (!isOnCooldown(uuid, level)) {
            setCooldown(uuid, level, cooldown);
            sendVibration(location, level, type);
        }
    }

    @Override
    public void trySendVibration(UUID uuid, Entity entity, Level level, VibrationType type, int cooldown) {
        if (!isOnCooldown(uuid, level)) {
            setCooldown(uuid, level, cooldown);
            sendVibration(entity, level, type);
        }
    }

    @Override
    public void sendVibration(BlockPos location, Level level, VibrationType vibrationType) {
        level.getServer().execute(() -> {
            BlockState state = level.getBlockState(location);
            level.gameEvent(getEvent(vibrationType), location, GameEvent.Context.of(state));
        });
    }
    @Override
    public void sendVibration(Entity entity, Level level, VibrationType vibrationType) {
        level.getServer().execute(() -> {
            entity.gameEvent(getEvent(vibrationType));
        });
    }

    @Override
    public GameEvent getEvent(VibrationType type) {
        switch (type) {
            case WEAK -> {
                return CommonVibrativeVoice.WEAK_VIBRATION_EVENT;
            }
            case STRONG -> {
                return CommonVibrativeVoice.STRONG_VIBRATION_EVENT;
            }
        }
        return CommonVibrativeVoice.WEAK_VIBRATION_EVENT;
    }

    @Override
    public double getPlayerLoudness(Player player) {
        return loudnesses.containsKey(player.getUUID()) ? loudnesses.get(player.getUUID()).volume : 0;
    }

    @Override
    public void setPlayerLoudness(Player player, double loudness, long timestamp) {
        UUID uuid = player.getUUID();
        if (loudnesses.containsKey(uuid)) {
            Peak lastPeak = loudnesses.get(uuid);
            if (timestamp - lastPeak.timestamp < CommonVibrativeVoice.CONFIG.peakDuration) {
                if (loudness <= lastPeak.volume) return;
            }
        }

        loudnesses.put(uuid, new Peak(loudness, timestamp));
    }

    @Override
    @Nullable
    public VibrationType getQualifyingType(byte[] data) {
        return getQualifyingType(data, 1);
    }

    @Override
    @Nullable
    public VibrationType getQualifyingType(byte[] data, float volumeFactor) {
        return getQualifyingType(getVolume(data) * volumeFactor);
    }

    @Override
    @Nullable
    public VibrationType getQualifyingType(short[] decoded) {
        return getQualifyingType(decoded, 1);
    }

    @Override
    @Nullable
    public VibrationType getQualifyingType(short[] decoded, float volumeFactor) {
        return getQualifyingType(getVolume(decoded) * volumeFactor);
    }

    @Override
    @Nullable
    public VibrationType getQualifyingType(double volume) {
        if (volume > CommonVibrativeVoice.CONFIG.strongVibrationThreshold) {
            return VibrationType.STRONG;
        } else if (volume > CommonVibrativeVoice.CONFIG.weakVibrationThreshold) {
            return VibrationType.WEAK;
        }
        return null;
    }

    @Override
    public double getVolume(byte[] data) {
        OpusDecoder decoder = CommonVibrativePlugin.INSTANCE.getDecoder();
        decoder.resetState();
        return getVolume(decoder.decode(data));
    }

    @Override
    public double getVolume(short[] decoded) { // it will simply have to do
        if (decoded.length == 0) return 0;

        double average = 0;
        for (short sample : decoded) {
            average += sample;
        }
        average /= decoded.length;

        return Math.sqrt(Math.abs(average));
    }

    //---- Cooldowns ----\\

    @Override
    public void setCooldown(UUID uuid, Level level, int cooldown) {
        timestamps.put(uuid, level.getGameTime() + cooldown);
    }
    @Override
    public long getTimestamp(UUID uuid) {
        return timestamps.get(uuid);
    }

    @Override
    public boolean isOnCooldown(UUID uuid, Level level) {
        long time = level.getGameTime();
        return time < timestamps.getOrDefault(uuid, time);
    }

    public record Peak(double volume, long timestamp) { }
}
