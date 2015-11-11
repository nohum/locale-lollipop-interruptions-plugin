package com.github.nohum.localezenmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.nohum.localezenmode.util.BlurbHelper;
import com.github.nohum.localezenmode.util.ServiceHelper;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ZenModeActivity";

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

        ServiceHelper.showServiceEnableDialogIfNecessary(this);

        Log.i(TAG, "asking for current interruption mode");
        Intent request = new Intent(ZenModeNotificationService.ACTION_REQUEST_CURRENT_ZENMODE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(request);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
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

        LocalBroadcastManager.getInstance(this).sendBroadcast(ZenModeNotificationService.requestIntent(mode));
    }

    private class ResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive() called with action = [" + intent.getAction() + "]");

            if (ZenModeNotificationService.ACTION_RESPONSE_CURRENT_ZENMODE.equals(intent.getAction())) {
                int mode = intent.getIntExtra(ZenModeNotificationService.EXTRA_ZENMODE_SETTING, -1);

                Log.i(TAG, "got back response about interruption setting: " + mode);
                lastZenState.setText(getString(R.string.last_zen_state, BlurbHelper.modeToString(TestActivity.this, mode)));
            }
        }
    }
}
