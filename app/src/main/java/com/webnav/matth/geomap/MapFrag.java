package com.webnav.matth.geomap;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
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


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.webnav.matth.R;
import com.webnav.matth.login.Config;
import com.webnav.matth.models.GeoMarker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


/**
 * Created by matth on 25/8/2017.
 */

public class MapFrag extends Fragment implements OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    public MapFrag() {

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String s = getActivity().getIntent().getStringExtra("latlng");
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_map_frag, container, false);

        return mView;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
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
                            addMarkerAndCenterOnMarker(response2, mGoogleMap, "Marker");
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getActivity(), "Allow location permissions to enable functionality", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        MapsInitializer.initialize(getActivity());
        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        //Default location on load
        addMarkerAndCenterOnMarker(new LatLng(40.689247, -74.044502), mGoogleMap, "Statue Of Liberty");

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

    //Add Marker to map and center camera
    private void addMarkerAndCenterOnMarker(LatLng latLng, GoogleMap mGoogleMap, String title) {
        mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(title));
        CameraPosition camPos = CameraPosition.builder().target(latLng).zoom(Config.MAP_ZOOM).bearing(Config.MAP_BEARING).tilt(Config.MAP_TILT).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    private void showMarkerDialog(Context context, final LatLng latLng, final String formattedAddress) {
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
                JSONObject user = getUserInfo();
                String markerOwner = null;
                try {
                    markerOwner = user.getString("username");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (markerOwner != null) {
                    String label = etDialogLabel.getText().toString();
                    GeoMarker geoMarker = new GeoMarker(formattedAddress, label, latLng.latitude, latLng.longitude, markerOwner);
                    geoMarker.addMarker(getActivity());
                }
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

    private JSONObject getUserInfo() {
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
