package com.gali.apps.eifoyesh;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.gali.apps.eifoyesh.exceptions.NullLocationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SearchService extends IntentService {

    public static final String INTENT_FILTER_FINISHED_SEARCH = "com.gali.apps.eifoyesh.FINISHED_SEARCH";
    public static final String INTENT_FILTER_FINISHED_DETAILS = "com.gali.apps.eifoyesh.FINISHED_DETAILS";

    private static final String ACTION_FIND_BY_TEXT = "com.gali.apps.eifoyesh.action.FIND_BY_TEXT";
    private static final String ACTION_FIND_NEAR_ME = "com.gali.apps.eifoyesh.action.FIND_NEAR_ME";
    private static final String ACTION_GET_PLACE_DETAILS = "com.gali.apps.eifoyesh.action.GET_PLACE_DETAILS";

    private static final String EXTRA_SEARCH_TEXT = "com.gali.apps.eifoyesh.extra.SEARCH_TEXT";
    //private static final String EXTRA_PIC_MAX_HEIGHT = "com.gali.apps.eifoyesh.extra.PIC_MAX_HEIGHT";
    private static final String EXTRA_LOCATION_LAT = "com.gali.apps.eifoyesh.extra.LOCATION_LAT";
    private static final String EXTRA_LOCATION_LNG = "com.gali.apps.eifoyesh.extra.LOCATION_LNG";
    private static final String EXTRA_RADIUS = "com.gali.apps.eifoyesh.extra.EXTRA_RADIUS";
    private static final String EXTRA_UNIT = "com.gali.apps.eifoyesh.extra.UNIT";
    private static final String EXTRA_PLACE_ID = "com.gali.apps.eifoyesh.extra.PLACE_ID";


    public SearchService() {
        super("SearchService");
    }

    public static void startActionGetPlaceDetails(Context context, String placeId) {
        Intent intent = new Intent(context, SearchService.class);
        intent.setAction(ACTION_GET_PLACE_DETAILS);
        intent.putExtra(EXTRA_PLACE_ID, placeId);
        context.startService(intent);
    }

    public static void startActionFindByText(Context context, String text, Location location) {
        Intent intent = new Intent(context, SearchService.class);
        intent.setAction(ACTION_FIND_BY_TEXT);
        intent.putExtra(EXTRA_SEARCH_TEXT, text);
        context.startService(intent);
    }

    public static void startActionFindNearMe(Context context, String text, Location location, int radius, String unit) throws NullLocationException {
        if (location==null)
            throw new NullLocationException();
        Intent intent = new Intent(context, SearchService.class);
        intent.setAction(ACTION_FIND_NEAR_ME);
        intent.putExtra(EXTRA_SEARCH_TEXT, text);
        intent.putExtra(EXTRA_LOCATION_LAT, location.getLatitude());
        intent.putExtra(EXTRA_LOCATION_LNG, location.getLongitude());
        intent.putExtra(EXTRA_RADIUS, radius);
        intent.putExtra(EXTRA_UNIT, unit);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FIND_BY_TEXT.equals(action)) {
                final String text = intent.getStringExtra(EXTRA_SEARCH_TEXT);
                String query = "";
                try {
                    query = URLEncoder.encode(text, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    query = text.replace(" ","+");
                }
                String url = Utils.buildSearchByTextUrl(query);
                handleActionFind(url, text, Constants.SEARCH_TYPE_TEXT);
            } else if (ACTION_FIND_NEAR_ME.equals(action)) {
                final String text = intent.getStringExtra(EXTRA_SEARCH_TEXT);
                String query = null;// text.replace(" ","+");
                try {
                    query = URLEncoder.encode(text, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    query = text.replace(" ","+");
                }
                final double lat = intent.getDoubleExtra(EXTRA_LOCATION_LAT,0);
                final double lng = intent.getDoubleExtra(EXTRA_LOCATION_LNG,0);
                final int radius = intent.getIntExtra(EXTRA_RADIUS,0);
                final String unit = intent.getStringExtra(EXTRA_UNIT);
                String url = Utils.buildSearchNearMeUrl(query,lat,lng,radius,unit);
                handleActionFind(url, text, Constants.SEARCH_TYPE_NEAR_ME);
            } else if (ACTION_GET_PLACE_DETAILS.equals(action)) {
                final String placeID = intent.getStringExtra(EXTRA_PLACE_ID);
                String url = Utils.buildGetPlaceDetailsUrl(placeID);
                handleActionGetPlaceDetails(url);
            }
        }
    }

    private void handleActionFind(String url, String text, int type) {
//Log.d("****************url: ",url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        String jsonResponse="";
        String status = "";
        String error_message = null;
        try {
            ArrayList<ResultItem> places = new ArrayList<>();
            client.newCall(request).execute();
            Response response = client.newCall(request).execute();
            jsonResponse = response.body().string();
            JSONObject top = new JSONObject(jsonResponse);
            status = top.getString("status");
            if (top.has("error_message"))
                error_message = top.getString("error_message");
            if (status.equals("OVER_QUERY_LIMIT")) {

            } else if (status.equals("OK")) {
                JSONArray results = top.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    String address = null;
                    if (result.has("formatted_address"))
                        address = result.getString("formatted_address");
                    else
                        address = result.getString("vicinity");
                    String name = result.getString("name");
                    String iconUrl = null;
                    if (result.has("photos")) {
                        JSONArray photos = result.getJSONArray("photos");
                        if (photos != null && photos.length() > 0) {
                            JSONObject photo = photos.getJSONObject(0);
                            String icon = photo.getString("photo_reference");
                            iconUrl = Utils.buildPhotoUrl(icon);
                        }
                    } else {
                        Log.d("name", name);
                    }
                    String id = result.getString("place_id");
                    JSONObject geometry = result.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    ResultItem place = new ResultItem(name, address, i, id, iconUrl, lat, lng);
                    places.add(place);
                }
            }

            Intent finishedSearchIntent = new Intent(INTENT_FILTER_FINISHED_SEARCH);
            finishedSearchIntent.putExtra("status", status);
            finishedSearchIntent.putExtra("error_message", error_message);
            finishedSearchIntent.putExtra("allresults", places);
            finishedSearchIntent.putExtra("search", text);
            finishedSearchIntent.putExtra("type", type);
            LocalBroadcastManager.getInstance(this).sendBroadcast(finishedSearchIntent);

        }catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    private void handleActionGetPlaceDetails(String url) {
//        Log.d("****************url: ",url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        String jsonResponse="";
        String status = "";
        String error_message = null;
        try {
            ResultItem place = null;
            client.newCall(request).execute();
            Response response = client.newCall(request).execute();
            jsonResponse = response.body().string();
            JSONObject top = new JSONObject(jsonResponse);
            status = top.getString("status");
            if (top.has("error_message"))
                error_message = top.getString("error_message");
            if (status.equals("OVER_QUERY_LIMIT")) {

            } else if (status.equals("OK")) {
                JSONObject result = top.getJSONObject("result");
                String address = null;
                if (result.has("formatted_address"))
                    address = result.getString("formatted_address");

                String phone = null;
                if (result.has("international_phone_number"))
                    phone = result.getString("international_phone_number");

                String website = null;
                if (result.has("website"))
                    website = result.getString("website");

                double rating = 0;
                if (result.has("rating")) {
                    rating = result.getDouble("rating");
                }

                place = new ResultItem(address, phone, website, rating);
            }

            Intent finishedSearchIntent = new Intent(INTENT_FILTER_FINISHED_DETAILS);
            finishedSearchIntent.putExtra("status", status);
            finishedSearchIntent.putExtra("error_message", error_message);
            finishedSearchIntent.putExtra("place", place);
            LocalBroadcastManager.getInstance(this).sendBroadcast(finishedSearchIntent);

        }catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }
}
