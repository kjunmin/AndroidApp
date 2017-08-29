package com.webnav.matth.geomap;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.webnav.matth.R;
import com.webnav.matth.login.Config;
import com.webnav.matth.login.RequestHandler;
import com.webnav.matth.models.GeoMarker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matth on 25/8/2017.
 */

public class MarkersFrag extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_markers_frag, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        JSONObject user = getUserInfo();
        String username = null;
        try {
            username = user.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (username != null) {
            String GET_MARKERS_URI = Config.DEV_GET_MARKERS_URL;
            Map<String, String> params = new HashMap<>();
            params.put("username", username);
            final String markerOwner = username;
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    boolean success = false;
                    JSONArray markerArray = new JSONArray();
                    try {
                        success = response.getBoolean("success");
                        markerArray = response.getJSONArray("output");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (success) {
                        displayMarkerArray(markerArray, markerOwner);
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Error retrieving marker data", Toast.LENGTH_SHORT).show();
                }
            };

            RequestHandler requestHandler = new RequestHandler(GET_MARKERS_URI, new JSONObject(params), listener, errorListener);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(requestHandler);
        }


    }

    private  HashMap<GeoMarker, List<GeoMarker>> createMarkerHashmap(JSONArray array, String username) {
        HashMap<GeoMarker, List<GeoMarker>> valuesList =
                new HashMap<GeoMarker, List<GeoMarker>>();
        for (int i = 0; i< array.length(); i++) {
            List<GeoMarker> m = new ArrayList<GeoMarker>();
            GeoMarker marker = new GeoMarker(array, i, username);
            m.add(marker);
            valuesList.put(marker, m);
        }
        return valuesList;
    }
    private  List<GeoMarker> createMarkerlist(JSONArray array, String username) {
        List<GeoMarker> m = new ArrayList<GeoMarker>();
        for (int i = 0; i< array.length(); i++) {

            GeoMarker marker = new GeoMarker(array, i, username);
            m.add(marker);
        }
        return m;
    }

    private void displayMarkerArray(JSONArray array, String username) {
        List<GeoMarker> groupList = createMarkerlist(array, username);
        HashMap<GeoMarker, List<GeoMarker>> markerCollection = createMarkerHashmap(array, username);
        ExpandableListView expListView;
        expListView = (ExpandableListView) getView().findViewById(R.id.list);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                getActivity(), groupList, markerCollection);

        expListView.setAdapter(expListAdapter);
    }

    public JSONObject getUserInfo() {
        //Retrieve Profile Information from shared preferences
        SharedPreferences prefs = getActivity().getSharedPreferences(Config.PREF_FILE_NAME, Context.MODE_PRIVATE);
        JSONObject jsonUser = new JSONObject();
        if (prefs != null) {
            String user = prefs.getString("user", null);
            try {
                jsonUser = new JSONObject(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonUser;
    }
}
