package com.storageauctions.dev;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.storageauctions.dev.ServiceManager.ServiceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class SignUpFragment extends Fragment {
    public SignUpFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);

        final EditText username = (EditText) rootView.findViewById(R.id.signup_username);
        final EditText email = (EditText) rootView.findViewById(R.id.signup_email);
        final EditText password = (EditText) rootView.findViewById(R.id.signup_password);
        final EditText name = (EditText) rootView.findViewById(R.id.signup_name);
        final EditText phone = (EditText) rootView.findViewById(R.id.signup_phone);

        final RadioButton auctionRadio = (RadioButton) rootView.findViewById(R.id.auction_radio);
        final RadioButton facilityRadio = (RadioButton) rootView.findViewById(R.id.facility_owner_radio);
        ImageButton signupBtn = (ImageButton) rootView.findViewById(R.id.signup_btn);

        auctionRadio.setChecked(true);
        facilityRadio.setChecked(false);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressView.sharedView().show(getContext());
                String role = auctionRadio.isChecked() ? "a" : "b";
                ServiceManager.sharedManager().signup(email.getText().toString(), password.getText().toString(), username.getText().toString(), phone.getText().toString(), name.getText().toString(), role, new JsonHttpResponseHandler() {
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
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        ProgressView.sharedView().dismiss();
                        Log.d("Sign up Success", response.toString());
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("welcome", true);
                        startActivity(intent);
                    }
                });
            }
        });

        auctionRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                facilityRadio.setChecked(!isChecked);
            }
        });

        facilityRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                auctionRadio.setChecked(!isChecked);
            }
        });

        return rootView;
    }
}
