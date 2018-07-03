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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.storageauctions.dev.ServiceManager.ServiceManager;

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

        ImageButton loginBtn = (ImageButton) rootView.findViewById(R.id.login_btn);
        Button forgotPasswordBtn = (Button) rootView.findViewById(R.id.forgot_password_btn);
        final EditText email = (EditText) rootView.findViewById(R.id.login_email);
        final EditText password = (EditText) rootView.findViewById(R.id.login_password);

        email.setText("ro.bert.miner@gmail.com");
        password.setText("miner001");

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
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        ProgressView.sharedView().dismiss();
                        Log.d("Login Success", response.toString());
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("welcome", false);
                        startActivity(intent);
                    }
                });
            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }
}
