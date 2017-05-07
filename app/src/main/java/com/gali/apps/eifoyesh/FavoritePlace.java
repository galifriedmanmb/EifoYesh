package com.gali.apps.eifoyesh;

/**
 * Used for saving favorite places with Sugar
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
        this.rating = resultItem.rating;
        this.phone = resultItem.phone;
        this.website = resultItem.website;
        this.photoEncoded = resultItem.photoEncoded;

    }
}
