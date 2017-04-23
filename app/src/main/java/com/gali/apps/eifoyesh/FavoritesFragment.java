package com.gali.apps.eifoyesh;


import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {

    private View mRootView;
    //ArrayList<ResultItem> allFavoriteItems;
    ArrayList<FavoriyePlace2> allFavoritePlaces;
    RecyclerView favoritesRV;

    Location currentLocation;
    //FavoritesListAdapter adapter;
    PlacesListAdapter adapter;

    int currentPosition;
    FragmentChanger fragmentChanger;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentChanger = (FragmentChanger) getActivity();
        if(mRootView==null){
            mRootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        }

        allFavoritePlaces = (ArrayList<FavoriyePlace2>)FavoriyePlace2.listAll(FavoriyePlace2.class);

        favoritesRV = (RecyclerView)mRootView.findViewById(R.id.favoritesRV);
        favoritesRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));
        //adapter = new FavoritesListAdapter(getActivity(), fragmentChanger, currentLocation, allFavoritePlaces);
        adapter = new PlacesListAdapter(getActivity(), fragmentChanger, currentLocation, allFavoritePlaces);
        favoritesRV.setAdapter(adapter);
        registerForContextMenu(favoritesRV);

        return mRootView;



    }
/*
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //to get the positon on the list
        currentPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        getActivity().getMenuInflater().inflate(R.menu.single_place_context_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        FavoritePlace place = allFavoritePlaces.get(currentPosition);
        switch (item.getItemId()) {
            case R.id.deleteMI:
                FavoritePlace favorite = (FavoritePlace)place;
                favorite.delete();
                allFavoritePlaces = (ArrayList<FavoritePlace>)FavoritePlace.listAll(FavoritePlace.class);
                adapter.notifyDataSetChanged();
                break;
        }
        return  true;
    }

*/

}
