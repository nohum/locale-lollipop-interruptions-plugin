package com.github.nohum.localezenmode.util;

import android.os.Bundle;

public class BundleHelper {

    private static final String EXTRA_ZEN_MODE = "zen_mode";

    private static final int ZEN_MODE_UNKNOWN = 0;

    private BundleHelper() {
    }

    public static boolean isValid(Bundle bundle) {
        return bundle.containsKey(EXTRA_ZEN_MODE) && bundle.getInt(EXTRA_ZEN_MODE, ZEN_MODE_UNKNOWN) != ZEN_MODE_UNKNOWN;
    }

    public static int getRequestedZenMode(Bundle bundle) {
        return bundle.getInt(EXTRA_ZEN_MODE, ZEN_MODE_UNKNOWN);
    }

    public static Bundle create(int selectedZenMode) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ZEN_MODE, selectedZenMode);

        return bundle;
    }
}
