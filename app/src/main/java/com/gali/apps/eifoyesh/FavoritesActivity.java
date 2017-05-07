package com.gali.apps.eifoyesh;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * The favorites activity, used for browsing the favorite places.
 * It extends ListMapActivity, therefor has the following characteristics:
 * 1. it contains a PlacesListFragment(FavoritesListFragment) and a ResultMapFragment.
 * 1. Location manager updates -> used for displaying a place's distance
 * 2. fragment changing -> when touch a place in the list (in the list fragment), set the relevant map fragment
 * 3. has options menu
 */
public class FavoritesActivity extends ListMapActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //setting the tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setTitle(getResources().getString(R.string.favorites));

        //define the fragments
        if (smallDevice) {
            //for small device, set only the list fragment
            listFragment = (FavoritesListFragment) getFragmentManager().findFragmentByTag("favoritesListFragment");
            if (listFragment == null) {
                listFragment = new FavoritesListFragment();
                listFragment.setCurrentLocation(currentLocation);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, listFragment, "favoritesListFragment");
                transaction.commit();
            }
        } else {
            //for large device, set both list and map fragments
            listFragment = (FavoritesListFragment) getFragmentManager().findFragmentByTag("favoritesListFragment");
            if (listFragment == null) {
                listFragment = new FavoritesListFragment();
                listFragment.setCurrentLocation(currentLocation);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerList, listFragment, "favoritesListFragment");
                transaction.commit();
            }
            mapFragment = (ResultMapFragment) getFragmentManager().findFragmentByTag("mapFragment");
            if (mapFragment == null) {
                mapFragment = new ResultMapFragment();
                mapFragment.setCurrentLocation(currentLocation);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerMap, mapFragment, "mapFragment");
                transaction.commit();
            }

        }
    }
}
