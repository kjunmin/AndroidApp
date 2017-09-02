package com.webnav.matth.geomap;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.webnav.matth.R;
import com.webnav.matth.login.Config;
import com.webnav.matth.login.RequestHandler;
import com.webnav.matth.models.GeoMarker;
import com.webnav.matth.models.LocalStorageHandler;
import com.webnav.matth.models.MarkerCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by matth on 25/8/2017.
 */

public class MapFrag extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 122;
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    private GoogleApiClient googleApiClient;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_map_frag, container, false);
        return mView;
    }

//    public boolean isOnline() {
//        ConnectivityManager cm =
//                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        return netInfo != null && netInfo.isConnectedOrConnecting();
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        // Location searchbar
        final EditText etLocationSearch = (EditText) view.findViewById(R.id.etLocationSearch);
        Button btnLocationSearch = (Button) view.findViewById(R.id.btnLocationSearch);

        btnLocationSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Handle button click and geocode string to retrieve location
                String location = etLocationSearch.getText().toString();
                if (location != null || !location.equals("")) {

                    GeocodeHandler geocodeHandler = new GeocodeHandler(getActivity(), mGoogleMap);
                    geocodeHandler.getLatLngFromName(location, new AsyncResponse() {

                        @Override
                        public void processFinish(final String response, final LatLng response2) {
                            addMarkerToMap(response2, mGoogleMap, "Marker");
                            centerCameraOnLatLng(response2, mGoogleMap);
                            Snackbar snackbar = Snackbar
                                    .make(getView(), response, Snackbar.LENGTH_LONG)
                                    .setAction("Add Marker", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            showMarkerDialog(getActivity(), response2, response);
                                        }
                                    });
                            snackbar.show();
                        }
                    });
//

                }
            }
        });
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getActivity());
        mGoogleMap = googleMap;

        //Check for location permission before allowing location services
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        } else
            mGoogleMap.setMyLocationEnabled(true);
        setMarkersFromArray(mGoogleMap);

        mGoogleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(pointOfInterest.latLng)
                        .title(pointOfInterest.name)
                        .snippet("Place ID: " + pointOfInterest.placeId));

                marker.showInfoWindow();
            }
        });

        mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            Marker currentMarker = null;

            @Override
            public void onMapLongClick(final LatLng latLng) {
                if (currentMarker != null) {
                    currentMarker.remove();
                    currentMarker = null;
                }
                GeocodeHandler geocodeHandler = (GeocodeHandler) new GeocodeHandler(getActivity(), mGoogleMap);
                geocodeHandler.getLocationFromLatLng(latLng, new AsyncResponse() {
                    @Override
                    public void processFinish(final String response, LatLng response2) {
                        Snackbar snackbar = Snackbar
                                .make(getView(), response, Snackbar.LENGTH_LONG)
                                .setAction("Add Marker", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showMarkerDialog(getActivity(), latLng, response);
                                    }
                                });
                        snackbar.show();
                    }
                });

                currentMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("GeoMarker"));
            }
        });


    }

    private void addMarkerToMap(LatLng latLng, GoogleMap mGoogleMap, String title) {
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(title));
    }

    private void centerCameraOnLatLng(LatLng latLng, GoogleMap mGoogleMap){
        CameraPosition camPos = CameraPosition.builder().target(latLng).zoom(Config.MAP_ZOOM).bearing(Config.MAP_BEARING).tilt(Config.MAP_TILT).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    public void showMarkerDialog(Context context, final LatLng latLng, final String formattedAddress) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.marker_popup_view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);

        //Truncate latitude and longitude to visual format 5dp
        String markerLat = new DecimalFormat("#.#####").format(latLng.latitude);
        String markerLng = new DecimalFormat("#.#####").format(latLng.longitude);

        final EditText etDialogLabel = (EditText) dialog.findViewById(R.id.dialogLabel);
        TextView tvDialogAddress = (TextView) dialog.findViewById(R.id.dialogAddress);
        TextView tvDialogLat = (TextView) dialog.findViewById(R.id.dialogLat);
        TextView tvDialogLng = (TextView) dialog.findViewById(R.id.dialogLng);
        Button btnDialogCancel = (Button) dialog.findViewById(R.id.dialogCancelBtn);
        Button btnDialogAccept = (Button) dialog.findViewById(R.id.dialogAcceptBtn);

        tvDialogLat.setText("Latitude: " + markerLat.toString());
        tvDialogLng.setText("Longitude: " + markerLng.toString());
        tvDialogAddress.setText("Address: " + formattedAddress);

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDialogAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalStorageHandler userStorage = new LocalStorageHandler();
                String markerOwner = userStorage.getUsername(getActivity().getApplicationContext());
                if (markerOwner != null) {
                    // Add marker
                    String label = etDialogLabel.getText().toString();
                    GeoMarker geoMarker = new GeoMarker(formattedAddress, label, latLng.latitude, latLng.longitude, markerOwner);
                    geoMarker.addMarker(getActivity());
                }
                dialog.cancel();
            }
        });

        /**
         *Height and width multiplier for dialog box (85% of screen)
         */
        double dialogSizeWidth = 0.85;
        double dialogSizeHeight = 0.85;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dialogWidth = (int) (displayMetrics.widthPixels * dialogSizeWidth);
        int dialogHeight = (int) (displayMetrics.heightPixels * dialogSizeHeight);
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);

        dialog.show();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        @SuppressWarnings("MissingPermission") Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            addMarkerToMap(new LatLng(location.getLatitude(), location.getLongitude()) , mGoogleMap, "Current");
            centerCameraOnLatLng(new LatLng(location.getLatitude(), location.getLongitude()), mGoogleMap);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void setMarkersFromArray(final GoogleMap mGoogleMap) {
        getMarkers(new MarkerCallback() {
            @Override
            public void onSuccess(JSONArray markerArray) {
                for (int i = 0; i < markerArray.length(); i++) {
                    JSONObject marker = null;
                    double markerLat = 0.0;
                    double markerLng = 0.0;
                    String markerLabel = null;
                    try {
                        marker = markerArray.getJSONObject(i);
                        markerLat = marker.getDouble("latitude");
                        markerLng = marker.getDouble("longitude");
                        markerLabel = marker.getString("label");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (marker != null && markerLabel != null) {
                        LatLng markerLatLng = new LatLng(markerLat, markerLng);
                        addMarkerToMap(markerLatLng, mGoogleMap, markerLabel);
                    }
                }
            }
        });
    }

    public void getMarkers(final MarkerCallback callback) {
        LocalStorageHandler userStorage = new LocalStorageHandler();
        String username = userStorage.getUsername(getActivity().getApplicationContext());
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
                        callback.onSuccess(markerArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (success) {
                        callback.onSuccess(markerArray);
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
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.add(requestHandler);
        }
    }
}
