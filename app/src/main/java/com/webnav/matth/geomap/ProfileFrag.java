package com.webnav.matth.geomap;

import android.content.Context;
import android.content.Intent;
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
import com.webnav.matth.models.LocalStorageHandler;
import com.webnav.matth.services.LocationService;

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

        LocalStorageHandler userStorage = new LocalStorageHandler();

        String userFirstname = userStorage.getFirstname(getActivity().getApplicationContext());
        String userLastname = userStorage.getLastname(getActivity().getApplicationContext());
        String userEmail = userStorage.getEmail(getActivity().getApplicationContext());

        if (userFirstname != null && userLastname != null){
            String userFullname = userFirstname + " " + userLastname;
            profileFullname.setText("Welcome Back, " + userFullname);
        }
        if (userEmail != null) {
            profileEmail.setText("Email: "+ userEmail);
        }
        Intent startLocationServices = new Intent(getActivity(), LocationService.class);
        getActivity().startService(startLocationServices);
    }

}

