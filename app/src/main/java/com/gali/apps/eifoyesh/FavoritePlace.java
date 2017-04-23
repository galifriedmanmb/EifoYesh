package com.gali.apps.eifoyesh;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

/**
 * Created by 1 on 4/23/2017.
 */

public class FavoritePlace extends SugarRecord implements Parcelable {

    String name;
    String address;
    double lat;
    double lng;
    int number;
    int distance;
    String distanceUnit;
    String iconUrl;
    String placeId;
    public FavoritePlace() {}

    public FavoritePlace(String name, String address, double lat, double lng, int number, int distance, String distanceUnit, String iconUrl, String placeId) {
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

    public FavoritePlace(String name, String address, int number, String placeId, String iconUrl, double lat, double lng) {
        this.name = name;
        this.address = address;
        this.number = number;
        this.iconUrl = iconUrl;
        this.placeId = placeId;
        this.lat = lat;
        this.lng = lng;
    }

    public FavoritePlace(ResultItem resultItem) {
        this.name = resultItem.name;
        this.address = resultItem.address;
        this.lat = resultItem.lat;
        this.lng = resultItem.lng;
        this.number = resultItem.number;
        this.distance = resultItem.distance;
        this.distanceUnit = resultItem.distanceUnit;
        this.iconUrl = resultItem.iconUrl;
        this.placeId = resultItem.placeId;

    }

    protected FavoritePlace(Parcel in) {
        name = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        number = in.readInt();
        distance = in.readInt();
        distanceUnit = in.readString();
        iconUrl = in.readString();
        placeId = in.readString();
    }

    public static final Creator<FavoritePlace> CREATOR = new Creator<FavoritePlace>() {
        @Override
        public FavoritePlace createFromParcel(Parcel in) {
            return new FavoritePlace(in);
        }

        @Override
        public FavoritePlace[] newArray(int size) {
            return new FavoritePlace[size];
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
    }
}
