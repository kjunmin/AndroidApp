package com.webnav.matth.GeoNavSRC;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
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

/**
 * Created by matth on 25/8/2017.
 */

public class MapFrag extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    public MapFrag() {

    }

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(40.689247, -74.044502)).title("Statute").snippet("U there"));
        CameraPosition Liberty = CameraPosition.builder().target(new LatLng(40.689247, -74.044502)).zoom(16).bearing(0).tilt(45).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));

        mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            Marker currentMarker = null;
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (currentMarker != null) {
                    currentMarker.remove();
                    currentMarker = null;
                }
                currentMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                CameraPosition test = CameraPosition.builder().target(latLng).zoom(16).bearing(0).tilt(45).build();
                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(test));
            }
        });

    }


}
