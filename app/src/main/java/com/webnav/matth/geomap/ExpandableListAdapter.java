package com.webnav.matth.geomap;

import android.app.Activity;
import android.content.Context;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.webnav.matth.R;
import com.webnav.matth.models.GeoMarker;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by matth on 30/8/2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<GeoMarker> parentList;
    private HashMap<GeoMarker, List<GeoMarker>> childListCollection;
    private LatLng locationLatLng;

    public ExpandableListAdapter(Context context, List<GeoMarker> parentList, HashMap<GeoMarker, List<GeoMarker>> childListCollection, LatLng locationLatLng) {
        this.context = context;
        this.childListCollection = childListCollection;
        this.parentList = parentList;
        this.locationLatLng = locationLatLng;
    }

    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childListCollection.get(parentList.get(groupPosition)).size();
    }

    @Override
    public GeoMarker getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override
    public GeoMarker getChild(int groupPosition, int childPosition) {
        return this.childListCollection.get(this.parentList.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.marker_list_header,
                    null);
        }
        setHeaderLayout(convertView, groupPosition);
        return convertView;
    }



    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.marker_list_child, null);
        }
        setChildLayout(convertView, childPosition, groupPosition);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void removeChild(int groupPosition, int childPosition) {

        if (getChildrenCount(groupPosition)>0 && getChildrenCount(groupPosition)-1 >= childPosition )
        {
            GeoMarker child = this.getChild(groupPosition, childPosition);
            parentList.remove(child);
            this.notifyDataSetChanged();
        }
    }

    // Binds the Marker List Header information to the Marker Fragment header layout
    private void setHeaderLayout(View convertView, int groupPosition) {
        String parentGeomarkerLabel = getGroup(groupPosition).getMarkerLabel();
        String formattedDistance;

        TextView tvMarkerHeader = (TextView) convertView.findViewById(R.id.tvMarkerHeader);
        TextView tvChildDistance = (TextView) convertView.findViewById(R.id.tvMarkerDistance);
        tvMarkerHeader.setText(parentGeomarkerLabel);
        if (locationLatLng != null) {
            Double parentLatitude = getGroup(groupPosition).getMarkerLat();
            Double parentLongitude = getGroup(groupPosition).getMarkerLng();
            LatLng parentLatLng = new LatLng(parentLatitude, parentLongitude);
            Double distance = calculateDistanceDiff(locationLatLng,parentLatLng);
            formattedDistance = formatDistance(distance);
        } else
            formattedDistance = "N/A";

        tvChildDistance.setText(formattedDistance);
    }

    // Binds the Marker Collection Child data to the Marker Fragment child layout
    private void setChildLayout(View convertView, final int childPosition, final int groupPosition) {
        final GeoMarker childMarker = getChild(groupPosition, childPosition);
        final String childMarkerAddress = getChild(groupPosition, childPosition).getMarkerAddress();
        final Double childMarkerLat = getChild(groupPosition, childPosition).getMarkerLat();
        final Double childMarkerLng = getChild(groupPosition, childPosition).getMarkerLng();

        //Get all child layout elements
        TextView tvChildAddress = (TextView) convertView.findViewById(R.id.tvMarkerAddress);
        TextView tvChildLat = (TextView) convertView.findViewById(R.id.tvMarkerLat);
        TextView tvChildLng = (TextView) convertView.findViewById(R.id.tvMarkerLng);


        // Set Child Item Layout Text
        tvChildAddress.setText("Address: " + childMarkerAddress);
        tvChildLat.setText("Lat: " + childMarkerLat.toString());
        tvChildLng.setText("Lng: " + childMarkerLng.toString());


        ImageButton ibToMapFromMarker = (ImageButton) convertView.findViewById(R.id.ibToMapLocation);
        ImageButton ibDeleteMarker = (ImageButton) convertView.findViewById(R.id.ibDeleteMarker);

        ibToMapFromMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment toMapFrag = new MapFrag();
                FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.MyFrameLayout, toMapFrag)
                        .commit();
            }
        });

        ibDeleteMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete?");
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO: DELETE MARKER HANDLER
                        childMarker.delMarker(context);
                        ExpandableListAdapter.this.removeChild(groupPosition, childPosition);
                    }
                });
                builder.setNegativeButton("No", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private String formatDistance(Double distance) {
        String quantifier;
        Double myDistance = distance;
        if (distance > 1000) {
            quantifier = "km";
            myDistance = myDistance/1000;
        } else {
            quantifier = "m";
        }
        String formatted = new DecimalFormat("####").format(myDistance);
        return formatted+quantifier;
    }

    private double calculateDistanceDiff(LatLng from, LatLng to) {
        final int R = 6371; // Radius of Earth
        double latFrom = from.latitude;
        double lngFrom = from.longitude;
        double latTo = to.latitude;
        double lngTo = to.longitude;

        double latDistance = Math.toRadians(latFrom - latTo);
        double lonDistance = Math.toRadians(lngFrom - lngTo);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latTo)) * Math.cos(Math.toRadians(latFrom))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance;
    }

    public void setLocationLatLng(LatLng latLng) {
        this.locationLatLng = latLng;
    }
}
