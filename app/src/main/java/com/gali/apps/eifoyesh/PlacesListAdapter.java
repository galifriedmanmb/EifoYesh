package com.gali.apps.eifoyesh;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by 1 on 4/23/2017.
 */

public class PlacesListAdapter<T extends ResultItem> extends RecyclerView.Adapter<PlacesListAdapter.PlaceViewHolder> {

    Context c;
    FragmentChanger fragmentChanger;
    Location currentLocation;
    ArrayList<T> allPlaces;
    int type;

    //public SearchListAdapter(ArrayList<ResultItem> allResults, Context c, FragmnetChanger fragmnetChanger) {
    public PlacesListAdapter(Context c, FragmentChanger fragmentChanger, Location currentLocation, ArrayList<T> allPlaces) {
        this.c = c;
        this.fragmentChanger = fragmentChanger;
        this.currentLocation = currentLocation;
        this.allPlaces = allPlaces;
    }

    public PlacesListAdapter.PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View singleview = LayoutInflater.from(c).inflate(R.layout.search_result_item, null);
        PlaceViewHolder singleResultVH= new PlaceViewHolder(singleview);
        return singleResultVH;
    }
    @Override
    public int getItemCount() {
        return allPlaces.size();
    }

    public void onBindViewHolder(PlacesListAdapter.PlaceViewHolder holder, int position) {
        holder.itemView.setLongClickable(true);
        ResultItem resultItem = allPlaces.get(position);
        holder.bindData(resultItem);
    }

        class PlaceViewHolder extends RecyclerView.ViewHolder {

            TextView nameTV;
            TextView addressTV;
            TextView numberTV;
            TextView distanceTV;
            ImageView iconIV;
            LinearLayout layout;


            public PlaceViewHolder(View itemView) {
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
                    String unit = PreferenceManager.getDefaultSharedPreferences(c).getString("distance_units",Constants.SHARED_PREFERENCES_UNIT_KM);
                    double distance = Utils.getDistance(resultItem.lat, resultItem.lng, currentLocation.getLatitude(), currentLocation.getLongitude(), unit);
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
                        fragmentChanger.changeFragments(resultItem);
                    }
                });


                Picasso.with(c).load(resultItem.iconUrl).into(iconIV);

            }
        }

}
