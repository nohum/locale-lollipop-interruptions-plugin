package com.github.nohum.localezenmode.util;

import android.content.Context;

import com.github.nohum.localezenmode.R;
import com.github.nohum.localezenmode.ZenModeNotificationService;

public class BlurbHelper {

    public static String modeToString(Context context, int mode) {
        switch (mode) {
            case ZenModeNotificationService.INTERRUPTION_FILTER_ALL: return context.getString(R.string.current_state_all);
            case ZenModeNotificationService.INTERRUPTION_FILTER_PRIORITY: return context.getString(R.string.current_state_priority);
            case ZenModeNotificationService.INTERRUPTION_FILTER_NONE: return context.getString(R.string.current_state_none);
        }

        return context.getString(R.string.current_state_unknown);
    }
}
