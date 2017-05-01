package com.gali.apps.eifoyesh;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    public int getAdapterPosition() {
        return getAdapterPosition();
    }

    public PlacesListAdapter(Context c, FragmentChanger fragmentChanger, Location currentLocation, ArrayList<T> allPlaces) {
        this.c = c;
        this.fragmentChanger = fragmentChanger;
        this.currentLocation = currentLocation;
        this.allPlaces = allPlaces;
    }

    public PlacesListAdapter.PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View singleview = LayoutInflater.from(c).inflate(R.layout.search_result_item, parent,false);
//        View singleview = LayoutInflater.from(c).inflate(R.layout.test, parent,false);
        PlaceViewHolder singleResultVH = new PlaceViewHolder(singleview);
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
                numberTV.setText("" + resultItem.number);

                if (currentLocation != null) {
                    //String unit = prefs.getString(Constants.SHARED_PREFERENCES_UNIT, Constants.SHARED_PREFERENCES_UNIT_KM);
                    String unit = PreferenceManager.getDefaultSharedPreferences(c).getString("distance_units", Constants.SHARED_PREFERENCES_UNIT_KM);
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
                        fragmentChanger.changeFragmentsToMap(resultItem);
                    }
                });

                //get photo
                if (resultItem.photoEncoded != null) {//from db
                    Bitmap photoBM = Utils.decodeBase64(resultItem.photoEncoded);
                    iconIV.setImageBitmap(photoBM);
                    //resultItem.save();
                } else {
                    Picasso.with(c).load(resultItem.iconUrl).into(iconIV, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) iconIV.getDrawable()).getBitmap();
                            resultItem.photoEncoded = Utils.encodeToBase64(bitmap);
                            //save the photo when it arrives
                            resultItem.save();
                        }
                        @Override
                        public void onError() {
                            int i=0;
                        }
                    });
                }
            }


        }

}
