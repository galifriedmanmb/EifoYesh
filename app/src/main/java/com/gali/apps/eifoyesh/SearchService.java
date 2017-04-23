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
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SearchService extends IntentService {

    public static final String INTENT_FILTER_FINISHED_SEARCH = "com.gali.apps.eifoyesh.FINISHED_SEARCH";

    private static final String ACTION_FIND_BY_TEXT = "com.gali.apps.eifoyesh.action.FIND_BY_TEXT";
    private static final String ACTION_FIND_NEAR_ME = "com.gali.apps.eifoyesh.action.FIND_NEAR_ME";

    private static final String EXTRA_SEARCH_TEXT = "com.gali.apps.eifoyesh.extra.SEARCH_TEXT";
    private static final String EXTRA_PIC_MAX_HEIGHT = "com.gali.apps.eifoyesh.extra.PIC_MAX_HEIGHT";
    private static final String EXTRA_LOCATION_LAT = "com.gali.apps.eifoyesh.extra.LOCATION_LAT";
    private static final String EXTRA_LOCATION_LNG = "com.gali.apps.eifoyesh.extra.LOCATION_LNG";

    public SearchService() {
        super("SearchService");
    }

    public static void startActionFindByText(Context context, String text, int picMaxHeight, Location location) {
        Intent intent = new Intent(context, SearchService.class);
        intent.setAction(ACTION_FIND_BY_TEXT);
        intent.putExtra(EXTRA_SEARCH_TEXT, text);
        intent.putExtra(EXTRA_PIC_MAX_HEIGHT, picMaxHeight);
        context.startService(intent);
    }

    public static void startActionFindNearMe(Context context, String text, int picMaxHeight, Location location) throws NullLocationException {
        if (location==null)
            throw new NullLocationException();
        Intent intent = new Intent(context, SearchService.class);
        intent.setAction(ACTION_FIND_NEAR_ME);
        intent.putExtra(EXTRA_SEARCH_TEXT, text);
        intent.putExtra(EXTRA_PIC_MAX_HEIGHT, picMaxHeight);
        intent.putExtra(EXTRA_LOCATION_LAT, location.getLatitude());
        intent.putExtra(EXTRA_LOCATION_LNG, location.getLongitude());

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FIND_BY_TEXT.equals(action)) {
                final String text = intent.getStringExtra(EXTRA_SEARCH_TEXT);
                final int picMaxHeight = intent.getIntExtra(EXTRA_PIC_MAX_HEIGHT,0);
                String query = text.replace(" ","+");
                String url="https://maps.googleapis.com/maps/api/place/textsearch/json?query="+query+"&key="+Constants.GOOGLE_PLACES_API_KEY;

                handleActionFind(url, picMaxHeight);
            } else if (ACTION_FIND_NEAR_ME.equals(action)) {
                final String text = intent.getStringExtra(EXTRA_SEARCH_TEXT);
                final int picMaxHeight = intent.getIntExtra(EXTRA_PIC_MAX_HEIGHT,0);
                final double lat = intent.getDoubleExtra(EXTRA_LOCATION_LAT,0);
                final double lng = intent.getDoubleExtra(EXTRA_LOCATION_LNG,0);
                String query = text.replace(" ","+");
                String url="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lng+"&radius=500&keyword="+query+"&key="+Constants.GOOGLE_PLACES_API_KEY;
                handleActionFind(url, picMaxHeight);
            }
        }
    }

    private void handleActionFind(String url, int picMaxHeight) {
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
//Log.d("****************response: ",jsonResponse);
            JSONObject top = new JSONObject(jsonResponse);
            status = top.getString("status");
            if (top.has("error_message"))
                error_message = top.getString("error_message");
            //if (status.equals("OVER_QUERY_LIMIT")) {

            //} else if (status.equals("OK")) {
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
                            iconUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + picMaxHeight + "&photoreference=" + icon + "&key=" + Constants.GOOGLE_PLACES_API_KEY;
                        }
                    } else {
                        Log.d("name", name);
                    }
                    String id = result.getString("place_id");
                    JSONObject geometry = result.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    //String iconUrl = "https://maps.googleapis.com/maps/api/place/photo?maxheight="+picMaxHeight+"&photoreference="+icon+"&key="+GOOGLE_PLACES_API_KEY;
                    ResultItem place = new ResultItem(name, address, i, id, iconUrl, lat, lng);
                    places.add(place);
                }
            //}

            Intent finishedSearchIntent = new Intent(INTENT_FILTER_FINISHED_SEARCH);
            finishedSearchIntent.putExtra("status", status);
            finishedSearchIntent.putExtra("error_message",error_message);
            finishedSearchIntent.putExtra("allresults", places);
            LocalBroadcastManager.getInstance(this).sendBroadcast(finishedSearchIntent);

        }catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }
}
