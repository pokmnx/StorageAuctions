package com.storageauctions.dev;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.storageauctions.dev.ServiceManager.ServiceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

public class FacilityDetailFragment extends Fragment {

    double mLat;
    double mLon;
    JSONObject mAddressInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facility_detail, container, false);

        final EditText nameEdit = (EditText) view.findViewById(R.id.detail_facility_name);
        final EditText phoneEdit = (EditText) view.findViewById(R.id.detail_phone_number);
        final EditText emailEdit = (EditText) view.findViewById(R.id.detail_contact_email);
        final EditText taxEdit = (EditText) view.findViewById(R.id.detail_tax_rate);
        final EditText commissionEdit = (EditText) view.findViewById(R.id.detail_commision_rate);

        Button saveBtn = (Button) view.findViewById(R.id.detail_save_button);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressView.sharedView().show();

                String[] addressArr = new String[7];

                for (int i = 0; i < 7; i++) addressArr[i] = "";

                addressArr[0] = String.format("%f", mLat);
                addressArr[1] = String.format("%f", mLon);
                addressArr[2] = "5";
                try {
                    addressArr[3] = mAddressInfo.getString("formatted_address");
                    JSONArray addressComps = mAddressInfo.getJSONArray("address_components");
                    for (int i = 0; i < addressComps.length(); i++) {
                        JSONObject comp = addressComps.getJSONObject(i);
                        JSONArray types = comp.getJSONArray("types");
                        for (int j = 0; j < types.length(); j++) {
                            String type = types.getString(j);
                            if (type.compareTo("postal_code") == 0) {
                                addressArr[5] = comp.getString("long_name");
                            } else if (type.compareTo("locality") == 0) {
                                addressArr[4] = comp.getString("long_name");
                            } else if (type.compareTo("administrative_area_level_1") == 0) {
                                addressArr[6] = comp.getString("short_name");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ServiceManager.sharedManager().addNewFacility(nameEdit.getText().toString(), emailEdit.getText().toString(), "", phoneEdit.getText().toString(), taxEdit.getText().toString(), commissionEdit.getText().toString(), "",
                        addressArr, new JsonHttpResponseHandler() {
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
                                Log.d("FacilityCreation Failed", errorResponse.toString());
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                ProgressView.sharedView().dismiss();
                                Log.d("FacilityCreatio Success", response.toString());

                                FacilityListFragment fragment = new FacilityListFragment();

                                if (fragment != null) {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, fragment);
                                    ft.commit();
                                }
                            }
                        });
            }
        });

        return view;
    }
}
