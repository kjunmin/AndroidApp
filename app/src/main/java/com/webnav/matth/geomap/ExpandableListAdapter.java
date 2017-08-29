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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.webnav.matth.R;
import com.webnav.matth.models.GeoMarker;

import java.util.HashMap;
import java.util.List;

/**
 * Created by matth on 30/8/2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<GeoMarker> parentList;
    private HashMap<GeoMarker, List<GeoMarker>> childListCollection;

    public ExpandableListAdapter(Context context, List<GeoMarker> parentList, HashMap<GeoMarker, List<GeoMarker>> childListCollection) {
        this.context = context;
        this.childListCollection = childListCollection;
        this.parentList = parentList;
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
        String parentGeomarkerLabel = getGroup(groupPosition).getMarkerLabel();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.marker_list_group,
                    null);
        }
        TextView tvMarkerHeader = (TextView) convertView.findViewById(R.id.tvMarkerHeader);
        tvMarkerHeader.setText(parentGeomarkerLabel);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String childMarkerAddress = getChild(groupPosition, childPosition).getMarkerAddress();
        final Double childMarkerLat = getChild(groupPosition, childPosition).getMarkerLat();
        final Double childMarkerLng = getChild(groupPosition, childPosition).getMarkerLng();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.marker_list_single, null);
        }

        //Get all child layout elements
        TextView tvChildAddress = (TextView) convertView.findViewById(R.id.tvMarkerAddress);
        TextView tvChildLat = (TextView) convertView.findViewById(R.id.tvMarkerLat);
        TextView tvChildLng = (TextView) convertView.findViewById(R.id.tvMarkerLng);
        ImageButton ibToMapFromMarker = (ImageButton) convertView.findViewById(R.id.ibToMapLocation);
        ImageButton ibDeleteMarker = (ImageButton) convertView.findViewById(R.id.ibDeleteMarker);
        LatLng childLatLng = new LatLng(childMarkerLat, childMarkerLng);

        // Set Child Item Layout Text
        tvChildAddress.setText("Address: " + childMarkerAddress);
        tvChildLat.setText("Lat: " + childMarkerLat.toString());
        tvChildLng.setText("Lng: " + childMarkerLng.toString());

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
                        Toast.makeText(context, "TODO:DELETE MARKER API CALL HANDLER", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
