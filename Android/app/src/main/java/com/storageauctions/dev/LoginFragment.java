package com.storageauctions.dev;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.storageauctions.dev.ServiceManager.Facility;
import com.storageauctions.dev.ServiceManager.ServiceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class LoginFragment extends Fragment {

    public LoginFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginBtn = (Button) rootView.findViewById(R.id.login_btn);
        Button forgotPasswordBtn = (Button) rootView.findViewById(R.id.forgot_password_btn);
        final EditText email = (EditText) rootView.findViewById(R.id.login_email);
        final EditText password = (EditText) rootView.findViewById(R.id.login_password);

        email.setText("ro.bert.miner@gmail.com");
        password.setText("miner001");

        Typeface inputTypeface = ResourcesCompat.getFont(getContext(), R.font.raleway_extralightitalic);
        email.setTypeface(inputTypeface);
        password.setTypeface(inputTypeface);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressView.sharedView().show(getContext());
                ServiceManager.sharedManager().login(email.getText().toString(), password.getText().toString(), new JsonHttpResponseHandler(){
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ProgressView.sharedView().dismiss();
                        try {
                            JSONObject error = errorResponse.getJSONObject("errors");
                            Iterator<String> keys = error.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                String value = error.getString(key);
                                if (value != null && value.length() > 0) {
                                    ProgressView.sharedView().showToast(value);
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Login Failed", errorResponse.toString());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response1) {
                        Log.d("Login Success", response1.toString());
                        try { ServiceManager.sharedManager().user_name = response1.getString("name"); } catch (JSONException e) { e.printStackTrace(); }

                        ServiceManager.sharedManager().getAllFacilities(new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    JSONArray facDicArr = response.getJSONArray("facilities");
                                    if (facDicArr.length() > 0) {
                                        JSONObject facDic = facDicArr.getJSONObject(0);
                                        try { ServiceManager.sharedManager().user_id = facDic.getInt("user_id"); } catch (JSONException e) { e.printStackTrace(); }
                                        ServiceManager.sharedManager().bLoginFirstTime = false;
                                    } else {
                                        ServiceManager.sharedManager().bLoginFirstTime = true;
                                    }

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ProgressView.sharedView().dismiss();
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            intent.putExtra("welcome", ServiceManager.sharedManager().bLoginFirstTime);
                                            startActivity(intent);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                ProgressView.sharedView().showToast("Login Failed");
                            }
                        });
                    }
                });
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.raleway_extralight);
        forgotPasswordBtn.setTypeface(typeface);

        return rootView;
    }
}
