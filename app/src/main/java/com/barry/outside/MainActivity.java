package com.barry.outside;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import com.barry.outside.account.AccountUtil;
import com.barry.outside.provider.WeatherProvider;
import com.barry.outside.syncadapter.WeatherSyncAdapter;

/**
 * Created by Owner on 2015/11/13.
 */
public class MainActivity extends ToolbarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar(R.mipmap.ic_action_navigation_arrow_back);

        AccountUtil.createDummyAccountIfNotExist(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        Account account = AccountUtil.getAccount(this);
        String authority = getResources().getString(R.string.auth_provider_weather);
        Bundle args = new Bundle();
        args.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        args.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(account, authority, args);
    }
}
