package com.barry.outside.air;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.barry.outside.R;
import com.barry.outside.base.ToolbarActivity;

public class PM25RankActivity extends ToolbarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);

        initToolbar(R.mipmap.ic_back);
        getSupportActionBar().setTitle(R.string.rank);

        Fragment f = new AirRankingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
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
}


