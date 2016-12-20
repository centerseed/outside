package com.barry.outside;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Arrays;
import java.util.List;

public class AirInfoTypeFragmentDialog extends DialogFragment {

    public static final String ARG_AIR_TYPE = "air_type";

    String mAirInfos[];
    OnSelectedListener selectedListener;

    public interface OnSelectedListener {
        void onSelected(int position);
    }

    public static AirInfoTypeFragmentDialog getInstance(int position) {
        AirInfoTypeFragmentDialog instance = new AirInfoTypeFragmentDialog();

        Bundle args = new Bundle();
        args.putInt(ARG_AIR_TYPE, position);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.choose_airinfo_type);

        mAirInfos = new String[]{getString(R.string.pm25), getString(R.string.pm10), getString(R.string.PSI), getString(R.string.O3), getString(R.string.CO)};

        int position = getArguments().getInt(ARG_AIR_TYPE, 0);
        builder.setSingleChoiceItems(mAirInfos, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (null != selectedListener) {
                    selectedListener.onSelected(i);
                }
                dismiss();
            }
        });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public void setSelectedListener(OnSelectedListener listener) {
        selectedListener = listener;
    }
}
