package com.gali.apps.eifoyesh;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultMapFragment extends Fragment {

    MySearchReciever mySearchReciever;
    private View mRootView;
    private Location currentLocation;
    ResultItem place;
    ImageView placeIV;
    TextView nameTV;
    TextView addressTV;
    TextView phoneTV;
    TextView websiteTV;
    RatingBar ratingBar;
    TextView ratingNumTV;

    public ResultMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView==null){
            mRootView = inflater.inflate(R.layout.fragment_result_map, container, false);
        }


        nameTV = (TextView)mRootView.findViewById(R.id.nameTV);
        placeIV = (ImageView)mRootView.findViewById(R.id.placeIV);
        addressTV = (TextView)mRootView.findViewById(R.id.addressTV);
        phoneTV = (TextView)mRootView.findViewById(R.id.phoneTV);
        websiteTV = (TextView)mRootView.findViewById(R.id.websiteTV);
        ratingBar = (RatingBar)mRootView.findViewById(R.id.ratingBar);
        ratingNumTV = (TextView)mRootView.findViewById(R.id.ratingNumTV);

        mySearchReciever = new MySearchReciever();
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) mRootView.findViewById(R.id.collapsing_toolbar);

        if (savedInstanceState!=null) {
            place = savedInstanceState.getParcelable("place");
            currentLocation = savedInstanceState.getParcelable("currentLocation");
        }

        MapFragment mapFragment = new MapFragment();
        getFragmentManager().beginTransaction().replace(R.id.mapFragmentContainer,mapFragment).commit();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                if (place != null) {
                    LatLng latLng = new LatLng(place.lat, place.lng);
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    googleMap.moveCamera(update);
                } else {
                    if (currentLocation != null) {
                        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);

                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        googleMap.moveCamera(update);
                    }

                }
            }
        });


        if (place!=null) {
            nameTV.setText(place.name);
            Picasso.with(getActivity()).load(place.iconUrl).into(placeIV);
            SearchService.startActionGetPlaceDetails(getActivity(),place.placeId);

            LinearLayout favoritesLO = (LinearLayout)mRootView.findViewById(R.id.buttonsLOFavoritesLO);
            String title = getActivity().getTitle().toString();
            if (title.equals(getResources().getString(R.string.favorites))) {
                favoritesLO.setVisibility(LinearLayout.GONE);
            } else {
                favoritesLO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.addToFavorites(place, getActivity());
                    }
                });
            }

            mRootView.findViewById(R.id.buttonsLOShareLO).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(Utils.createShareIntent(place,getActivity()));
                }
            });

        } else {
            mRootView.findViewById(R.id.nestedScrollView).setVisibility(View.INVISIBLE);
            AppBarLayout.LayoutParams p = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
            p.setScrollFlags(0);
            collapsingToolbarLayout.setLayoutParams(p);

            AppBarLayout appBarLayout = (AppBarLayout)mRootView.findViewById(R.id.appBarLayout);
            CoordinatorLayout.LayoutParams tp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
            tp.height = AppBarLayout.LayoutParams.MATCH_PARENT;
            appBarLayout.setLayoutParams(tp);


        }

        return mRootView;
    }

    public void setCurrentLocation (Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("currentLocation",currentLocation);
        outState.putParcelable("place",place);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mySearchReciever, new IntentFilter(SearchService.INTENT_FILTER_FINISHED_DETAILS));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mySearchReciever);

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

            ResultItem placeDetails  = intent.getParcelableExtra("place");
            String address = placeDetails.address;
            String phone = placeDetails.phone;
            final String website = placeDetails.website;
            double rating = placeDetails.rating;

            if (address!=null)
                addressTV.setText(address);
            if (phone!=null)
                phoneTV.setText(phone);
            if (website!=null) {
                websiteTV.setText(website);
                websiteTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(website));
                        startActivity(intent);
                    }
                });
            }
            if (rating>0) {
                Float ratingFloat = (float)rating;
                DecimalFormat df = new DecimalFormat("#.#");
                String ratingStr = df.format(ratingFloat);
                ratingNumTV.setText(ratingStr);
                ratingBar.setRating(ratingFloat);
            } else {
                LinearLayout ratingLayout = (LinearLayout)mRootView.findViewById(R.id.generalLOinner);
                ratingLayout.setVisibility(LinearLayout.GONE);
            }


        }
    }


}
