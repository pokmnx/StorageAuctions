package com.storageauctions.dev;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.storageauctions.dev.ServiceManager.ServiceManager;

import org.json.JSONArray;
import org.json.JSONObject;

public class FacilityMapFragment extends Fragment {

    public double mLat;
    public double mLon;
    public String mAddress;
    public JSONObject mAddressInfo;

    private SupportMapFragment mSupportMapFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facility_map, container, false);

        TextView addressTitle = (TextView) view.findViewById(R.id.map_title);
        TextView addressView = (TextView) view.findViewById(R.id.map_address);
        addressView.setText(mAddress);

        final LatLng point = new LatLng(mLat, mLon);

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapView, mSupportMapFragment).commit();
        }

        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {

                        MarkerOptions marker = new MarkerOptions().position(
                                new LatLng(mLat, mLon));
                        marker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                        // adding marker
                        googleMap.addMarker(marker);

                        googleMap.getUiSettings().setAllGesturesEnabled(true);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(point).zoom(15.0f).build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        googleMap.moveCamera(cameraUpdate);

                    }
                }
            });
        }

        Button contBtn = (Button) view.findViewById(R.id.map_continue_btn);
        contBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacilityDetailFragment fragment = new FacilityDetailFragment();
                fragment.mLat = mLat;
                fragment.mLon = mLon;
                fragment.mAddressInfo = mAddressInfo;

                if (fragment != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

        ServiceManager.sharedManager().setTypeFace(getContext(), addressTitle, R.font.montserrat_medium);
        ServiceManager.sharedManager().setTypeFace(getContext(), addressView, R.font.montserrat_extralight);
        ServiceManager.sharedManager().setTypeFace(getContext(), contBtn, R.font.montserrat_bold);

        return view;
    }
}
