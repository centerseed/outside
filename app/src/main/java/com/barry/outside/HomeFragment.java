package com.barry.outside;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.barry.outside.air.AirInfoFragment;
import com.barry.outside.air.AirMapFragment;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;


public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AirInfoFragment airInfoFragment = new AirInfoFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.containerAirInfo, airInfoFragment, null).commit();

        AirMapFragment mapFragment = new AirMapFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.containerMap, mapFragment, null).commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_localization) {
        }
        return super.onOptionsItemSelected(item);
    }
}
