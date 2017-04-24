package com.gali.apps.eifoyesh;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.orm.SugarRecord;

/**
 * Created by 1 on 4/19/2017.
 */

public class ResultItem extends SugarRecord implements Parcelable {

    String name;
    String address;
    double lat;
    double lng;
    int number;
    int distance;
    String distanceUnit;
    String iconUrl;
    String placeId;
    String photoEncoded;

    public ResultItem() {

    }

    public ResultItem(String name, String address, double lat, double lng, int number, int distance, String distanceUnit, String iconUrl, String placeId) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.number = number;
        this.distance = distance;
        this.distanceUnit = distanceUnit;
        this.iconUrl = iconUrl;
        this.placeId = placeId;

    }

    public ResultItem(String name, String address, int number, String placeId, String iconUrl, double lat, double lng) {
        this.name = name;
        this.address = address;
        this.number = number;
        this.iconUrl = iconUrl;
        this.placeId = placeId;
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
        placeId = in.readString();
        photoEncoded = in.readString();
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
        dest.writeString(placeId);
        dest.writeString(photoEncoded);
    }
}
