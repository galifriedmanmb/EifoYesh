package com.gali.apps.eifoyesh;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by 1 on 4/23/2017.
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



    public static double getDistance(double lat1, double lng1, double lat2, double lng2, String unit) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        if (unit.equals(Constants.SHARED_PREFERENCES_UNIT_KM))
            return d;
        else
            return d/1.61;
    }

    public static String buildPlaceUrl(ResultItem place) {
        return "https://maps.google.com/maps?placeid="+place.placeId;
    }

    public static String buildSearchByTextUrl(String query) {
        return "https://maps.googleapis.com/maps/api/place/textsearch/json?query="+query+"&key="+Constants.GOOGLE_PLACES_API_KEY;
    }

    public static String buildSearchNearMeUrl(String query, double lat, double lng) {
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lng+"&radius=500&keyword="+query+"&key="+Constants.GOOGLE_PLACES_API_KEY;
    }

    public static String buildPhotoUrl(String photoreference) {
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + Constants.PIC_MAX_WIDTH + "&photoreference=" + photoreference + "&key=" + Constants.GOOGLE_PLACES_API_KEY;
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOutputStream);
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
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


}
