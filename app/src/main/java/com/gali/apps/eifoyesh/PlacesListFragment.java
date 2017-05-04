package com.gali.apps.eifoyesh;


import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class PlacesListFragment extends Fragment {

    protected View mRootView;
    int layoutId;
    ArrayList allPlaces;
    ContextMenuRecyclerView placesRV;

    private Location currentLocation;
    PlacesListAdapter adapter;

//    int currentPosition;
    FragmentChanger fragmentChanger;

    public PlacesListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentChanger = (FragmentChanger) getActivity();
        if(mRootView==null){
            mRootView = inflater.inflate(layoutId, container, false);
        }


        placesRV = (ContextMenuRecyclerView)mRootView.findViewById(R.id.placesRV);
        placesRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        adapter = new PlacesListAdapter(getActivity(), fragmentChanger, currentLocation, allPlaces);
        placesRV.setAdapter(adapter);
        registerForContextMenu(placesRV);

        if (savedInstanceState!=null) {
            ArrayList<ResultItem> newResults = savedInstanceState.getParcelableArrayList("allPlaces");
            allPlaces.clear();
            allPlaces.addAll(newResults);
            currentLocation = savedInstanceState.getParcelable("location");
            adapter.setCurrentLocation(currentLocation);
            adapter.notifyDataSetChanged();
        }
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("location",currentLocation);
        outState.putParcelableArrayList("allPlaces",allPlaces);
    }

    public void setCurrentLocation (Location currentLocation) {
        this.currentLocation = currentLocation;
        if (adapter!=null)
            adapter.setCurrentLocation(currentLocation);
    }


    public Location getCurrentLocation () {
        return currentLocation;
    }

}
