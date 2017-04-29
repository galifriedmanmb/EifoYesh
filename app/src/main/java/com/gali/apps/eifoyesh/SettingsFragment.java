package com.gali.apps.eifoyesh;


import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {

    FragmentChanger fragmentChanger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        fragmentChanger = (FragmentChanger) getActivity();
        Preference deleteAllFavoritesButton = (Preference)findPreference(getResources().getString(R.string.deleteAllFavoritesKey));
        deleteAllFavoritesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(getResources().getString(R.string.areYouSureYouWantToDeleteAllFavorites));
                alertDialogBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete all favorites
                        FavoritePlace.deleteAll(FavoritePlace.class);
                        Toast.makeText(getActivity(),getResources().getString(R.string.allFavoritesDeleted),Toast.LENGTH_SHORT).show();
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
                fragmentChanger.goBack();
                return true;
            }
        });


    }


}
