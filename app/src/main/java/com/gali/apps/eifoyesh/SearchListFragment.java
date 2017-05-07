package com.gali.apps.eifoyesh;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gali.apps.eifoyesh.exceptions.NullLocationException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This fragment handles the searches
 */
public class SearchListFragment extends PlacesListFragment{

    MySearchReciever mySearchReciever;
    SharedPreferences prefs;
    EditText searchET;
    Switch nearMeSwitch;
    SeekBar radiusSeekbar;

    public SearchListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //set the layout
        layoutId = R.layout.fragment_search_list;

        //load the last searched places from db
        allPlaces = (ArrayList<Place>) Place.listAll(Place.class);

        mRootView = super.onCreateView(inflater,container,savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //define a reciever for search broadcasts
        mySearchReciever = new MySearchReciever();

        //clear the searchET when clicked
        searchET = (EditText) mRootView.findViewById(R.id.searchET);
        searchET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)v).setText("");
            }
        });

        //nearMe switch: dont show radius seekbar if not "nearMe"
        nearMeSwitch = (Switch)mRootView.findViewById(R.id.nearMeSwitch);
        nearMeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RelativeLayout radiusLayout = (RelativeLayout) mRootView.findViewById(R.id.radiusLayout);
                if (isChecked)
                    radiusLayout.setVisibility(LinearLayout.VISIBLE);
                else
                    radiusLayout.setVisibility(LinearLayout.GONE);
            }
        });

        //radius setting
        int radius = prefs.getInt(Constants.PREF_RADIUS, 1);
        radiusSeekbar = (SeekBar) mRootView.findViewById(R.id.radiusSeekBar);
        radiusSeekbar.setMax(9);
        final TextView minTV = (TextView) mRootView.findViewById(R.id.minTV);
        TextView maxTV = (TextView) mRootView.findViewById(R.id.maxTV);
        minTV.setText(""+radius);
        maxTV.setText(""+10);
        radiusSeekbar.setProgress(radius-1);
        radiusSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minTV.setText(""+(progress+1));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                prefs.edit().putInt(Constants.PREF_RADIUS,seekBar.getProgress()+1).commit();
                searchNearMe();
            }
        });

        //searching
        mRootView.findViewById(R.id.searchIV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get rid of virtual keyboard
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);

                boolean nearMe = nearMeSwitch.isChecked();
                if (nearMe)
                    searchNearMe();
                else
                    searchByText();
            }

        });

        if (savedInstanceState!=null) {
           //first = savedInstanceState.getBoolean("first");
        } else {
            //load the last search:
            if (!Utils.isConnected(getActivity())) {
                //no internet connection, get last search from db
                Toast.makeText(getActivity(), getResources().getString(R.string.noInternetConnectionMsg), Toast.LENGTH_SHORT).show();
                ArrayList<Place> newResults  = (ArrayList<Place>) Place.listAll(Place.class);
                allPlaces.clear();
                allPlaces.addAll(newResults);
                adapter.notifyDataSetChanged();
            } else {
                //internet connection ok, run last search (if it's first time in this page)
                boolean first = prefs.getBoolean(Constants.PREF_FIRST,true);
                if (first) {
                    //run last search
                    String lastSearch = prefs.getString(Constants.PREF_LAST_SEARCH, null);
                    int lastSearchType = prefs.getInt(Constants.PREF_LAST_SEARCH_TYPE, -1);
                    if (lastSearch != null) {
                        searchET.setText(lastSearch);
                        if (lastSearchType == Constants.SEARCH_TYPE_TEXT) {
                            nearMeSwitch.setChecked(false);
                            searchByText();
                        } else {
                            searchNearMe();
                        }
                    }
                }

            }

        }
        prefs.edit().putBoolean(Constants.PREF_FIRST,false).commit();
        return mRootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Inflate Menu from xml resource
        getActivity().getMenuInflater().inflate(R.menu.single_place_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        Place place = (Place)allPlaces.get(info.position);
        switch (item.getItemId()) {
            case R.id.addToFavoritesMI:
                Utils.addToFavorites(place,getActivity());
                break;
            case R.id.shareMI:
                try {
                    startActivity(Utils.createShareIntent(place,getActivity()));
                } catch (ActivityNotFoundException e) {}
                break;
        }
        return  true;
    }

    private void saveLastSearch(String search, int type) {
        //save the search params
        prefs.edit().putString(Constants.PREF_LAST_SEARCH,search).commit();
        prefs.edit().putInt(Constants.PREF_LAST_SEARCH_TYPE,type).commit();

        //delete all history Search from db
        Place.deleteAll(Place.class);
        //save all search results in db
        Iterator<Place> i = allPlaces.iterator();
        while(i.hasNext()){
            i.next().save();
        }

    }

    private void searchByText() {
        //if all conditions are ok, start the service of searchByText
        if (!Utils.isConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.noInternetConnectionMsg), Toast.LENGTH_SHORT).show();
            return;
        }
        String search = searchET.getText().toString();
        if (search.length()>0)
            SearchService.startActionFindByText(getActivity(), search, getCurrentLocation());
        else
            Toast.makeText(getActivity(), getResources().getString(R.string.enterValueToSearch), Toast.LENGTH_SHORT).show();
    }

    private void searchNearMe() {
        //if all conditions are ok, start the service of searchNearMe
        if (!Utils.isConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.noInternetConnectionMsg), Toast.LENGTH_SHORT).show();
            return;
        }
        String search = searchET.getText().toString();
        if (search.length()>0) {
            try {
                SearchService.startActionFindNearMe(getActivity(), search, getCurrentLocation(), prefs.getInt(Constants.PREF_RADIUS,1), prefs.getString("distance_units", Constants.SHARED_PREFERENCES_UNIT_KM));
            } catch (NullLocationException nle) {
                Toast.makeText(getActivity(), getResources().getString(R.string.currentLocationUnknown), Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(getActivity(), getResources().getString(R.string.enterValueToSearch), Toast.LENGTH_SHORT).show();
    }

    //recieve search results
    class MySearchReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            //handle errors
            if (!status.equals("OK")) {
                String error = intent.getStringExtra("error_message");
                if (error!=null)
                    status = status+": "+error;
                Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
            }

            //handle results
            ArrayList<Place> newResults  = intent.getParcelableArrayListExtra("allresults");
            allPlaces.clear();
            allPlaces.addAll(newResults);
            String search = intent.getStringExtra("search");
            int type = intent.getIntExtra("type",Constants.SEARCH_TYPE_NEAR_ME);
            adapter.notifyDataSetChanged();
            //save the search
            saveLastSearch(search, type);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mySearchReciever, new IntentFilter(SearchService.INTENT_FILTER_FINISHED_SEARCH));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mySearchReciever);
    }

}
