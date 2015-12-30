package com.barry.outside;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


import com.barry.outside.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mac on 15/11/18.
 */
public class chooseCountyFragmentDialog extends DialogFragment {

    public static final String ARG_CURR_LOCATION = "_arg_curr_location";
    OnSelectedListener selectedListener;

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.choose_country);

        List<String> countrys = Arrays.asList(getContext().getResources().getStringArray(R.array.country));
        int position = countrys.indexOf(getArgCurrLocation());

        builder.setSingleChoiceItems(R.array.country, position, null);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                int position =  ((AlertDialog)getDialog()).getListView().getCheckedItemPosition();
                if (position == -1) {
                    dismiss();
                    return;
                }

                String location = (String) ((AlertDialog)getDialog()).getListView().getAdapter().getItem(position);
                if (null != selectedListener) {
                    selectedListener.onSelected(location);
                }
                dismiss();
            }
        });

        return builder.create();
    }

    public interface OnSelectedListener {
        void onSelected(String location);
    }

    public void setSelectedListener(OnSelectedListener listener) {
        selectedListener = listener;
    }

    private String getArgCurrLocation() {
        return getArguments().getString(ARG_CURR_LOCATION);
    }
}
