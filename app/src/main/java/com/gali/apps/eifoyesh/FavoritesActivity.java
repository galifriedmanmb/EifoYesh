package com.gali.apps.eifoyesh;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class FavoritesActivity extends ListMapActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Utils.setupActionBar(this, true);
        setTitle(getResources().getString(R.string.favorites));

        if (smallDevice) {
            listFragment = (FavoritesListFragment) getFragmentManager().findFragmentByTag("favoritesListFragment");
            if (listFragment == null) {
                listFragment = new FavoritesListFragment();
                //listFragment.layoutId = R.layout.fragment_places_list;
                listFragment.setCurrentLocation(currentLocation);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, listFragment, "favoritesListFragment");
                transaction.commit();
            }
        } else {
            listFragment = (FavoritesListFragment) getFragmentManager().findFragmentByTag("favoritesListFragment");
            if (listFragment == null) {
                listFragment = new FavoritesListFragment();
                //listFragment.layoutId = R.layout.fragment_places_list;
                listFragment.setCurrentLocation(currentLocation);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerList, listFragment, "favoritesListFragment");
                transaction.commit();
            }
            mapFragment = (ResultMapFragment) getFragmentManager().findFragmentByTag("favoritesMapFragment");
            if (mapFragment == null) {
                mapFragment = new ResultMapFragment();
                mapFragment.setCurrentLocation(currentLocation);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerMap, mapFragment, "favoritesMapFragment");
                transaction.commit();
            }
        }
    }
}
