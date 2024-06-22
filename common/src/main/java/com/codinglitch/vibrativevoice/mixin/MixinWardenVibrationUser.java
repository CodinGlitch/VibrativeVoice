package com.codinglitch.vibrativevoice.mixin;

import com.codinglitch.vibrativevoice.CommonVibrativeVoice;
import com.codinglitch.vibrativevoice.VibrativeVoiceLibrary;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(targets = "net.minecraft.world.entity.monster.warden.Warden$VibrationUser")
public abstract class MixinWardenVibrationUser implements VibrationSystem.User {

    @Shadow(aliases = {"field_44600"}) @Final
    Warden this$0;

    @Inject(at = @At("HEAD"), method = "onReceiveVibration")
    private void vibrativevoice$onReceiveVibration(ServerLevel level, BlockPos pos, GameEvent event, Entity origin, Entity trueOrigin, float distance, CallbackInfo ci) {
        if (origin instanceof ServerPlayer player) {
            if (event == CommonVibrativeVoice.WEAK_VIBRATION_EVENT || event == CommonVibrativeVoice.STRONG_VIBRATION_EVENT) {
                double loudness = CommonVibrativeVoice.API.getPlayerLoudness(player);

                Optional<Double> lastLoudness = this$0.getBrain().getMemory(CommonVibrativeVoice.LOUDEST_PLAYER);
                if (lastLoudness.isPresent()) {
                    if ((loudness - lastLoudness.get()) >= VibrativeVoiceLibrary.CONFIG.warden.loudnessFactor) {
                        this$0.setTarget(player);
                    }

                    if (loudness < lastLoudness.get()) return;
                }

                this$0.getBrain().setMemoryWithExpiry(CommonVibrativeVoice.LOUDEST_PLAYER, loudness, VibrativeVoiceLibrary.CONFIG.warden.loudnessRemembrance);
            }
        }
    }
}
