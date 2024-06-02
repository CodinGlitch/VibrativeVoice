package com.codinglitch.vibrativevoice.platform;

import com.codinglitch.vibrativevoice.CommonVibrativeVoice;
import com.codinglitch.vibrativevoice.platform.services.PlatformHelper;

public class Services {
    public static final PlatformHelper PLATFORM = CommonVibrativeVoice.loadService(PlatformHelper.class);
}