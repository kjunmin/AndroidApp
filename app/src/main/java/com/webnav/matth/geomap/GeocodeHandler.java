package com.webnav.matth.geomap;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.webnav.matth.login.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matth on 28/8/2017.
 */

public class GeocodeHandler {
    final String GEOCODE_REQUEST_URL = Config.GEOCODE_REQUEST_URL;
    final String REVERSE_GEOCODE_REQUEST_URL = Config.REVERSE_GEOCODE_REQUEST_URL;
    Context context;
    GoogleMap mGoogleMap;

    public GeocodeHandler(Context context, GoogleMap mGoogleMap) {
        this.context = context;
        this.mGoogleMap = mGoogleMap;
    }


    public void getLatLngFromName(String location, final AsyncResponse callback) {
        String searchLocation = GEOCODE_REQUEST_URL + location;
        Response.Listener<String> listener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray streetName = null;
                JSONObject tmpObj = null;
                LatLng latLng = null;
                try {
                    streetName = new JSONObject(response).getJSONArray("results");
                    tmpObj = streetName.getJSONObject(0);
                    double latitude = tmpObj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    double longitude = tmpObj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    latLng = new LatLng(latitude, longitude);
                    String formattedAddress = tmpObj.getString("formatted_address");
                    callback.processFinish(formattedAddress, latLng);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.GET, searchLocation, listener, null);
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        requestQueue.add(stringRequest);
    }

    public void getLocationFromLatLng(LatLng latLng, final AsyncResponse callback) {
        String searchLocation = REVERSE_GEOCODE_REQUEST_URL + latLng.latitude + "," + latLng.longitude;
        Response.Listener<String> listener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONArray streetName = null;
                JSONObject tmpObj = null;

                LatLng latLng = null;
                try {
                    streetName = new JSONObject(response).getJSONArray("results");
                    tmpObj = streetName.getJSONObject(0);
                    double latitude = tmpObj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    double longitude = tmpObj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    latLng = new LatLng(latitude, longitude);
                    String formattedAddress = tmpObj.getString("formatted_address");
                    callback.processFinish(formattedAddress, latLng);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.GET, searchLocation, listener, null);
        RequestQueue requestQueue = Volley.newRequestQueue(this.context);
        requestQueue.add(stringRequest);
    }
}
