package com.gali.apps.eifoyesh;


import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

/**
 * The fragment that displays the place in a map, and the detailed information about the place
 */
public class ResultMapFragment extends Fragment {

    MySearchReciever mySearchReciever;
    private View mRootView;
    private Location currentLocation;
    Place place;
    ImageView placeIV;
    TextView nameTV;
    TextView addressTV;
    TextView phoneTV;
    TextView websiteTV;
    RatingBar ratingBar;
    TextView ratingNumTV;
    ImageView websiteIV;
    ImageView phoneIV;

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
        websiteIV = (ImageView)mRootView.findViewById(R.id.globeIV);
        phoneIV = (ImageView)mRootView.findViewById(R.id.phoneIV);

        //for retrieving detailed data on the plavce
        mySearchReciever = new MySearchReciever();

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) mRootView.findViewById(R.id.collapsing_toolbar);

        if (savedInstanceState!=null) {
            place = savedInstanceState.getParcelable("place");
            currentLocation = savedInstanceState.getParcelable("currentLocation");
        }

        //display the map
        MapFragment mapFragment = new MapFragment();
        getFragmentManager().beginTransaction().replace(R.id.mapFragmentContainer,mapFragment).commit();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                if (place != null) {
                    //open the map on the place's location
                    LatLng latLng = new LatLng(place.lat, place.lng);
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);

                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    googleMap.moveCamera(update);
                } else {
                    //no specific place (in large devices, before a place was picked)
                    if (currentLocation != null) {
                        //open the map on current location
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


        //display the detailed data of the place
        if (place!=null) {
            nameTV.setText(place.name);
            if (Utils.isConnected(getActivity())) {
                //has internet, get the details
                Picasso.with(getActivity()).load(place.iconUrl).into(placeIV);
                //get the details and fill the details in the UI
                SearchService.startActionGetPlaceDetails(getActivity(), place.placeId);
            } else {
                //no internet, try to load from db
                Bitmap photoBM = Utils.decodeBase64(place.photoEncoded);
                placeIV.setImageBitmap(photoBM);
                String address = place.address;
                String phone = place.phone;
                String website = place.website;
                double rating = place.rating;
                //fill the details in the UI
                setDetails(address, phone, website, rating);
            }

            //only show "favorites" icon if not in favorites screen already...
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

            //share
            mRootView.findViewById(R.id.buttonsLOShareLO).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startActivity(Utils.createShareIntent(place,getActivity()));
                    } catch (ActivityNotFoundException e) {}
                }
            });

        } else {
            //if no specific place, dont show extra data
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

    //update the current location when changed
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

    //fill the datails of the place in the UI
    private void setDetails(String address, String phone,String  website, double rating) {
        if (address!=null)
            addressTV.setText(address);
        if (phone!=null) {
            phoneTV.setText(phone);
            phoneTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL );
                    intent.setData(Uri.parse("tel:"+phoneTV.getText()));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {}
                }
            });
            phoneIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL );
                    intent.setData(Uri.parse("tel:"+phoneTV.getText()));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {}
                }
            });
        }
        if (website!=null) {
            websiteTV.setText(website);
            websiteTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(""+websiteTV.getText()));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {}
                }
            });
            websiteIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(""+websiteTV.getText()));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {}
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

    //recieve details of place
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

            Place placeDetails  = intent.getParcelableExtra("place");
            String address = placeDetails.address;
            String phone = placeDetails.phone;
            String website = placeDetails.website;
            double rating = placeDetails.rating;

            //fill the details in the UI
            setDetails(address,phone,website,rating);
        }
    }



}
