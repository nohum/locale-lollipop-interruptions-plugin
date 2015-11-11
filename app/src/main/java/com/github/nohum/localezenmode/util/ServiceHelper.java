package com.github.nohum.localezenmode.util;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import com.github.nohum.localezenmode.R;

public class ServiceHelper {

    /**
     * Also to be found in {@link android.provider.Settings.Secure}
     */
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private ServiceHelper() {
    }

    public static void showServiceEnableDialogIfNecessary(final Context context) {
        if (isServiceEnabled(context)) {
            return;
        }

        new AlertDialog.Builder(context)
                .setMessage(R.string.notification_access_message)
                .setTitle(R.string.notification_access_title)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
                                context.startActivity(intent);
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    public static boolean isServiceEnabled(Context context) {
        final String flatList = Settings.Secure.getString(context.getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);

        if (TextUtils.isEmpty(flatList)) {
            return false;
        }

        String pkgName = context.getPackageName();
        final String[] names = flatList.split(":");

        for (String name : names) {
            final ComponentName componentName = ComponentName.unflattenFromString(name);

            if (pkgName.equals(componentName.getPackageName())) {
                return true;
            }
        }

        return false;
    }

}
