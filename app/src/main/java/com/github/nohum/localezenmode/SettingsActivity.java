package com.github.nohum.localezenmode;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "ZenModeActivity";

    /**
     * Also to be found in {@link android.provider.Settings.Secure}
     */
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private ResultReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(ZenModeNotificationService.ACTION_RESPONSE_INTERRUPTION_SETTING);
        receiver = new ResultReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        if (!isServiceEnabled()) {
            showServiceEnableDialog();
        } else {
            Log.i(TAG, "asking for current interruption mode");

            Intent request = new Intent(ZenModeNotificationService.ACTION_REQUEST_INTERRUPTION_SETTING);
            LocalBroadcastManager.getInstance(this).sendBroadcast(request);
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        super.onPause();
    }

    private void showServiceEnableDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.notification_access_message)
                .setTitle(R.string.notification_access_title)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // pass
                            }
                        })
                .show();
    }

    private boolean isServiceEnabled() {
        final String flatList = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);

        if (TextUtils.isEmpty(flatList)) {
            return false;
        }

        String pkgName = getPackageName();
        final String[] names = flatList.split(":");

        for (String name : names) {
            final ComponentName componentName = ComponentName.unflattenFromString(name);

            if (pkgName.equals(componentName.getPackageName())) {
                return true;
            }
        }

        return false;
    }

    private class ResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive() called with action = [" + intent.getAction() + "]");

            if (ZenModeNotificationService.ACTION_RESPONSE_INTERRUPTION_SETTING.equals(intent.getAction())) {
                int mode = intent.getIntExtra(ZenModeNotificationService.EXTRA_INTERRUPTION_SETTING, -1);

                Log.i(TAG, "got back response about interruption setting: " + mode);
            }
        }
    }
}
