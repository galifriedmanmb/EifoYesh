package com.gali.apps.eifoyesh;

import android.app.FragmentTransaction;
import android.os.Bundle;

public class FavoritesActivity extends ListMapActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Utils.setupActionBar(this, true);
        setTitle(getResources().getString(R.string.favorites));
        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);
        if (smallDevice) {
            listFragment = (FavoritesListFragment) getFragmentManager().findFragmentByTag("favoritesListFragment");
            if (listFragment == null) {
                listFragment = new FavoritesListFragment();
                //listFragment.layoutId = R.layout.fragment_places_list;
                listFragment.currentLocation = currentLocation;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, listFragment, "favoritesListFragment");
                transaction.commit();
            }
        } else {
            listFragment = (FavoritesListFragment) getFragmentManager().findFragmentByTag("favoritesListFragment");
            if (listFragment == null) {
                listFragment = new FavoritesListFragment();
                //listFragment.layoutId = R.layout.fragment_places_list;
                listFragment.currentLocation = currentLocation;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerList, listFragment, "favoritesListFragment");
                transaction.commit();
            }
            mapFragment = (ResultMapFragment) getFragmentManager().findFragmentByTag("favoritesMapFragment");
            if (mapFragment == null) {
                mapFragment = new ResultMapFragment();
                mapFragment.location = currentLocation;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerMap, mapFragment, "favoritesMapFragment");
                transaction.commit();
            }
        }
    }
}
