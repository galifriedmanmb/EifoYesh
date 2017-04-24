package com.gali.apps.eifoyesh;

/**
 * Created by 1 on 4/24/2017.
 */

public class FavoritePlace extends ResultItem {

    public FavoritePlace() {}

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
}
