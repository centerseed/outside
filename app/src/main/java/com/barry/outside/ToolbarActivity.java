package com.barry.outside;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.barry.outside.syncadapter.WeatherSyncAdapter;

public abstract class ToolbarActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private static final String TAG = "ToolbarActivity";
    private Toolbar m_toolbar;

    public void initToolbar(int naviId) {
        m_toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(m_toolbar);
        m_toolbar.setTitle(R.string.app_name);
        m_toolbar.setOnMenuItemClickListener(this);
        m_toolbar.setNavigationIcon(naviId);
    }

    public void setToolbarTitle(final int rid) {
        getToolbar().post(new Runnable() {
            @Override
            public void run() {
                getToolbar().setTitle(rid);
            }
        });
    }


    public void setToolbarLogo(Drawable d) {
        getToolbar().setLogo(d);
    }

    public Toolbar getToolbar() {
        return m_toolbar;
    }

    public void enableToolbarNavigationBack() {
        getToolbar().setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        for (Fragment f : getSupportFragmentManager().getFragments())
            if (f != null && f.onOptionsItemSelected(item))
                return true;

        if (onOptionsItemSelected(item))
            return true;

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WeatherSyncAdapter.BROADCAST_SYNC_ERROR);
        registerReceiver(m_receiverSync, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(m_receiverSync);
    }

    private BroadcastReceiver m_receiverSync = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive " + intent.getExtras());
            try {
                Exception e = (Exception) intent.getSerializableExtra(WeatherSyncAdapter.ARG_BROADCAST_ERROR);
                Snackbar.make(m_toolbar, e.getClass().getName() + "\n" + e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (Exception e) {
            }
        }
    };
}
