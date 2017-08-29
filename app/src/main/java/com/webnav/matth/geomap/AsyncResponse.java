package com.webnav.matth.geomap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by matth on 28/8/2017.
 */

public interface AsyncResponse {
    void processFinish(String response, LatLng response2);
}
