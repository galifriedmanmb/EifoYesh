package com.gali.apps.eifoyesh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * General Utilities
 */

public class Utils {

    /**
     * return true if has network, false if not
     */
    public static boolean isConnected(Context context)  {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean result = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        return result;
    }


    /**
     * get the distance of the place, in the relevant unit
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2, String unit) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        if (unit.equals(Constants.SHARED_PREFERENCES_UNIT_KM)) {
            return d;
        } else {
            return d / 1.61;
        }
    }

    public static String buildPlaceUrl(Place place) {
        return "https://maps.google.com/maps?placeid="+place.placeId;
    }

    public static String buildSearchByTextUrl(String query) {
        return "https://maps.googleapis.com/maps/api/place/textsearch/json?query="+query+"&key="+Constants.GOOGLE_PLACES_API_KEY;
    }

    public static String buildSearchNearMeUrl(String query, double lat, double lng, int radius, String unit) {
        int radiusInMeters = getRadiusInMeters(radius,unit);
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lng+"&radius="+radiusInMeters+"&keyword="+query+"&key="+Constants.GOOGLE_PLACES_API_KEY;
    }

    public static String buildPhotoUrl(String photoreference) {
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + Constants.PIC_MAX_WIDTH + "&photoreference=" + photoreference + "&key=" + Constants.GOOGLE_PLACES_API_KEY;
    }

    public static String buildGetPlaceDetailsUrl(String placeId) {
        return "https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeId+"&key="+Constants.GOOGLE_PLACES_API_KEY;
    }

    public static String encodeToBase64(Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input){
        byte[] decodedBytes = Base64.decode(input,0);
        return BitmapFactory.decodeByteArray(decodedBytes,0,decodedBytes.length);
    }

    private static int getRadiusInMeters (int radius, String unit) {
        if (unit.equals(Constants.SHARED_PREFERENCES_UNIT_KM))
            return radius * 1000;
        else
            return (int)(radius * 1000 * 1.61);
    }


    /**
     * save a place to favorites
     */
    public static void addToFavorites(Place place, Context context) {
        List<FavoritePlace> favorites = FavoritePlace.listAll(FavoritePlace.class);

        //dont save if already exists
        for (int i = 0; i < favorites.size() ; i++) {
            FavoritePlace favorite = favorites.get(i);
            if (favorite.placeId.equals(place.placeId)) {
                Toast.makeText(context,context.getResources().getText(R.string.placeAlreadyInFavorites).toString() , Toast.LENGTH_SHORT).show();
                return;
            }
        }
        FavoritePlace favoritePlace = new FavoritePlace(place);
        favoritePlace.save();
        Toast.makeText(context, context.getResources().getString(R.string.favoriteAdded), Toast.LENGTH_SHORT).show();
    }

    //to share place details
    public static Intent createShareIntent(Place place, Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");

        //send a link to place
        String urlPlace = Utils.buildPlaceUrl(place);
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.sharePlaceMessage)+"\n"+ urlPlace);
        return shareIntent;
    }
}
