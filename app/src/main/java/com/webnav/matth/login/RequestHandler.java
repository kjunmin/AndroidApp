package com.webnav.matth.login;

import com.android.volley.Response;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by matth on 26/8/2017.
 */

public class RequestHandler extends JsonObjectRequest {

    public RequestHandler(String URL, JSONObject obj, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(URL, obj, listener, errorListener);

    }

}
