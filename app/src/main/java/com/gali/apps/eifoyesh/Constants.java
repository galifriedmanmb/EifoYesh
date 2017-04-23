package com.gali.apps.eifoyesh;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by 1 on 4/20/2017.
 */

public class Constants {

    //private static int key = 0;

    public static final String GOOGLE_PLACES_API_KEY = "AIzaSyBp--GgJR-PifaHqW47bLliMTJPTXoeh6A";
    //public static final String GOOGLE_PLACES_API_KEY1 = "AIzaSyAdl51G7iH0wF46jQGLsMoNXQeuYl6l5_A";
    //public static final String GOOGLE_PLACES_API_KEY2 = "AIzaSyClw2A3GuHtDpLgfrutpqQXTqGNltW5rWI";
    //public static final String GOOGLE_PLACES_API_KEY3 = "AIzaSyCgRHv2hyJ5NBHQAwv7VNcIaifRRzysTRw";

    public static final int SEARCH_TYPE_TEXT = 0;
    public static final int SEARCH_TYPE_NEAR_ME = 1;

    public static final int DB_PLACE_TYPE_LAST_SEARCH = 0;
    public static final int DB_PLACE_TYPE_FAVORITE = 1;

    //public static final String SHARED_PREFERENCES_UNIT = "unit";
    public static final String SHARED_PREFERENCES_UNIT_KM = "km";
    public static final String SHARED_PREFERENCES_UNIT_MILES = "miles";
/*
    protected static String getKey() {
        return "AIzaSyBp--GgJR-PifaHqW47bLliMTJPTXoeh6A";

        key++;
        if (key%3==1)
            return GOOGLE_PLACES_API_KEY1;
        if (key%3==2)
            return GOOGLE_PLACES_API_KEY2;
        return GOOGLE_PLACES_API_KEY3;

    }
    */

}
