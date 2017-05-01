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
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesListFragment extends PlacesListFragment {
/*
    private View mRootView;
    //ArrayList<ResultItem> allFavoriteItems;
    ArrayList<FavoritePlace> allFavoritePlaces;
    RecyclerView favoritesRV;

    Location currentLocation;
    //FavoritesListAdapter adapter;
    PlacesListAdapter adapter;

    int currentPosition;
    FragmentChanger fragmentChanger;
*/
    public FavoritesListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layoutId = R.layout.fragment_favorites;
        allPlaces = (ArrayList<FavoritePlace>) FavoritePlace.listAll(FavoritePlace.class);
        mRootView = super.onCreateView(inflater,container,savedInstanceState);
        return mRootView;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Inflate Menu from xml resource
        getActivity().getMenuInflater().inflate(R.menu.single_favorite_context_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerContextMenuInfo info = (ContextMenuRecyclerView.RecyclerContextMenuInfo) item.getMenuInfo();
        //Toast.makeText(getActivity() , " User selected  " + info.position, Toast.LENGTH_LONG).show();

        FavoritePlace place = (FavoritePlace) allPlaces.get(info.position);
        switch (item.getItemId()) {
            case R.id.deleteFavoriteMI:
                place.delete();
                Toast.makeText(getActivity(), getResources().getString(R.string.favoriteDeleted), Toast.LENGTH_SHORT).show();
                break;
        }
        return  true;
    }


}
