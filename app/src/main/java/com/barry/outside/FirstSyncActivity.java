package com.barry.outside;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Mac on 15/11/20.
 */
public class FirstSyncActivity extends Activity {

    ProgressBar pbLoading;
    TextView tvStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first_sync);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
