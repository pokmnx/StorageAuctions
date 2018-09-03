package com.storageauctions.dev;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.storageauctions.dev.ServiceManager.ServiceManager;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ImageView background = (ImageView) view.findViewById(R.id.home_background);
        // Picasso.get().load(R.drawable.first_auct_background).into(background);

        TextView welcomeText = (TextView) view.findViewById(R.id.welcomeStr);
        welcomeText.setText("Welcome, " + ServiceManager.sharedManager().user_name + "!");
        ServiceManager.sharedManager().setTypeFace(getContext(), welcomeText, R.font.raleway_medium);

        Button createAuctionBtn = (Button) view.findViewById(R.id.create_new_auction_btn);
        createAuctionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAuctionFragment fragment = new CreateAuctionFragment();
                if (fragment != null) {
                    fragment.bEdit = false;
                    fragment.mAuction = null;
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });
        ServiceManager.sharedManager().setTypeFace(getContext(), createAuctionBtn, R.font.montserrat_bold);

        return view;
    }
}
