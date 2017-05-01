package com.gali.apps.eifoyesh;


import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesListFragment extends Fragment {

    protected View mRootView;
    int layoutId;
    ArrayList allPlaces;
    ContextMenuRecyclerView placesRV;

    Location currentLocation;
    PlacesListAdapter adapter;

    int currentPosition;
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
            adapter.notifyDataSetChanged();
        }
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("allPlaces",allPlaces);
    }


}
