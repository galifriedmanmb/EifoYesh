package com.gali.apps.eifoyesh;


import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

/**
 * General Fragment for List of Places
 * 1. contains a RecyclerView of places
 * 2. maintains current location
 * 3. holds reference to fragmentChanger (when a place is clicked, handle change of map fragment accordingly)
 */
public class PlacesListFragment extends Fragment {

    protected View mRootView;
    int layoutId;
    private Location currentLocation;
    ArrayList allPlaces;
    ContextMenuRecyclerView placesRV;
    PlacesListAdapter adapter;
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
            //load allPlaces from the savedInstanceState, and retrieve the currentLocation
            ArrayList<Place> newResults = savedInstanceState.getParcelableArrayList("allPlaces");
            if (newResults!=null) {
                allPlaces.clear();
                allPlaces.addAll(newResults);
                currentLocation = savedInstanceState.getParcelable("location");
                adapter.setCurrentLocation(currentLocation);
                adapter.notifyDataSetChanged();
            }
        }
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save allPlaces and the currentLocation
        outState.putParcelable("location",currentLocation);
        outState.putParcelableArrayList("allPlaces",allPlaces);
    }

    //update the current location when it is changed
    public void setCurrentLocation (Location currentLocation) {
        this.currentLocation = currentLocation;
        if (adapter!=null)
            adapter.setCurrentLocation(currentLocation);
    }


    public Location getCurrentLocation () {
        return currentLocation;
    }

}
