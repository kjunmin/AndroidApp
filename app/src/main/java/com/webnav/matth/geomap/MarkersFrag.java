package com.webnav.matth.geomap;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.webnav.matth.R;
import com.webnav.matth.login.Config;
import com.webnav.matth.login.RequestHandler;
import com.webnav.matth.models.GeoMarker;
import com.webnav.matth.models.LocalStorageHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matth on 25/8/2017.
 */

public class MarkersFrag extends Fragment {
    ExpandableListAdapter expandableListAdapter;


    IntentFilter filter = new IntentFilter("location_update");
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Double locationLat = (Double) intent.getExtras().get("location_latitude");
            Double locationLng = (Double) intent.getExtras().get("location_longitude");
            LatLng locationLatLng = new LatLng(locationLat, locationLng);
            if (expandableListAdapter != null) {
                updateAdapter(expandableListAdapter, locationLatLng);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_markers_frag, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestUserData(null);
    }

    private void requestUserData(final LatLng latLng) {
        LocalStorageHandler userStorage = new LocalStorageHandler();
        String username = userStorage.getUsername(getActivity());
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
                        displayMarkerArray(markerArray, markerOwner, latLng);
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

    private HashMap<GeoMarker, List<GeoMarker>> createMarkerHashMap(JSONArray array, String username) {
        HashMap<GeoMarker, List<GeoMarker>> markerHashMap =
                new HashMap<GeoMarker, List<GeoMarker>>();
        for (int i = 0; i< array.length(); i++) {
            List<GeoMarker> m = new ArrayList<GeoMarker>();
            GeoMarker marker = new GeoMarker(array, i, username);
            m.add(marker);
            markerHashMap.put(marker, m);
        }
        return markerHashMap;
    }

    private List<GeoMarker> createMarkerlist(JSONArray array, String username) {
        List<GeoMarker> markerList = new ArrayList<GeoMarker>();
        for (int i = 0; i< array.length(); i++) {
            GeoMarker marker = new GeoMarker(array, i, username);
            markerList.add(marker);
        }
        return markerList;
    }

    private void updateAdapter(ExpandableListAdapter expandableListAdapter, LatLng latLng) {
        expandableListAdapter.setLocationLatLng(latLng);
        expandableListAdapter.notifyDataSetChanged();
    }

    private void displayMarkerArray(JSONArray array, String username, LatLng locationLatLng) {
        List<GeoMarker> groupList = createMarkerlist(array, username);
        HashMap<GeoMarker, List<GeoMarker>> markerCollection = createMarkerHashMap(array, username);
        ExpandableListView expListView = (ExpandableListView) getView().findViewById(R.id.list);
        ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                getActivity(), groupList, markerCollection, locationLatLng);
        this.expandableListAdapter = expListAdapter;
        expListView.setAdapter(expListAdapter);
    }
}
