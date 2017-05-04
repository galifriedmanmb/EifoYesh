package com.gali.apps.eifoyesh;


import android.os.Bundle;
import android.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;

public class FavoritesListFragment extends PlacesListFragment {

    public FavoritesListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layoutId = R.layout.fragment_favorites_list;
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
                allPlaces = (ArrayList<FavoritePlace>) FavoritePlace.listAll(FavoritePlace.class);
                adapter = new PlacesListAdapter(getActivity(), fragmentChanger, getCurrentLocation(), allPlaces);
                placesRV.setAdapter(adapter);
                break;
        }
        return  true;
    }


}
