package com.github.nohum.localezenmode;

import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.github.nohum.localezenmode.util.BlurbHelper;
import com.github.nohum.localezenmode.util.BundleHelper;
import com.github.nohum.localezenmode.util.ServiceHelper;
import com.twofortyfouram.locale.sdk.client.ui.activity.AbstractLocalePluginActivity;

/**
 * Entry point activity for Locale plugins
 */
public class EditSettingActivity extends AbstractLocalePluginActivity
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = "EditSettingActivity";

    private static final String STATE_SELECTED_SETTING = "selected_setting";

    private int selectedSetting = 0;

    private RadioButton modeAll;

    private RadioButton modePriority;

    private RadioButton modeNone;

    private Button btnFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_edit);

        modeAll = (RadioButton) findViewById(R.id.mode_all);
        modePriority = (RadioButton) findViewById(R.id.mode_priority);
        modeNone = (RadioButton) findViewById(R.id.mode_none);
        btnFinished = (Button) findViewById(R.id.btn_finished);

        modeAll.setOnCheckedChangeListener(this);
        modePriority.setOnCheckedChangeListener(this);
        modeNone.setOnCheckedChangeListener(this);
        btnFinished.setOnClickListener(this);
    }

    private void updateUi() {
        switch (selectedSetting) {
            case NotificationListenerService.INTERRUPTION_FILTER_PRIORITY:
                modePriority.setChecked(true);
                break;

            case NotificationListenerService.INTERRUPTION_FILTER_NONE:
                modeNone.setChecked(true);
                break;

            case NotificationListenerService.INTERRUPTION_FILTER_ALL:
                modeAll.setChecked(true);
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        selectedSetting = savedInstanceState.getInt(STATE_SELECTED_SETTING, 0);
        updateUi();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_SETTING, selectedSetting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ServiceHelper.showServiceEnableDialogIfNecessary(this);
    }

    @Override
    public boolean isBundleValid(@NonNull Bundle bundle) {
        return BundleHelper.isValid(bundle);
    }

    @Override
    public void onPostCreateWithPreviousResult(@NonNull Bundle bundle, @NonNull String previousBlurb) {
        selectedSetting = BundleHelper.getRequestedZenMode(bundle);
        updateUi();
    }

    @Nullable
    @Override
    public Bundle getResultBundle() {
        // 0 = unknown
        return selectedSetting > 0 ? BundleHelper.create(selectedSetting) : null;
    }

    @NonNull
    @Override
    public String getResultBlurb(@NonNull Bundle bundle) {
        int mode = BundleHelper.getRequestedZenMode(bundle);
        String blurb = getString(R.string.locale_blurb_update_zen_mode, BlurbHelper.modeToString(this, mode));

        Log.i(TAG, "result blurb = " + blurb);
        return blurb;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            return;
        }

        if (buttonView.equals(modeAll)) {
            selectedSetting = NotificationListenerService.INTERRUPTION_FILTER_ALL;
        } else if (buttonView.equals(modePriority)) {
            selectedSetting = NotificationListenerService.INTERRUPTION_FILTER_PRIORITY;
        } else if (buttonView.equals(modeNone)) {
            selectedSetting = NotificationListenerService.INTERRUPTION_FILTER_NONE;
        }

        Log.i(TAG, "selected interruptions mode set to = " + selectedSetting);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnFinished)) {
            finish();
        }
    }
}
