package com.gali.apps.eifoyesh;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);
        setTitle(getResources().getString(R.string.settings));

        addPreferencesFromResource(R.xml.settings);

        Preference deleteAllFavoritesButton = (Preference)findPreference(getResources().getString(R.string.deleteAllFavoritesKey));
        deleteAllFavoritesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                alertDialogBuilder.setMessage(getResources().getString(R.string.areYouSureYouWantToDeleteAllFavorites));
                alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete all favorites
                        FavoritePlace.deleteAll(FavoritePlace.class);
                        Toast.makeText(SettingsActivity.this,getResources().getString(R.string.allFavoritesDeleted),Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing, dont delete
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
            }
        });

        Preference exitButton = (Preference)findPreference(getResources().getString(R.string.exitKey));
        exitButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                finish();
                return true;
            }
        });

        Utils.setupActionBar(this);


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsMI:
                Intent settingsIntent = new Intent(this,SettingsActivity.class);
                startActivity(settingsIntent);
                break;
                /*
                SettingsFragment settingsFragment = new SettingsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack("settingsFragment");
                transaction.replace(R.id.activity_main, settingsFragment).commit();
                break;
                */
            case R.id.favoritesMI:
                Intent favoritesIntent = new Intent(this,FavoritesActivity.class);
                startActivity(favoritesIntent);
                break;
                /*
                FavoritesFragment favoritesFragment = new FavoritesFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack("favoritesFragment");
                //transaction2.replace(R.id.activity_main, favoritesFragment).commit();
                transaction.replace(R.id.fragmentContainerList, favoritesFragment);
                if (!smallDevice) {
                    mapFragment = new ResultMapFragment();
                    transaction.replace(R.id.fragmentContainerMap, mapFragment, "resultMapFragment");
                }
                transaction.commit();
                break;
                */

            case R.id.homeMI:
                Intent searchIntent = new Intent(this,MainActivity.class);
                startActivity(searchIntent);
                break;
//                getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                break;

        }
        return true;
    }

}
