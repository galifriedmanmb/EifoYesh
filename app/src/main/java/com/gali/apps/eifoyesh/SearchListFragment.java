package com.gali.apps.eifoyesh;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.gali.apps.eifoyesh.exceptions.NullLocationException;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchListFragment extends Fragment implements LocationListener {

    SharedPreferences prefs;
    private View mRootView;

    Location currentLocation;
    LocationManager locationManager;

    FragmentChanger fragmentChanger;
    EditText searchET;
    ContextMenuRecyclerView searchListRV;

    ArrayList<ResultItem> allResults = new ArrayList<>();
    PlacesListAdapter adapter;

    boolean first = true;

    public SearchListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setRetainInstance(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        fragmentChanger = (FragmentChanger) getActivity();
        if(mRootView==null){
            mRootView = inflater.inflate(R.layout.fragment_search_list, container, false);
        }
        //allResults = new ArrayList<>();
        searchET = (EditText) mRootView.findViewById(R.id.searchET);
        mRootView.findViewById(R.id.searchByTextBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchByText();
            }

        });
        mRootView.findViewById(R.id.searchNearMeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchNearMe();
            }
        });

        searchListRV = (ContextMenuRecyclerView)mRootView.findViewById(R.id.searchListRV);
        searchListRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        //adapter = new SearchListAdapter(getActivity() , fragmnetChanger );
        adapter = new PlacesListAdapter(getActivity() , fragmentChanger, currentLocation, allResults );
        searchListRV.setAdapter(adapter);
        registerForContextMenu(searchListRV);
        locationManager = (LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        getCurrentLocation();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new MySearchReciever(), new IntentFilter(SearchService.INTENT_FILTER_FINISHED_SEARCH));

        if (savedInstanceState!=null) {
            ArrayList<ResultItem> newResults = savedInstanceState.getParcelableArrayList("allResults");
            allResults.clear();
            allResults.addAll(newResults);
            adapter.notifyDataSetChanged();
            first = savedInstanceState.getBoolean("first");
        } else {
            if (!Utils.isConnected(getActivity())) {
                Toast.makeText(getActivity(), getResources().getString(R.string.noInternetConnectionMsg), Toast.LENGTH_SHORT).show();
                //get last search from db
                ArrayList<ResultItem> newResults  = (ArrayList<ResultItem>) ResultItem.listAll(ResultItem.class);
                allResults.clear();
                allResults.addAll(newResults);
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

        ResultItem place = allResults.get(info.position);
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

    private void getCurrentLocation() {
        //get last known location by gps
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        } else {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if(currentLocation == null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        } else {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, SearchListFragment.this);
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
                Toast.makeText(getActivity(), getResources().getText(R.string.messageMustAllowGPSPermission).toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startGPS() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
    }

    //to share movie details
    private Intent createShareIntent(ResultItem place) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");

        //send a link to place
        String urlPlace = Utils.buildPlaceUrl(place);
        shareIntent.putExtra(Intent.EXTRA_TEXT, urlPlace);
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

//        SaveSearchTask saveTask = new SaveSearchTask();
//        saveTask.execute();

        prefs.edit().putString(Constants.PREF_LAST_SEARCH,search).commit();
        prefs.edit().putInt(Constants.PREF_LAST_SEARCH_TYPE,type).commit();
        searchET.setText("");

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
            allResults.clear();
            allResults.addAll(newResults);
            //allResults = intent.getParcelableArrayListExtra("allresults");
            String search = intent.getStringExtra("search");
            int type = intent.getIntExtra("type",Constants.SEARCH_TYPE_NEAR_ME);
            adapter.notifyDataSetChanged();
            saveLastSearch(search, type);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        } else {
            startGPS();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation=location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("allResults",allResults);
        outState.putBoolean("first",first);

    }

    class SaveSearchTask extends AsyncTask<String, Void, Bitmap> {

        protected void onPreExecute() {
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            for (int i = 0; i < allResults.size() ; i++) {
                ResultItem resultItem = allResults.get(i);
                try {
                    Bitmap photoBM = Picasso.with(getActivity()).load(resultItem.iconUrl).get();
                    if (photoBM!=null) {
                        resultItem.photoEncoded = Utils.encodeToBase64(photoBM);
//                        iconIV.setImageBitmap(photoBM);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Bitmap bitmap) {
            ResultItem.deleteAll(ResultItem.class);
            for (int i = 0; i < allResults.size() ; i++) {
                ResultItem resultItem = allResults.get(i);
                resultItem.save();
            }

        }
    }
}
