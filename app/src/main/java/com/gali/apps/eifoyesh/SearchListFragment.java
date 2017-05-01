package com.gali.apps.eifoyesh;

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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.gali.apps.eifoyesh.exceptions.NullLocationException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchListFragment extends PlacesListFragment{

    MySearchReciever mySearchReciever;
    SharedPreferences prefs;
    EditText searchET;

    boolean first = true;

    public SearchListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutId = R.layout.fragment_search_list;
        allPlaces = (ArrayList<ResultItem>) ResultItem.listAll(ResultItem.class);
        mRootView = super.onCreateView(inflater,container,savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mySearchReciever = new MySearchReciever();
        searchET = (EditText) mRootView.findViewById(R.id.searchET);
        mRootView.findViewById(R.id.searchIV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch nearMeSwitch = (Switch)mRootView.findViewById(R.id.nearMeSwitch);
                boolean nearMe = nearMeSwitch.isChecked();
                if (nearMe)
                    searchNearMe();
                else
                    searchByText();
            }

        });

        if (savedInstanceState!=null) {
           first = savedInstanceState.getBoolean("first");
        } else {
            if (!Utils.isConnected(getActivity())) {
                Toast.makeText(getActivity(), getResources().getString(R.string.noInternetConnectionMsg), Toast.LENGTH_SHORT).show();
                //get last search from db
                ArrayList<ResultItem> newResults  = (ArrayList<ResultItem>) ResultItem.listAll(ResultItem.class);
                allPlaces.clear();
                allPlaces.addAll(newResults);
                adapter.notifyDataSetChanged();

            } else {
                if (first) {
                    //run last search
                    String lastSearch = prefs.getString(Constants.PREF_LAST_SEARCH, null);
                    int lastSearchType = prefs.getInt(Constants.PREF_LAST_SEARCH_TYPE, -1);
                    if (lastSearch != null) {
                        searchET.setText(lastSearch);
                        if (lastSearchType == Constants.SEARCH_TYPE_TEXT)
                            searchByText();
                        else
                            searchNearMe();
                    }
                }

            }

        }
        first = false;
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
        //Toast.makeText(getActivity() , " User selected  " + info.position, Toast.LENGTH_LONG).show();

        ResultItem place = (ResultItem)allPlaces.get(info.position);
        switch (item.getItemId()) {
            case R.id.addToFavoritesMI:
                addToFavorites(place);
                Toast.makeText(getActivity(), getResources().getString(R.string.favoriteAdded), Toast.LENGTH_SHORT).show();
                break;
            case R.id.shareMI:
                startActivity(createShareIntent(place));
                break;
        }
        return  true;
    }


    //to share place details
    private Intent createShareIntent(ResultItem place) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");

        //send a link to place
        String urlPlace = Utils.buildPlaceUrl(place);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.sharePlaceMessage)+"\n"+ urlPlace);
        return shareIntent;
    }

    private void addToFavorites(ResultItem place) {
        List<FavoritePlace> favorites = FavoritePlace.listAll(FavoritePlace.class);

        for (int i = 0; i < favorites.size() ; i++) {
            FavoritePlace favorite = favorites.get(i);
            if (favorite.placeId.equals(place.placeId)) {
                Toast.makeText(getActivity(),getResources().getText(R.string.placeAlreadyInFavorites).toString() , Toast.LENGTH_SHORT).show();
                return;
            }
        }
        FavoritePlace favoritePlace = new FavoritePlace(place);
        favoritePlace.save();
    }

    private void saveLastSearch(String search, int type) {
        prefs.edit().putString(Constants.PREF_LAST_SEARCH,search).commit();
        prefs.edit().putInt(Constants.PREF_LAST_SEARCH_TYPE,type).commit();
        searchET.setText("");
        Iterator<ResultItem> i = allPlaces.iterator();
        while(i.hasNext()){
            i.next().save();
        }

    }

    private void searchByText() {
        if (!Utils.isConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.noInternetConnectionMsg), Toast.LENGTH_SHORT).show();
            return;
        }
        String search = searchET.getText().toString();
        SearchService.startActionFindByText(getActivity(), search, currentLocation);
    }

    private void searchNearMe() {
        if (!Utils.isConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.noInternetConnectionMsg), Toast.LENGTH_SHORT).show();
            return;
        }
        String search = searchET.getText().toString();
        try {
            SearchService.startActionFindNearMe(getActivity(), search, currentLocation);
        } catch (NullLocationException nle) {
            Toast.makeText(getActivity(), getResources().getString(R.string.currentLocationUnknown), Toast.LENGTH_SHORT).show();
        }
    }


    class MySearchReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            if (!status.equals("OK")) {
                String error = intent.getStringExtra("error_message");
                if (error!=null)
                    status = status+": "+error;
                Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
            }
            //delete all historySearch from db
            ResultItem.deleteAll(ResultItem.class);

            ArrayList<ResultItem> newResults  = intent.getParcelableArrayListExtra("allresults");
            allPlaces.clear();
            allPlaces.addAll(newResults);
            //allResults = intent.getParcelableArrayListExtra("allresults");
            String search = intent.getStringExtra("search");
            int type = intent.getIntExtra("type",Constants.SEARCH_TYPE_NEAR_ME);
            adapter.notifyDataSetChanged();
            saveLastSearch(search, type);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putParcelableArrayList("allPlaces",allPlaces);
        outState.putBoolean("first",first);

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
