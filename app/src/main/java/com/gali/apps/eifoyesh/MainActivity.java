package com.gali.apps.eifoyesh;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.orm.SugarContext;

public class MainActivity extends ListMapActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Utils.setupActionBar(this, false);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SugarContext.init(this);

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);

        if (smallDevice) {
            listFragment = (SearchListFragment) getFragmentManager().findFragmentByTag("listFragment");
            if (listFragment == null) {
                listFragment = new SearchListFragment();
                //listFragment.layoutId = R.layout.fragment_search_list;
                listFragment.setCurrentLocation(currentLocation);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, listFragment, "listFragment");
                transaction.commit();
            }
        } else {
            listFragment = (SearchListFragment) getFragmentManager().findFragmentByTag("listFragment");
            if (listFragment == null) {
                listFragment = new SearchListFragment();
                //listFragment.layoutId = R.layout.fragment_search_list;
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
