package com.gali.apps.eifoyesh;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements FragmnetChanger {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
/*
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .build();
*/

        SearchListFragment searchListFragment = (SearchListFragment)getFragmentManager().findFragmentByTag("searchListFragment");
        if (searchListFragment == null) {
            searchListFragment = new SearchListFragment();
            getFragmentManager().beginTransaction().replace(R.id.activity_main, searchListFragment, "searchListFragment").commit();
            searchListFragment.picMaxHeight = 100;
        }
            //searchListFragment.mGoogleApiClient = mGoogleApiClient;

    }

    public void changeFragments(ResultItem resultItem) {

        if (resultItem!=null) {
            ResultMapFragment resultMapFragment = new ResultMapFragment();
            resultMapFragment.resultItem = resultItem;

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.addToBackStack("changeFrags");
            transaction.replace(R.id.activity_main, resultMapFragment).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settingsMI:
                SettingsFragment settingsFragment = new SettingsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack("settingsFrags");
                transaction.replace(R.id.activity_main, settingsFragment).commit();
                break;
            case R.id.exitMI:
                finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount()==0){
            finish();
        } else {
            getFragmentManager().popBackStack();
        }

    }

    }
