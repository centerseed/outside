package com.barry.outside;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.barry.outside.utils.PreferenceUtils;

public class SettingFragment extends Fragment {
    Switch mLocalize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLocalize = (Switch) view.findViewById(R.id.sw_auto_localize);
        mLocalize.setChecked(PreferenceUtils.getAutoLocalize(getContext()));
        mLocalize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PreferenceUtils.setAutoLocalize(getContext(), b);
            }
        });
    }
}
