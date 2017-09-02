package com.webnav.matth.models;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.webnav.matth.login.Config;
import com.webnav.matth.login.LoginActivity;
import com.webnav.matth.login.RegisterActivity;
import com.webnav.matth.login.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matth on 30/8/2017.
 */

public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;

    public User(String firstName, String lastName, String username, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
    }


    public void addUser(final Application app) {
        final String REGISTER_REQUEST_URL = Config.DEV_REGISTER_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("firstname", this.firstName);
        params.put("lastname", this.lastName);
        params.put("username", this.username);
        params.put("email", this.email);
        params.put("password", this.password);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                boolean success = false;
                String output = "Unidentified Error";
                try {
                    success = response.getBoolean("success");
                    output = response.getString("output");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (success) {
                    Toast.makeText(app, output, Toast.LENGTH_SHORT).show();
                    Intent toLogin = new Intent(app, LoginActivity.class);
                    app.startActivity(toLogin);
                } else {
                    Toast.makeText(app, output, Toast.LENGTH_SHORT).show();
                }

            }
        };

        //Handle Error exceptions
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(app, error+"", Toast.LENGTH_SHORT).show();
            }
        };

        //Pass Request to request handler and add to request queue
        RequestHandler requestHandler = new RequestHandler(REGISTER_REQUEST_URL, new JSONObject(params), listener, errorListener);
        RequestQueue requestQueue = Volley.newRequestQueue(app);
        requestQueue.add(requestHandler);
    }

    public User getUser() {
        return this;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
