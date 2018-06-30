package com.storageauctions.dev;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

public class SignUpFragment extends Fragment {
    public SignUpFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);

        EditText username = (EditText) rootView.findViewById(R.id.signup_username);
        EditText email = (EditText) rootView.findViewById(R.id.signup_email);
        EditText password = (EditText) rootView.findViewById(R.id.signup_password);
        EditText name = (EditText) rootView.findViewById(R.id.signup_name);
        EditText phone = (EditText) rootView.findViewById(R.id.signup_phone);

        RadioButton auctionRadio = (RadioButton) rootView.findViewById(R.id.auction_radio);
        RadioButton facilityRadio = (RadioButton) rootView.findViewById(R.id.facility_owner_radio);
        ImageButton signupBtn = (ImageButton) rootView.findViewById(R.id.signup_btn);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }
}
