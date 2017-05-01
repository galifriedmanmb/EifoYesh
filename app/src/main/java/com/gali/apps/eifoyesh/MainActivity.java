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
/*
    boolean smallDevice = true;
    LocationManager locationManager;
    Location currentLocation = null;
    SearchListFragment searchListFragment;
    ResultMapFragment resultMapFragment;
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);
        Utils.setupActionBar(this, false);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SugarContext.init(this);
/*
        if (findViewById(R.id.fragmentContainerMap)!=null)
            smallDevice = false;

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        getCurrentLocation();
*/
        if (smallDevice) {
            listFragment = (SearchListFragment) getFragmentManager().findFragmentByTag("listFragment");
            if (listFragment == null) {
                listFragment = new SearchListFragment();
                //listFragment.layoutId = R.layout.fragment_search_list;
                listFragment.currentLocation = currentLocation;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, listFragment, "listFragment");
                transaction.commit();
            }
        } else {
            listFragment = (SearchListFragment) getFragmentManager().findFragmentByTag("listFragment");
            if (listFragment == null) {
                listFragment = new SearchListFragment();
                //listFragment.layoutId = R.layout.fragment_search_list;
                listFragment.currentLocation = currentLocation;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerList, listFragment, "listFragment");
                transaction.commit();
            }
            mapFragment = (ResultMapFragment) getFragmentManager().findFragmentByTag("mapFragment");
            if (mapFragment == null) {
                mapFragment = new ResultMapFragment();
                mapFragment.location = currentLocation;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerMap, mapFragment, "mapFragment");
                transaction.commit();
            }
        }

    }
/*
    private void getCurrentLocation() {
        //get last known location by gps
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        } else {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if(currentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            } else {
                currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }



        //wait for 60 seconds and try to get location using network provider (cell ro wifi)
        final Handler handler = new Handler();
        Runnable getLocationByNetwork = new Runnable() {
            @Override
            public void run() {
                if(currentLocation==null) {
                    try {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        } else {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, MainActivity.this);
                        }
                    }catch (Exception e) {}
                }
            }
        };
        handler.postDelayed(getLocationByNetwork  , 60000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 12) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGPS();
            } else {
                Toast.makeText(this, getResources().getText(R.string.messageMustAllowGPSPermission).toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
    }


    public void changeFragmentsToMap(ResultItem resultItem) {

        if (resultItem != null) {
            resultMapFragment = new ResultMapFragment();
            Location location = new Location("");
            location.setLatitude(resultItem.lat);
            location.setLongitude(resultItem.lng);
            resultMapFragment.location = location;

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if (smallDevice) {
                transaction.addToBackStack("changeMap");
                transaction.replace(R.id.fragmentContainerMap, resultMapFragment,"resultMapFragment").commit();
            } else
                transaction.replace(R.id.fragmentContainerMap, resultMapFragment,"resultMapFragment").commit();
        }

    }

    @Override
    public void goBack() {
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsMI:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                break;

//                SettingsFragment settingsFragment = new SettingsFragment();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.addToBackStack("settingsFragment");
//                transaction.replace(R.id.activity_main, settingsFragment).commit();
//                break;

            case R.id.favoritesMI:
                FavoritesFragment favoritesFragment = new FavoritesFragment();
                FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                transaction2.addToBackStack("favoritesFragment");
                //transaction2.replace(R.id.activity_main, favoritesFragment).commit();
                transaction2.replace(R.id.fragmentContainerList, favoritesFragment);
                if (!smallDevice) {
                    resultMapFragment = new ResultMapFragment();
                    transaction2.replace(R.id.fragmentContainerMap, resultMapFragment, "resultMapFragment");
                }
                transaction2.commit();
                break;
            case R.id.homeMI:
                getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;
            case R.id.exitMI:
                finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        } else {
            getFragmentManager().popBackStack();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        } else {
            startGPS();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation=location;
        searchListFragment.currentLocation = currentLocation;
        resultMapFragment.location = currentLocation;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "location: onStatusChanged "+status+" "+provider, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "location: onProviderEnabled: "+provider, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "location: onProviderDisabled: "+provider, Toast.LENGTH_SHORT).show();

    }
    */

}
