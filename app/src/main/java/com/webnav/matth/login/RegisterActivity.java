package com.webnav.matth.login;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.webnav.matth.R;
import com.webnav.matth.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etFirstName = (EditText) findViewById(R.id.etFirstname);
        final EditText etLastName = (EditText) findViewById(R.id.etLastName);
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bRegister = (Button) findViewById(R.id.btnRegister);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusView = null;
                boolean cancel = false;

                final String firstName = etFirstName.getText().toString();
                final String lastName = etLastName.getText().toString();
                final String username = etUsername.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                //Validation of input fields
                if (TextUtils.isEmpty(firstName)) {
                    etFirstName.setError("This field is required!");
                    focusView = etFirstName;
                    cancel = true;
                }
                if (TextUtils.isEmpty(lastName)) {
                    etLastName.setError("This field is required!");
                    focusView = etLastName;
                    cancel = true;
                }
                if (TextUtils.isEmpty(username)) {
                    etUsername.setError("This field is required!");
                    focusView = etUsername;
                    cancel = true;
                }
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("This field is required!");
                    focusView = etEmail;
                    cancel = true;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("This field is required!");
                    focusView = etPassword;
                    cancel = true;
                }
                if (!isEmailValid(email)) {
                    etEmail.setError("Enter a valid email!");
                    focusView = etEmail;
                    cancel = true;
                }
                if (cancel) {
                    focusView.requestFocus();
                } else {
                    User user = new User(firstName, lastName, username, email, password);
                    user.addUser(getApplication());
                }

            }
        });
    }


    //Email checking logic goes here
    private boolean isEmailValid(String emailToTest) {
        if (emailToTest.contains("@")){
            return true;
        } else {
            return false;
        }
    }

//    private void Register(final String firstName, final String lastName, final String username, final String email, final String password) {
//        //Map Form input to respective JSON Post request format
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("firstname", firstName);
//        params.put("lastname", lastName);
//        params.put("username", username);
//        params.put("email", email);
//        params.put("password", password);
//
//
//        //Obtain Request URL from config folder
//        final String REGISTER_REQUEST_URL = Config.DEV_REGISTER_URL;
//
//        //Handle Response
//        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                boolean success = false;
//                String output = "Unidentified Error";
//                try {
//                    success = response.getBoolean("success");
//                    output = response.getString("output");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                if (success) {
//                    Toast.makeText(getApplication(), output, Toast.LENGTH_SHORT).show();
//                    Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
//                    RegisterActivity.this.startActivity(toLogin);
//                } else {
//                    Toast.makeText(getApplication(), output, Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        };
//
//        //Handle Error exceptions
//        Response.ErrorListener errorListener = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(RegisterActivity.this, error+"", Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        //Pass Request to request handler and add to request queue
//        RequestHandler requestHandler = new RequestHandler(REGISTER_REQUEST_URL, new JSONObject(params), listener, errorListener);
//        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
//        requestQueue.add(requestHandler);
//    }
}
