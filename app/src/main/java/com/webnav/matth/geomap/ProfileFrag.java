package com.webnav.matth.geomap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.webnav.matth.R;
import com.webnav.matth.login.Config;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by matth on 25/8/2017.
 */

public class ProfileFrag extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile_frag, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView profileFullname = (TextView) getView().findViewById(R.id.profile_fullname);
        final TextView profileEmail = (TextView) getView().findViewById(R.id.profile_email);
        JSONObject userInfo = getUserInfo();
        String userFirstname = null;
        String userLastname = null;
        String userEmail = null;
        try {
            userFirstname = userInfo.getString("firstname");
            userLastname = userInfo.getString("lastname");
            userEmail = userInfo.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (userFirstname != null && userLastname != null){
            String userFullname = userFirstname + " " + userLastname;
            profileFullname.setText("Name: " + userFullname);
        }
        if (userEmail != null) {
            profileEmail.setText("Email: "+ userEmail);
        }
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

