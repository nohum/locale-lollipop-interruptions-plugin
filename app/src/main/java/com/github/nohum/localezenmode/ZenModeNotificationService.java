package com.github.nohum.localezenmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ZenModeNotificationService extends NotificationListenerService {

    private static final String TAG = "ZenModeService";

    public static final String ACTION_REQUEST_CURRENT_ZENMODE = "com.github.nohum.localezenmode.REQUEST_CURRENT_ZENMODE";

    public static final String ACTION_RESPONSE_CURRENT_ZENMODE = "com.github.nohum.localezenmode.RESPONSE_CURRENT_ZENMODE";

    public static final String ACTION_REQUEST_SET_ZENMODE = "com.github.nohum.localezenmode.REQUEST_SET_ZENMODE";

    public static final String EXTRA_ZENMODE_SETTING = "zenModeSetting";

    private CommandReceiver receiver;

    private int currentZenMode = 0;

    public ZenModeNotificationService() {
        // pass
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REQUEST_CURRENT_ZENMODE);
        filter.addAction(ACTION_REQUEST_SET_ZENMODE);
        receiver = new CommandReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onListenerConnected() {
        Log.d(TAG, "onListenerConnected");

        currentZenMode = getCurrentInterruptionFilter();
    }

    @Override
    public void onInterruptionFilterChanged(int interruptionFilter) {
        Log.i(TAG, "onInterruptionFilterChanged() called with " + "interruptionFilter = [" + interruptionFilter + "]");

        currentZenMode = interruptionFilter;
        reportZenModeState(this);
    }

    private class CommandReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive() called with action = [" + intent.getAction() + "]");

            if (ACTION_REQUEST_CURRENT_ZENMODE.equals(intent.getAction())) {
                reportZenModeState(context);
            }

            if (ACTION_REQUEST_SET_ZENMODE.equals(intent.getAction())) {
                int newMode = intent.getIntExtra(EXTRA_ZENMODE_SETTING, 0);

                Log.i(TAG, "setting new interruption setting: " + newMode);
                if (newMode > 0) {
                    requestInterruptionFilter(newMode);
                }
            }
        }
    }

    private void reportZenModeState(Context context) {
        Intent response = new Intent(ACTION_RESPONSE_CURRENT_ZENMODE);
        response.putExtra(EXTRA_ZENMODE_SETTING, currentZenMode);

        Log.i(TAG, "sending back response about interruption setting: " + currentZenMode);
        LocalBroadcastManager.getInstance(context).sendBroadcast(response);
    }
}
