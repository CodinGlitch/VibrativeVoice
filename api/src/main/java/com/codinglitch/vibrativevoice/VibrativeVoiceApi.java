package com.codinglitch.vibrativevoice;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public abstract class VibrativeVoiceApi {
    public static VibrativeVoiceApi INSTANCE;

    public enum RestrictionType {
        GROUPS,
        WHISPERING,
        SNEAKING
    }

    public enum VibrationType {
        WEAK,
        STRONG
    }

    public VibrativeVoiceApi() {
        INSTANCE = this;
    }

    /**
     * This is a way to apply a restriction based on whether or not it is enabled in the Vibrative Voice config.
     * @param restrictionType The restriction type to apply
     * @param flag The flag of the restriction
     * @return If the restriction is enabled in the config and if it can apply in the context
     */
    public abstract boolean applyRestriction(RestrictionType restrictionType, boolean flag);

    /**
     * Gets a config entry from a specified path.
     * <p>
     * To get the <b>whisperFactor</b> entry from the <b>restrictions</b> config:
     * <pre>{@code VibrativeVoiceApi.INSTANCE.getConfig("restrictions.whisperFactor");}</pre>
     * @param path The path to the config entry
     * @return An optional containing the value, if found
     */
    public abstract <T> Optional<T> getConfig(String path);

    /**
     * Sets a config entry from a specified path.
     * <p>
     * To set the <b>whisperFactor</b> entry from the <b>restrictions</b> config:
     * <pre>{@code VibrativeVoiceApi.INSTANCE.getConfig("restrictions.whisperFactor", 0.8d);}</pre>
     * @param path The path to the config entry
     * @param value The value to set the config entry to
     */
    public abstract <T> void setConfig(String path, T value);

    /**
     * Tries to send a vibration originating at the specified location, checking if it is on cooldown and using the default config cooldown duration.
     * @param uuid The UUID for the cooldown
     * @param location The location where the vibration originates from
     * @param level The level where the vibration exists
     * @param type The vibration type to send
     */
    public abstract void trySendVibration(UUID uuid, BlockPos location, Level level, VibrationType type);

    /**
     * Tries to send a vibration originating from the specified entity, checking if it is on cooldown and using the default config cooldown duration.
     * @param uuid The UUID for the cooldown
     * @param entity The Entity that the vibration originates from
     * @param level The level where the vibration exists
     * @param type The vibration type to send
     */
    public abstract void trySendVibration(UUID uuid, Entity entity, Level level, VibrationType type);

    /**
     * Tries to send a vibration originating from the specified entity, checking if it is on cooldown.
     * @param entity The Entity that the vibration originates from, using its UUID for the cooldown
     * @param level The level where the vibration exists
     * @param type The vibration type to send
     */
    public abstract void trySendVibration(Entity entity, Level level, VibrationType type);

    /**
     * Tries to send a vibration originating at the specified location, checking if it is on cooldown.
     * @param uuid The UUID for the cooldown
     * @param location The location where the vibration originates from
     * @param level The level where the vibration exists
     * @param type The vibration type to send
     * @param cooldown The cooldown duration (in ticks) to set
     */
    public abstract void trySendVibration(UUID uuid, BlockPos location, Level level, VibrationType type, int cooldown);

    /**
     * Tries to send a vibration originating from the specified entity, checking if it is on cooldown.
     * @param uuid The UUID for the cooldown
     * @param entity The Entity that the vibration originates from
     * @param level The level where the vibration exists
     * @param type The vibration type to send
     * @param cooldown The cooldown duration (in ticks) to set
     */
    public abstract void trySendVibration(UUID uuid, Entity entity, Level level, VibrationType type, int cooldown);

    /**
     * Sends a vibration at the location with the specified type.
     * @param location The location to send the vibration from
     * @param level The level to send the vibration in
     * @param vibrationType The type of vibration to send
     */
    public abstract void sendVibration(BlockPos location, Level level, VibrationType vibrationType);
    /**
     * Sends a vibration originating from an entity with the specified type.
     * @param entity The entity the vibration originates from
     * @param level The level to send the vibration in
     * @param vibrationType The type of vibration to send
     */
    public abstract void sendVibration(Entity entity, Level level, VibrationType vibrationType);

    /**
     * Gets a rough loudness value for the specified player at this current moment.
     * @param player The player to get the loudness of
     * @return The loudness of the player
     */
    public abstract double getPlayerLoudness(Player player);

    /**
     * Sets the loudness value for the specified player.
     * @param player The player to set the loudness of
     * @param loudness The loudness of the player
     * @param timestamp The timestamp this loudness was emitted
     */
    public abstract void setPlayerLoudness(Player player, double loudness, long timestamp);

    /**
     * Gets an event from a vibration type.
     * @param type The vibration type
     * @return The GameEvent corresponding to that type
     */
    public abstract GameEvent getEvent(VibrationType type);

    /**
     * Gets a qualifying VibrationType for the volume level of the data, or null if none qualifies (audio too quiet).
     * @param data The raw data that will be decoded
     * @return The qualifying VibrationType, or null if none qualified
     */
    @Nullable
    public abstract VibrationType getQualifyingType(byte[] data);

    /**
     * Gets a qualifying VibrationType for the volume level of the data, or null if none qualifies (audio too quiet).
     * @param data The raw data that will be decoded
     * @param volumeFactor The multiplier to skew volume by
     * @return The qualifying VibrationType, or null if none qualified
     */
    @Nullable
    public abstract VibrationType getQualifyingType(byte[] data, float volumeFactor);

    /**
     * Gets a qualifying VibrationType for the volume level of the data, or null if none qualifies (audio too quiet).
     * @param decoded The opus decoded data
     * @return The qualifying VibrationType, or null if none qualified
     */
    @Nullable
    public abstract VibrationType getQualifyingType(short[] decoded);

    /**
     * Gets a qualifying VibrationType for the volume level of the data, or null if none qualifies (audio too quiet).
     * @param decoded The opus decoded data
     * @param volumeFactor The multiplier to skew volume by
     * @return The qualifying VibrationType, or null if none qualified
     */
    @Nullable
    public abstract VibrationType getQualifyingType(short[] decoded, float volumeFactor);

    /**
     * Gets a qualifying VibrationType for the specified volume level, or null if none qualifies (audio too quiet).
     * @param volume The volume
     * @return The qualifying VibrationType, or null if none qualified
     */
    @Nullable
    public abstract VibrationType getQualifyingType(double volume);

    /**
     * Gets a rough volume level from the raw data
     * @param data The raw data
     * @return The approximate volume (in no particular measurement)
     */
    public abstract double getVolume(byte[] data);

    /**
     * Gets a rough volume level from the decoded data
     * @param decoded The opus decoded data
     * @return The approximate volume (in no particular measurement)
     */
    public abstract double getVolume(short[] decoded); // it will simply have to do

    //---- Cooldowns ----\\

    public abstract void setCooldown(UUID uuid, Level level, int cooldown);
    public abstract long getTimestamp(UUID uuid);

    public abstract boolean isOnCooldown(UUID uuid, Level level);
}
