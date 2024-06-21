package com.codinglitch.vibrativevoice;

import com.codinglitch.lexiconfig.LexiconfigApi;
import com.codinglitch.lexiconfig.Library;
import com.codinglitch.lexiconfig.annotations.LexiconLibrary;

@LexiconLibrary
public class VibrativeVoiceLibrary extends Library {
    public static VibrativeVoiceConfig CONFIG = new VibrativeVoiceConfig();

    @Override
    public void shelveLexicons() {
        LexiconfigApi.shelveLexicon(CONFIG);
    }
}
