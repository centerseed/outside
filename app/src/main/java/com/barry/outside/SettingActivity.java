package com.barry.outside;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.barry.outside.utils.PreferenceUtils;

/**
 * Created by Mac on 15/11/18.
 */
public class SettingActivity extends ToolbarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initToolbar(R.mipmap.ic_back);

        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUI() {
        Switch swLocalized = (Switch) findViewById(R.id.sw_auto_localize);
        swLocalized.setChecked(PreferenceUtils.getAutoLocalize(getApplicationContext()));

        swLocalized.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceUtils.setAutoLocalize(getApplicationContext(), b);
            }
        });
    }
}
