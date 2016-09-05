package com.barry.outside;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Arrays;
import java.util.List;

public class chooseCountyFragmentDialog extends DialogFragment {

    public static final String ARG_CURR_LOCATION = "_arg_curr_location";
    OnSelectedListener selectedListener;

    public interface OnSelectedListener {
        void onSelected(String location);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.choose_country);

        List<String> countrys = Arrays.asList(getContext().getResources().getStringArray(R.array.country));

        builder.setSingleChoiceItems(R.array.country, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (null != selectedListener) {
                    String location = (String) ((AlertDialog) getDialog()).getListView().getAdapter().getItem(i);
                    selectedListener.onSelected(location);
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
