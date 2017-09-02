package com.webnav.matth.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.webnav.matth.login.Config;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matth on 2/9/2017.
 */

public class LocalStorageHandler {

    public LocalStorageHandler() {
    }



    public JSONObject getJsonUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Config.PREF_FILE_NAME, Context.MODE_PRIVATE);
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

    public String getUsername(Context context)  {
//        SharedPreferences prefs = context.getSharedPreferences(Config.PREF_FILE_NAME, Context.MODE_PRIVATE);
        JSONObject user = getJsonUser(context);
        String username = null;
        try {
            username = user.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return username;
    }

    public String getEmail(Context context){
        JSONObject user = getJsonUser(context);
        String email = null;
        try {
            email = user.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return email;
    }

    public String getFirstname(Context context) {
        JSONObject user = getJsonUser(context);
        String firstname = null;
        try {
            firstname = user.getString("firstname");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return firstname;
    }

    public String getLastname(Context context) {
        JSONObject user = getJsonUser(context);
        String lastname = null;
        try {
            lastname = user.getString("lastname");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lastname;
    }
}
