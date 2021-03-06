package com.gali.apps.eifoyesh;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * A General class that contains a PlacesListFragment and a ResultMapFragment
 * has 2 subclasses:
 * 1. MainActivity  -> used for searching
 * 2. FavoritesActivity  ->  used for browsing through favorite places
 *
 * this class handles these capabilities:
 * 1. Location manager updates -> used for searching "near_me" and for displaying a place's distance
 * 2. fragment changing -> when touch a place in the list (in the list fragment), set the relevant map fragment
 * 3. creates the options menu and defines the behaviour
 */
public class ListMapActivity extends AppCompatActivity implements FragmentChanger, LocationListener {

    boolean smallDevice = true;
    LocationManager locationManager;
    Location currentLocation = null;
    PlacesListFragment listFragment;
    ResultMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       //determin the screen size, to be used when setting fragments
       int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
       if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE)
           smallDevice = false;

        //location updates
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        //start with the current location
        getCurrentLocation();
    }

    /**
     * determines the current location by:
     * 1. get last knows location from fines location provider available
     * 2. try to get location by network provider if not successful otherwize
     */
    private void getCurrentLocation() {
        //get last known location by gps
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        } else {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        //if not successful, get last known location by network provider
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
                        if (ActivityCompat.checkSelfPermission(ListMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ListMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        } else {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, ListMapActivity.this);
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
            if (grantResults!=null && grantResults.length>0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //register for gps location updates
                    startGPS();
                } else {
                    Toast.makeText(this, getResources().getText(R.string.messageMustAllowGPSPermission).toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //register for gps location updates
    private void startGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
    }


    public void changeFragmentsToMap(Place place) {
        //when a place was picked, change too the relevant map fragment
        if (place != null) {
            mapFragment = new ResultMapFragment();
            mapFragment.place = place;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if (smallDevice) {
                transaction.addToBackStack("changeMap");
                transaction.replace(R.id.fragmentContainer, mapFragment,"mapFragment").commit();
            } else
                transaction.replace(R.id.fragmentContainerMap, mapFragment,"mapFragment").commit();
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
                //open settings activity
                Intent settingsIntent = new Intent(this,SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.favoritesMI:
                //open favorites activity
                Intent favoritesIntent = new Intent(this,FavoritesActivity.class);
                startActivity(favoritesIntent);
                break;
             case R.id.homeMI:
                //open main activity, for searches
                Intent searchIntent = new Intent(this,MainActivity.class);
                startActivity(searchIntent);
                break;
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
        //refister for gps updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        } else {
            startGPS();
        }
    }

    @Override
    public void onPause() {
        //unregister from gps updates
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation=location;
        //update both fragments with new location
        if (listFragment!=null)
            listFragment.setCurrentLocation(currentLocation);
        if (mapFragment!=null)
            mapFragment.setCurrentLocation(currentLocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Toast.makeText(this, "location: onStatusChanged "+status+" "+provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
//        Toast.makeText(this, "location: onProviderEnabled: "+provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
//        Toast.makeText(this, "location: onProviderDisabled: "+provider, Toast.LENGTH_SHORT).show();
    }

}
