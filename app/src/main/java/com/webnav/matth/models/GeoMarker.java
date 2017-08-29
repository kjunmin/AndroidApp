package com.webnav.matth.models;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.webnav.matth.login.Config;
import com.webnav.matth.login.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matth on 28/8/2017.
 */

public class GeoMarker {
    private String markerAddress;
    private String markerLabel;
    private Double markerLat;
    private Double markerLng;
    private String markerOwner;


    public GeoMarker(String markerAddress, String markerLabel, Double markerLat, Double markerLng, String markerOwner) {
        this.markerAddress = markerAddress;
        this.markerLabel = markerLabel;
        this.markerLat = markerLat;
        this.markerLng = markerLng;
        this.markerOwner = markerOwner;
    }

    public GeoMarker(JSONArray jsonMarkerArray, int arrayIndex, String markerOwner) {
        try {
            this.markerLabel = jsonMarkerArray.getJSONObject(arrayIndex).getString("label");
            this.markerAddress = jsonMarkerArray.getJSONObject(arrayIndex).getString("address");
            this.markerLat = jsonMarkerArray.getJSONObject(arrayIndex).getDouble("latitude");
            this.markerLng = jsonMarkerArray.getJSONObject(arrayIndex).getDouble("longitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int hashCode(){
        return markerLabel.hashCode()*markerAddress.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof GeoMarker)) {
            return false;
        }

        GeoMarker other = (GeoMarker) obj;
        return this.markerLabel.equals(other.markerLabel);
    }

    public GeoMarker getMarker(JSONArray jsonMarkerArray, int arrayIndex, String markerOwner) {
        GeoMarker marker = new GeoMarker(jsonMarkerArray, arrayIndex, markerOwner);
        return marker;
    }

    public void addMarker(final Context context) {
        //TODO: Implement method
        Map<String, String> params = new HashMap<>();
        params.put("label", this.markerLabel);
        params.put("address", this.markerAddress);
        params.put("latitude", this.markerLat.toString());
        params.put("longitude", this.markerLng.toString());
        params.put("username", this.markerOwner);

        final String ADD_MARKER_URL = Config.DEV_ADD_MARKERS_URL;

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                boolean success = false;
                String output = "Failed to add GeoMarker!";
                try {
                    success = response.getBoolean("success");
                    output = response.getString("output");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (success) {
                    Toast.makeText(context, output, Toast.LENGTH_SHORT).show();
                } else  {
                    Toast.makeText(context, output, Toast.LENGTH_SHORT).show();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error+"", Toast.LENGTH_SHORT).show();
            }
        };

        RequestHandler requestHandler = new RequestHandler(ADD_MARKER_URL, new JSONObject(params), listener, errorListener);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(requestHandler);

    }

    public String getMarkerAddress() {
        return this.markerAddress;
    }

    public String getMarkerLabel() {
        return this.markerLabel;
    }

    public Double getMarkerLat() {
        return this.markerLat;
    }

    public Double getMarkerLng() {
        return this.markerLng;
    }
}
