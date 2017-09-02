package com.webnav.matth.login;

/**
 * Created by matth on 26/8/2017.
 */

public class Config {
    public static final String DEV_REGISTER_URL = "https://dry-bastion-37261.herokuapp.com/users/register";
    public static final String DEV_AUTHENTICATE_URL = "https://dry-bastion-37261.herokuapp.com/users/authenticate";
    public static final String DEV_GET_MARKERS_URL = "https://dry-bastion-37261.herokuapp.com/users/getMarkers";
    public static final String DEV_ADD_MARKERS_URL = "https://dry-bastion-37261.herokuapp.com/users/addMarker";
    public static final String DEV_DEL_MARKERS_URL = "https://dry-bastion-37261.herokuapp.com/users/delMarker";
    public static final String GEOCODE_REQUEST_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    public static final String REVERSE_GEOCODE_REQUEST_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    public static final int MAP_ZOOM = 16;
    public static final int MAP_BEARING = 0;
    public static final int MAP_TILT = 45;
    public static final String PREF_FILE_NAME = "Preferences";

}
