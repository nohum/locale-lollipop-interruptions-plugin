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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ZenModeActivity";

    /**
     * Also to be found in {@link android.provider.Settings.Secure}
     */
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private ResultReceiver receiver;

    private TextView lastZenState;

    private Button btnZenAll;

    private Button btnZenPriority;

    private Button btnZenNone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        lastZenState = (TextView) findViewById(R.id.last_zen_state);
        btnZenAll = (Button) findViewById(R.id.btn_zen_all);
        btnZenPriority = (Button) findViewById(R.id.btn_zen_priority);
        btnZenNone = (Button) findViewById(R.id.btn_zen_none);

        btnZenAll.setOnClickListener(this);
        btnZenPriority.setOnClickListener(this);
        btnZenNone.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(ZenModeNotificationService.ACTION_RESPONSE_CURRENT_ZENMODE);
        receiver = new ResultReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        if (!isServiceEnabled()) {
            showServiceEnableDialog();
        } else {
            Log.i(TAG, "asking for current interruption mode");

            Intent request = new Intent(ZenModeNotificationService.ACTION_REQUEST_CURRENT_ZENMODE);
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

    @Override
    public void onClick(View v) {
        if (v.equals(btnZenAll)) {
            requestZenMode(ZenModeNotificationService.INTERRUPTION_FILTER_ALL);
        } else if (v.equals(btnZenPriority)) {
            requestZenMode(ZenModeNotificationService.INTERRUPTION_FILTER_PRIORITY);
        } else if (v.equals(btnZenNone)) {
            requestZenMode(ZenModeNotificationService.INTERRUPTION_FILTER_NONE);
        }
    }

    private void requestZenMode(int mode) {
        Log.i(TAG, "request interruption mode update to: " + mode);

        Intent request = new Intent(ZenModeNotificationService.ACTION_REQUEST_SET_ZENMODE);
        request.putExtra(ZenModeNotificationService.EXTRA_ZENMODE_SETTING, mode);

        LocalBroadcastManager.getInstance(this).sendBroadcast(request);
    }

    private class ResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive() called with action = [" + intent.getAction() + "]");

            if (ZenModeNotificationService.ACTION_RESPONSE_CURRENT_ZENMODE.equals(intent.getAction())) {
                int mode = intent.getIntExtra(ZenModeNotificationService.EXTRA_ZENMODE_SETTING, -1);

                Log.i(TAG, "got back response about interruption setting: " + mode);
                lastZenState.setText(getString(R.string.last_zen_state, modeToString(mode)));
            }
        }
    }

    private String modeToString(int mode) {
        switch (mode) {
            case ZenModeNotificationService.INTERRUPTION_FILTER_ALL: return getString(R.string.current_state_all);
            case ZenModeNotificationService.INTERRUPTION_FILTER_PRIORITY: return getString(R.string.current_state_priority);
            case ZenModeNotificationService.INTERRUPTION_FILTER_NONE: return getString(R.string.current_state_none);
        }

        return getString(R.string.current_state_unknown);
    }
}
