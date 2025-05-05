package com.codinglitch.vibrativevoice;

import com.codinglitch.lexiconfig.classes.LexiconEntryData;
import com.codinglitch.lexiconfig.classes.LexiconSubstrate;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VibrativeVoiceApiImpl extends VibrativeVoiceApi {
    private final ConcurrentHashMap<UUID, Long> timestamps = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<UUID, Peak> loudnesses = new ConcurrentHashMap<>();

    @Override
    public boolean applyRestriction(RestrictionType restrictionType, boolean flag) {
        Boolean restriction = VibrativeVoiceLibrary.CONFIG.restrictions.<Boolean>getEntry(restrictionType.toString().toLowerCase()).orElse(null);
        if (restriction == null) {
            CommonVibrativeVoice.warn("Invalid restriction type {}!", restrictionType);
            return false;
        }

        return !((boolean) restriction) || flag;
    }

    @Override
    public <T> Optional<T> getConfig(String path) {
        LexiconSubstrate currentSubstrate = VibrativeVoiceLibrary.CONFIG;
        String[] paths = path.split("\\.");
        for (String name : paths) {
            Object entry = currentSubstrate.getEntry(name).orElse(null);
            if (entry instanceof LexiconSubstrate substrate) {
                currentSubstrate = substrate;
            } else {
                return Optional.of((T) entry);
            }
        }
        return Optional.empty();
    }

    @Override
    public <T> void setConfig(String path, T value) {
        LexiconSubstrate currentSubstrate = VibrativeVoiceLibrary.CONFIG;
        String[] paths = path.split("\\.");
        for (int i = 0; i < paths.length; i++) {
            String name = paths[i];
            if (i == paths.length-1) {
                LexiconEntryData<T> entryData = (LexiconEntryData<T>) currentSubstrate.getContents(entry -> entry.getName().equals(name)).stream().findFirst().orElse(null);
                if (entryData != null) entryData.set(value);
            } else {
                Object entry = currentSubstrate.getEntry(name).orElse(null);
                if (entry instanceof LexiconSubstrate substrate) currentSubstrate = substrate;
            }
        }
    }

    @Override
    public void trySendVibration(UUID uuid, BlockPos location, Level level, VibrationType type) {
        trySendVibration(uuid, location, level, type, VibrativeVoiceLibrary.CONFIG.cooldownDuration);
    }
    @Override
    public void trySendVibration(UUID uuid, Entity entity, Level level, VibrationType type) {
        trySendVibration(uuid, entity, level, type, VibrativeVoiceLibrary.CONFIG.cooldownDuration);
    }
    @Override
    public void trySendVibration(Entity entity, Level level, VibrationType type) {
        trySendVibration(entity.getUUID(), entity, level, type, VibrativeVoiceLibrary.CONFIG.cooldownDuration);
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
            if (timestamp - lastPeak.timestamp < VibrativeVoiceLibrary.CONFIG.peakDuration) {
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
        if (volume > VibrativeVoiceLibrary.CONFIG.strongVibrationThreshold) {
            return VibrationType.STRONG;
        } else if (volume > VibrativeVoiceLibrary.CONFIG.weakVibrationThreshold) {
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

        float volume = 0;
        for (short sample : decoded) {
            volume += sample*sample;
        }

        return Math.sqrt(volume / decoded.length);
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
