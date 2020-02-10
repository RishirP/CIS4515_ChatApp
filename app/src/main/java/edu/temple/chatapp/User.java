package edu.temple.chatapp;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements Comparable {

    private String username;
    private Double latitude;
    private Double longitude;
    private LatLng position;
    private Location location;

    public User( String username, Double latitude, Double longitude){
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.setPosition( this.latitude, this.longitude);
        this.initializeLocation();
    }

    public User(JSONObject args) throws JSONException {
        this.username = args.getString("user");
        this.latitude = args.getDouble("latitude");
        this.longitude = args.getDouble("longitude");
        this.setPosition( this.latitude, this.longitude);
        this.initializeLocation();
    }

    private void initializeLocation(){
        this.location = new Location(LocationManager.GPS_PROVIDER);
        this.location.setLatitude( this.latitude );
        this.location.setLongitude( this.longitude );
    }

    private void setPosition(Double latitude, Double longitude){
        this.position = new LatLng( latitude, longitude );
    }

    public LatLng getPosition(){
        return this.position;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
