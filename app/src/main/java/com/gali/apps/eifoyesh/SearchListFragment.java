package com.gali.apps.eifoyesh;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.gali.apps.eifoyesh.exceptions.NullLocationException;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
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

    //GoogleApiClient mGoogleApiClient;
    int picMaxHeight;
    FragmnetChanger fragmnetChanger;
    EditText searchET;
    RecyclerView searchListRV;

    ArrayList<ResultItem> allResults;
    SearchListAdapter adapter;

    public SearchListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setRetainInstance(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        fragmnetChanger = (FragmnetChanger) getActivity();
        if(mRootView==null){
            mRootView = inflater.inflate(R.layout.fragment_search_list, container, false);
        }

        if (savedInstanceState!=null) {
            allResults = savedInstanceState.getParcelableArrayList("allResults");
            picMaxHeight = savedInstanceState.getInt("picMaxHeight");
        }
        if (allResults==null) {
            //get last search from db
            allResults = (ArrayList<ResultItem>) ResultItem.listAll(ResultItem.class);

        }
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

        searchListRV = (RecyclerView)mRootView.findViewById(R.id.searchListRV);
        searchListRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        adapter = new SearchListAdapter(getActivity() , fragmnetChanger );
        searchListRV.setAdapter(adapter);

        locationManager = (LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        getCurrentLocation();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new MySearchReciever(), new IntentFilter(SearchService.INTENT_FILTER_FINISHED_SEARCH));


        return mRootView;
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
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    } else {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, SearchListFragment.this);
                    }
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

    private void addToSearchHistory(String query, int type) {
        List<SearchHistoryItem> history = SearchHistoryItem.listAll(SearchHistoryItem.class);
        if (history.size()>9) {
            SearchHistoryItem oldItem = history.get(0);
            oldItem.delete();
            SearchHistoryItem newItem = new SearchHistoryItem(query,type);
            newItem.save();
        }

    }

    private void saveLastSearch() {
        ResultItem.deleteAll(ResultItem.class);
        for (int i = 0; i < allResults.size() ; i++) {
            ResultItem item = allResults.get(i);
            item.save();
        }
    }

    private void searchByText() {
        String search = searchET.getText().toString();
        SearchService.startActionFindByText(getActivity(),search,picMaxHeight,currentLocation);
        addToSearchHistory(search, Constants.SEARCH_TYPE_TEXT);
    }

    private void searchNearMe() {
        String search = searchET.getText().toString();
        try {
            SearchService.startActionFindNearMe(getActivity(), search, picMaxHeight, currentLocation);
            addToSearchHistory(search, Constants.SEARCH_TYPE_NEAR_ME);
        } catch (NullLocationException nle) {
            Toast.makeText(getActivity(), "current location is unknown", Toast.LENGTH_SHORT).show();
        }
    }

    private class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ResultItemViewHolder> {

        //ArrayList<ResultItem> allResults;
        Context c;
        FragmnetChanger fragmnetChanger;

        //public SearchListAdapter(ArrayList<ResultItem> allResults, Context c, FragmnetChanger fragmnetChanger) {
        public SearchListAdapter(Context c, FragmnetChanger fragmnetChanger) {
            //this.allResults = allResults;
            this.c = c;
            this.fragmnetChanger= fragmnetChanger;
        }

        public ResultItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View singleview = LayoutInflater.from(c).inflate(R.layout.search_result_item, null);
            ResultItemViewHolder singleResultVH= new ResultItemViewHolder(singleview);
            return singleResultVH;
        }

        @Override
        public int getItemCount() {
            return allResults.size();
        }

        @Override
        public void onBindViewHolder(ResultItemViewHolder holder, int position) {
            ResultItem resultItem = allResults.get(position);
            holder.bindData(resultItem);

        }

        class ResultItemViewHolder extends RecyclerView.ViewHolder {

            TextView nameTV;
            TextView addressTV;
            TextView numberTV;
            TextView distanceTV;
            ImageView iconIV;
            LinearLayout layout;


            public ResultItemViewHolder(View itemView) {
                super(itemView);
                nameTV = (TextView) itemView.findViewById(R.id.nameTV);
                addressTV = (TextView) itemView.findViewById(R.id.addressTV);
                numberTV = (TextView) itemView.findViewById(R.id.numberTV);
                distanceTV = (TextView) itemView.findViewById(R.id.distanceTV);
                iconIV = (ImageView) itemView.findViewById(R.id.iconIV);
                layout = (LinearLayout) itemView.findViewById(R.id.resultItemLinearLayout);

            }

            public void bindData (final ResultItem resultItem) {
                nameTV.setText(resultItem.name);
                addressTV.setText(resultItem.address);
                numberTV.setText(""+resultItem.number);
                if (currentLocation!=null) {
                    //String unit = prefs.getString(Constants.SHARED_PREFERENCES_UNIT, Constants.SHARED_PREFERENCES_UNIT_KM);
                    String unit = prefs.getString("distance_units",Constants.SHARED_PREFERENCES_UNIT_KM);
                    double distance = ResultItem.getDistance(resultItem.lat, resultItem.lng, currentLocation.getLatitude(), currentLocation.getLongitude(), unit);
                    DecimalFormat df = new DecimalFormat("#.#");
                    String distanceString = df.format(distance);
//                    String unitString = unit.equals(Constants.SHARED_PREFERENCES_UNIT_KM) ? Constants.SHARED_PREFERENCES_UNIT_KM : "miles";
                    distanceTV.setText(distanceString + " " + unit);
                } else {
                    distanceTV.setText("");
                }
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragmnetChanger.changeFragments(resultItem);
                    }
                });
                Picasso.with(c).load(resultItem.iconUrl).into(iconIV);

            }
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
            allResults = intent.getParcelableArrayListExtra("allresults");
            //searchListRV.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
            //SearchListAdapter adapter= new SearchListAdapter(allResults, context , fragmnetChanger );
            //searchListRV.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            saveLastSearch();
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
        outState.putInt("picMaxHeight",picMaxHeight);

    }
/*
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState!=null) {
            allResults = savedInstanceState.getParcelableArrayList("allResults");
            adapter.notifyDataSetChanged();
        }

    }
*/
    /*
    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {
        private int mHeight;
        private int mWidth;

        public PhotoTask(int width, int height) {
            mHeight = height;
            mWidth = width;
        }



        protected AttributedPhoto doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }

            final String placeId = params[0];
            AttributedPhoto attributedPhoto = null;

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadata.getCount() > 0 && !isCancelled()) {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadata.get(0);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();

                    attributedPhoto = new AttributedPhoto(attribution, image);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            return attributedPhoto;
        }



        class AttributedPhoto {

            public final CharSequence attribution;

            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }
*/
}
