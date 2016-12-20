package com.barry.outside.air;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.barry.outside.R;
import com.barry.outside.AirInfoTypeFragmentDialog;
import com.barry.outside.provider.WeatherProvider;

public class AirRankListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,AirInfoTypeFragmentDialog.OnSelectedListener {

    Uri contentUri;
    SiteCursorAdapter adapter;
    boolean isASC = false;
    int mAirInfoPos = 0;
    String mAirInfos[];
    String mProviderInfo[] = {WeatherProvider.FIELD_PM25, WeatherProvider.FIELD_PM10, WeatherProvider.FIELD_PSI, WeatherProvider.FIELD_O3, WeatherProvider.FIELD_CO};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        adapter = new SiteCursorAdapter(getContext(), null);

        contentUri = WeatherProvider.getProviderUri(getContext().getResources().getString(R.string.auth_provider_weather), WeatherProvider.TABLE_PM25);
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAirInfos = new String[]{getString(R.string.pm25), getString(R.string.pm10), getString(R.string.PSI), getString(R.string.O3), getString(R.string.CO)};
        setHasOptionsMenu(true);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(0, null, this);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mAirInfos[mAirInfoPos] + getString(R.string.rank));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_rank, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_swap_order) {
            isASC = !isASC;
            getLoaderManager().restartLoader(0, null, this);
        }
        if (id == R.id.action_choose_airinfo_type) {
            AirInfoTypeFragmentDialog f = AirInfoTypeFragmentDialog.getInstance(mAirInfoPos);
            f.setSelectedListener(this);
            f.show(getChildFragmentManager(), "");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cl = new CursorLoader(getActivity());
        cl.setUri(contentUri);
        if (isASC) {
            cl.setSortOrder("cast(" + mProviderInfo[mAirInfoPos] + " as REAL) ASC, " + WeatherProvider.FIELD_COUNTRY + " ASC");
        } else {
            cl.setSortOrder("cast(" + mProviderInfo[mAirInfoPos] + " as REAL) DESC, " + WeatherProvider.FIELD_COUNTRY + " ASC");
        }

        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (adapter != null) {
            adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter != null) {
            adapter.swapCursor(null);
        }
    }

    @Override
    public void onSelected(int position) {
        mAirInfoPos = position;
        adapter.setInfoType(position);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mAirInfos[mAirInfoPos] + getString(R.string.rank));
        getLoaderManager().restartLoader(0, null, this);
    }
}

