package com.gali.apps.eifoyesh;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.Preference;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * the activity to display the settings
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.settings));

        addPreferencesFromResource(R.xml.settings);

        //handle delete all favorites
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

        //handle exit from settings
        Preference exitButton = (Preference)findPreference(getResources().getString(R.string.exitKey));
        exitButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                finish();
                return true;
            }
        });

        //create the toolbar
        setupActionBar();
    }

    //creating the toolbar
    private void setupActionBar() {
        Toolbar toolbar = new Toolbar(this);
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        AppBarLayout appBarLayout = new AppBarLayout(this);
        appBarLayout.addView(toolbar);
//        appBarLayout.setLayoutParams(new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.WRAP_CONTENT));

        final ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        final ViewGroup window = (ViewGroup) root.getChildAt(0);
        window.addView(appBarLayout, 0);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
            case R.id.favoritesMI:
                Intent favoritesIntent = new Intent(this,FavoritesActivity.class);
                startActivity(favoritesIntent);
                break;
            case R.id.homeMI:
                Intent searchIntent = new Intent(this,MainActivity.class);
                startActivity(searchIntent);
                break;
        }
        return true;
    }

}
