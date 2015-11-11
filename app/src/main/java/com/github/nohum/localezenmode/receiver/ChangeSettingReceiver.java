package com.github.nohum.localezenmode.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.nohum.localezenmode.ZenModeNotificationService;
import com.github.nohum.localezenmode.util.BundleHelper;
import com.twofortyfouram.locale.sdk.client.receiver.AbstractPluginSettingReceiver;

/**
 * Entry point for Locale setting changes
 */
public class ChangeSettingReceiver extends AbstractPluginSettingReceiver {

    private static final String TAG = "ChangeSettingReceiver";

    public ChangeSettingReceiver() {
    }

    @Override
    protected boolean isBundleValid(@NonNull Bundle bundle) {
        return BundleHelper.isValid(bundle);
    }

    @Override
    protected boolean isAsync() {
        return false;
    }

    @Override
    protected void firePluginSetting(@NonNull Context context, @NonNull Bundle bundle) {
        int requestedZenMode = BundleHelper.getRequestedZenMode(bundle);
        Log.i(TAG, "received request for new interruption setting: " + requestedZenMode);

        if (!isValidMode(requestedZenMode)) {
            Log.w(TAG, "interruption setting is invalid");
            return;
        }

        Intent request = ZenModeNotificationService.requestIntent(requestedZenMode);
        LocalBroadcastManager.getInstance(context).sendBroadcast(request);
    }

    private boolean isValidMode(int mode) {
        return mode == NotificationListenerService.INTERRUPTION_FILTER_ALL
                || mode == NotificationListenerService.INTERRUPTION_FILTER_PRIORITY
                || mode == NotificationListenerService.INTERRUPTION_FILTER_NONE;
    }
}
