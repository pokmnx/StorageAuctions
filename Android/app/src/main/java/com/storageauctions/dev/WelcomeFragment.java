package com.storageauctions.dev;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.storageauctions.dev.ServiceManager.ServiceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class WelcomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        TextView welcomeText = (TextView) view.findViewById(R.id.welcomeStr);
        welcomeText.setText("Welcome, " + ServiceManager.sharedManager().user_name + "!");
        ServiceManager.sharedManager().setTypeFace(getContext(), welcomeText, R.font.raleway_medium);

        Button claimFacility = (Button) view.findViewById(R.id.claim_facility_btn);
        Button addFirstAuction = (Button) view.findViewById(R.id.add_first_auction_btn);
        Button contactSupport = (Button) view.findViewById(R.id.contact_support_btn);

        ServiceManager.sharedManager().setTypeFace(getContext(), claimFacility, R.font.montserrat_bold);
        ServiceManager.sharedManager().setTypeFace(getContext(), addFirstAuction, R.font.montserrat_bold);
        ServiceManager.sharedManager().setTypeFace(getContext(), contactSupport, R.font.montserrat_bold);

        claimFacility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FacilitySearchFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });

        addFirstAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressView.sharedView().show(getContext());
                ServiceManager.sharedManager().getAllFacilities(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONArray facDicArr = response.getJSONArray("facilities");
                            if (facDicArr.length() > 0) {
                                JSONObject facDic = facDicArr.getJSONObject(0);
                                try { ServiceManager.sharedManager().user_id = facDic.getInt("user_id"); } catch (JSONException e) { e.printStackTrace(); }
                                ServiceManager.sharedManager().bLoginFirstTime = false;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ProgressView.sharedView().dismiss();
                                        Fragment fragment = new CreateAuctionFragment();
                                        if (fragment != null) {
                                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                            ft.replace(R.id.content_frame, fragment);
                                            ft.addToBackStack(null);
                                            ft.commit();
                                        }
                                    }
                                });
                            } else {
                                ServiceManager.sharedManager().bLoginFirstTime = true;
                                ProgressView.sharedView().showToast("You need to create Facility first.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ProgressView.sharedView().showToast("You need to create Facility first.");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        ProgressView.sharedView().showToast("You need to create Facility first.");
                    }
                });
            }
        });

        contactSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "support@storageauctions.net" });
                i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Support Request - Sent From iOS App");
                startActivity(Intent.createChooser(i, "Contact Support"));
            }
        });

        return view;
    }
}
