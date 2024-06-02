package com.codinglitch.vibrativevoice;

import com.codinglitch.lexiconfig.annotations.Lexicon;
import com.codinglitch.lexiconfig.annotations.LexiconEntry;
import com.codinglitch.lexiconfig.annotations.LexiconPage;
import com.codinglitch.lexiconfig.classes.LexiconData;
import com.codinglitch.lexiconfig.classes.LexiconPageData;

@Lexicon(name = CommonVibrativeVoice.ID)
public class VibrativeVoiceConfig extends LexiconData {

    @LexiconEntry(comment = "This is the frequency to use for the vibration events. Defaults to 7.")
    public Integer frequency = 7;

    @LexiconEntry(comment = "This is the threshold for when a weak vibration is transmitted (triggers warden and sculk sensors). Defaults to 6.")
    public Double weakVibrationThreshold = 6d;

    @LexiconEntry(comment = "This is the threshold for when a strong vibration is transmitted (triggers shriekers without a sensor). Defaults to 12.")
    public Double strongVibrationThreshold = 12d;

    @LexiconEntry(comment = "This is the duration (in ticks) before a vibration signal can be sent again. Defaults to 20.")
    public Integer cooldownDuration = 20;

    @LexiconEntry(comment = "This is the range (in ticks) that volume peaks are gathered. Defaults to 20.")
    public Integer peakDuration = 20;

    @LexiconPage(comment = "These are the restrictions that may influence the outcome of the vibrations resulting from voice chat.")
    public Restrictions restrictions = new Restrictions();

    public static class Restrictions extends LexiconPageData {
        @LexiconEntry(comment = "This is the range after which players can no longer be heard for frequency modulation. Defaults to 1000.")
        public Boolean groups = false;

        @LexiconEntry(comment = "If true, whispering will change the volume by the specified (whisperFactor) amount. Defaults to true.")
        public Boolean whispering = true;
        @LexiconEntry(comment = "This is the factor by how much whispering will reduce noise, with 0 completely silencing the audio and 1 being unaffected. Defaults to 0.8.")
        public Double whisperFactor = 0.8d;

        @LexiconEntry(comment = "If true, sneaking will change the volume by the specified (sneakFactor) amount. Defaults to true.")
        public Boolean sneaking = true;
        @LexiconEntry(comment = "This is the factor by how much sneaking will reduce noise, with 0 completely silencing the audio and 1 being unaffected. Defaults to 0.7.")
        public Double sneakFactor = 0.7d;
    }

    @LexiconPage(comment = "These are the configurations with the warden.")
    public Warden warden = new Warden();

    public static class Warden extends LexiconPageData {
        @LexiconEntry(comment = "This is the gap in loudness required for the warden to completely switch targets to the louder person.")
        public Double loudnessFactor = 0.8d;

        @LexiconEntry(comment = "This is how long (in ticks) the warden remembers the loudness of the loudest player.")
        public Integer loudnessRemembrance = 300;
    }
}