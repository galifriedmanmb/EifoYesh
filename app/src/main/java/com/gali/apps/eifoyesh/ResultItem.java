package com.gali.apps.eifoyesh;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 1 on 4/19/2017.
 */

public class ResultItem implements Parcelable {



    String name;
    String address;
    double lat;
    double lng;
    int number;
    int distance;
    String distanceUnit;
    String iconUrl;
    String id;


    public ResultItem(String name, String address, int number, String id, String iconUrl, double lat, double lng) {
        this.name = name;
        this.address = address;
        this.number = number;
        this.iconUrl = iconUrl;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }


    protected ResultItem(Parcel in) {
        name = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        number = in.readInt();
        distance = in.readInt();
        distanceUnit = in.readString();
        iconUrl = in.readString();
        id = in.readString();
    }

    public static final Creator<ResultItem> CREATOR = new Creator<ResultItem>() {
        @Override
        public ResultItem createFromParcel(Parcel in) {
            return new ResultItem(in);
        }

        @Override
        public ResultItem[] newArray(int size) {
            return new ResultItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeInt(number);
        dest.writeInt(distance);
        dest.writeString(distanceUnit);
        dest.writeString(iconUrl);
        dest.writeString(id);
    }

    public static double getDistance(double lat1, double lng1, double lat2, double lng2, int unit) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        if (unit == Constants.SHARED_PREFERENCES_UNIT_KM)
            return d;
        else
            return d/1.61;
    }
}
