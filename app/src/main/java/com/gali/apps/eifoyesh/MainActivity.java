package com.gali.apps.eifoyesh;

import android.app.FragmentTransaction;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.orm.SugarContext;

/**
 * The main activity, used for performing searches.
 * It extends ListMapActivity, therefor has the following characteristics:
 * 1. it contains a PlacesListFragment(SearchListFragment) and a ResultMapFragment.
 * 1. Location manager updates -> used for searching "near_me" and for displaying a place's distance
 * 2. fragment changing -> when touch a place in the list (in the list fragment), set the relevant map fragment
 * 3. has options menu
 */
public class MainActivity extends ListMapActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set default values for preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        //init sugar
        SugarContext.init(this);

        //define the fragments
        if (smallDevice) {
            listFragment = (SearchListFragment) getFragmentManager().findFragmentByTag("listFragment");
            if (listFragment == null) {
                listFragment = new SearchListFragment();
                listFragment.setCurrentLocation(currentLocation);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, listFragment, "listFragment");
                transaction.commit();
            }
        } else {
            listFragment = (SearchListFragment) getFragmentManager().findFragmentByTag("listFragment");
            if (listFragment == null) {
                listFragment = new SearchListFragment();
                listFragment.setCurrentLocation(currentLocation);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerList, listFragment, "listFragment");
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
