package com.webnav.matth.geomap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.webnav.matth.R;
import com.webnav.matth.models.GeoMarker;

import java.util.ArrayList;

/**
 * Created by matth on 27/8/2017.
 */

public class MarkerListAdapter extends ArrayAdapter<GeoMarker> {

    private final Activity context;
    private final ArrayList<GeoMarker> markersArray;
    public MarkerListAdapter(Activity context,
                      ArrayList<GeoMarker> markersArray) {
        super(context, R.layout.marker_list_single, markersArray);
        this.context = context;
        this.markersArray = markersArray;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.marker_list_single, null);

        //Set layout textview with marker array list
        TextView tvAddress = (TextView) rowView.findViewById(R.id.tvMarkerAddress);
        TextView tvLat = (TextView) rowView.findViewById(R.id.tvMarkerLat);
        TextView tvLng = (TextView) rowView.findViewById(R.id.tvMarkerLng);
//        tvTitle.setText(markersArray.get(position).getMarkerLabel());
        tvAddress.setText("Address: " + markersArray.get(position).getMarkerAddress());
        tvLat.setText("Latitude: " + markersArray.get(position).getMarkerLat().toString());
        tvLng.setText("Longitude: " + markersArray.get(position).getMarkerLng().toString());

        return rowView;
    }

    public GeoMarker getItem(int position) {
        return markersArray.get(position);
    }
}