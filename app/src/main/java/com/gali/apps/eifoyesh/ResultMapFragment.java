package com.gali.apps.eifoyesh;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultMapFragment extends Fragment {

    private View mRootView;
    ResultItem resultItem;

    public ResultMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(mRootView==null){
            mRootView = inflater.inflate(R.layout.fragment_result_map, container, false);
        }
        //View view = inflater.inflate(R.layout.fragment_result_map, container, false);
        if (savedInstanceState!=null) {
            resultItem = savedInstanceState.getParcelable("resultItem");
        }
        final double lat = resultItem.lat;
        final double lng = resultItem.lng;
        MapFragment mapFragment = new MapFragment();
        getFragmentManager().beginTransaction().replace(R.id.mapFragmentContainer,mapFragment).commit();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                LatLng latLng = new LatLng(lat,lng);
                CameraUpdate update= CameraUpdateFactory.newLatLngZoom(latLng, 15);
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                googleMap.moveCamera(update);
            }
        });
        return mRootView;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("resultItem",resultItem);
    }

}