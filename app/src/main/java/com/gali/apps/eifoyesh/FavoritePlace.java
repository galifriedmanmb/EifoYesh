package com.gali.apps.eifoyesh;

/**
 * Used for saving favorite places with Sugar
 */

public class FavoritePlace extends Place {

    public FavoritePlace() {}

    public FavoritePlace(Place place) {
        this.name = place.name;
        this.address = place.address;
        this.lat = place.lat;
        this.lng = place.lng;
        this.number = place.number;
        this.distance = place.distance;
        this.distanceUnit = place.distanceUnit;
        this.iconUrl = place.iconUrl;
        this.placeId = place.placeId;
        this.rating = place.rating;
        this.phone = place.phone;
        this.website = place.website;
        this.photoEncoded = place.photoEncoded;

    }
}
