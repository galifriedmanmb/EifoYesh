package com.gali.apps.eifoyesh;

import android.app.FragmentTransaction;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.orm.SugarContext;


public class MainActivity extends AppCompatActivity implements FragmentChanger {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SugarContext.init(this);
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
                transaction.addToBackStack("settingsFragment");
                transaction.replace(R.id.activity_main, settingsFragment).commit();
                break;
            case R.id.favoritesMI:
                FavoritesFragment favoritesFragment = new FavoritesFragment();
                FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                transaction2.addToBackStack("favoritesFragment");
                transaction2.replace(R.id.activity_main, favoritesFragment).commit();
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
