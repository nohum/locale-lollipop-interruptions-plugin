package com.github.nohum.localezenmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ZenModeNotificationService extends NotificationListenerService {

    private static final String TAG = "ZenModeService";

    public static final String ACTION_REQUEST_INTERRUPTION_SETTING = "com.github.nohum.localezenmode.REQUEST_INTERRUPTION_SETTING";

    public static final String ACTION_RESPONSE_INTERRUPTION_SETTING = "com.github.nohum.localezenmode.RESPONSE_INTERRUPTION_SETTING";

    public static final String EXTRA_INTERRUPTION_SETTING = "interruptionSetting";

    private CommandReceiver receiver;

    private int currentInterruptionFilter = 0;

    public ZenModeNotificationService() {
        Log.d(TAG, "constructor()");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter(ACTION_REQUEST_INTERRUPTION_SETTING);
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
        Log.d(TAG, "onBind");
        return super.onBind(intent);
    }

    @Override
    public void onListenerConnected() {
        Log.d(TAG, "onListenerConnected");

        currentInterruptionFilter = getCurrentInterruptionFilter();
    }

    @Override
    public void onInterruptionFilterChanged(int interruptionFilter) {
        Log.i(TAG, "onInterruptionFilterChanged() called with " + "interruptionFilter = [" + interruptionFilter + "]");
        currentInterruptionFilter = interruptionFilter;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "onNotificationPosted()");
    }

    private class CommandReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive() called with action = [" + intent.getAction() + "]");

            if (ACTION_REQUEST_INTERRUPTION_SETTING.equals(intent.getAction())) {
                Intent response = new Intent(ACTION_RESPONSE_INTERRUPTION_SETTING);
                response.putExtra(EXTRA_INTERRUPTION_SETTING, currentInterruptionFilter);

                Log.i(TAG, "sending back response about interruption setting");
                LocalBroadcastManager.getInstance(context).sendBroadcast(response);
            }
        }
    }
}
