package com.gali.apps.eifoyesh;

import android.os.Parcel;
import android.os.Parcelable;
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
    double rating;
    String phone;
    String website;
    String photoEncoded;

    public ResultItem() {
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

    public ResultItem(String address, String phone, String website, double rating) {
        this.address = address;
        this.phone = phone;
        this.website = website;
        this.rating = rating;
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
        rating = in.readDouble();
        phone = in.readString();
        website = in.readString();
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
        dest.writeDouble(rating);
        dest.writeString(phone);
        dest.writeString(website);
        dest.writeString(photoEncoded);
    }
}
