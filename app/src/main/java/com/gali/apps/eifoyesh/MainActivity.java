package com.gali.apps.eifoyesh;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity implements FragmnetChanger {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount()==0){
            finish();
        } else {
            getFragmentManager().popBackStack();
        }

    }

    }
