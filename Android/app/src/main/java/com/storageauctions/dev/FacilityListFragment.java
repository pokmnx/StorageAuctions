package com.storageauctions.dev;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.storageauctions.dev.ServiceManager.Facility;
import com.storageauctions.dev.ServiceManager.ServiceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import cz.msebera.android.httpclient.Header;

public class FacilityListFragment extends Fragment {

    ListView facilityListView;
    ArrayList<Facility> facilityArrayList;
    public Context mContext;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facility_list, container, false);

        facilityListView = (ListView) view.findViewById(R.id.list_facility);
        facilityArrayList = new ArrayList<>();
        mContext = getContext();
        ProgressView.sharedView().show();

        ServiceManager.sharedManager().getAllFacilities(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray facDicArr = response.getJSONArray("facilities");
                    for (int i = 0; i < facDicArr.length(); i++) {
                        JSONObject facDic = facDicArr.getJSONObject(i);

                        Facility facility = new Facility();
                        try {
                            facility.id = facDic.getInt("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try { facility.user_id = facDic.getInt("user_id"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.active = facDic.getInt("active"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.name = facDic.getString("name"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.address = facDic.getString("address"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.city = facDic.getString("city"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.acc = facDic.getInt("acc"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.state_code = facDic.getString("state_code"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.postal_code = facDic.getString("postal_code"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.country = facDic.getString("country"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.lat = facDic.getDouble("lat"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.lon = facDic.getDouble("lon"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.phone = facDic.getString("phone"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.website = facDic.getString("website"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.email = facDic.getString("email"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.time_created = getDateFrom(facDic.getString("time_created")); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.time_updated = getDateFrom(facDic.getString("time_updated")); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.source = facDic.getString("source"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.source_id = facDic.getInt("source_id"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.terms = facDic.getString("terms"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.taxrate = facDic.getDouble("taxrate"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.remote_id = facDic.getInt("remote_id"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.commission_rate = facDic.getDouble("commission_rate"); } catch (JSONException e) { e.printStackTrace(); }
                        try { facility.url = facDic.getString("url"); } catch (JSONException e) { e.printStackTrace(); }

                        facilityArrayList.add(facility);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProgressView.sharedView().dismiss();
                            FacilityListAdapter adapter = new FacilityListAdapter(mContext, savedInstanceState, facilityArrayList);
                            facilityListView.setAdapter(adapter);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                ProgressView.sharedView().showToast(mContext, "Loading Failed");
            }
        });

        return view;
    }

    public Date getDateFrom(String str) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public class FacilityListAdapter extends BaseAdapter {

        LayoutInflater mLayoutInflater;
        ArrayList<Facility> facilityList;
        Bundle mSavedInstance;
        SupportMapFragment mSupportMapFragment;

        public FacilityListAdapter(Context fragment, Bundle savedInstance, ArrayList<Facility> list) {
            facilityList = list;
            mSavedInstance = savedInstance;
            mLayoutInflater = LayoutInflater.from(fragment);
        }

        @Override
        public int getCount() {
            return facilityList.size();
        }

        @Override
        public Object getItem(int position) {
            return facilityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = mLayoutInflater.inflate(R.layout.list_facility, parent, false);

            final Facility facility = facilityList.get(position);

            TextView nameView = (TextView) rowView.findViewById(R.id.facility_cell_name);
            TextView addressView = (TextView) rowView.findViewById(R.id.facility_cell_address);
            TextView auctionCountView = (TextView) rowView.findViewById(R.id.facility_cell_active_auctions);
            TextView totalAuctionCountView = (TextView) rowView.findViewById(R.id.facility_cell_total_auctions);

            ServiceManager.sharedManager().setTypeFace(getContext(), nameView, R.font.montserrat_medium);
            ServiceManager.sharedManager().setTypeFace(getContext(), addressView, R.font.montserrat_extralight);
            ServiceManager.sharedManager().setTypeFace(getContext(), auctionCountView, R.font.montserrat_extralight);
            ServiceManager.sharedManager().setTypeFace(getContext(), totalAuctionCountView, R.font.montserrat_extralight);

            nameView.setText(facility.name);
            addressView.setText(facility.address);
            auctionCountView.setText("Active Auctions: " + facility.active);
            totalAuctionCountView.setText("Total Auctions: 4");
            final LatLng point = new LatLng(facility.lat, facility.lon);

            ImageView mapView = (ImageView) rowView.findViewById(R.id.facility_cell_map);
            String getMapURL = "http://maps.googleapis.com/maps/api/staticmap?zoom=15&size=560x240&markers=size:mid|color:red|"
                    + Double.toString(facility.lat)
                    + ","
                    + Double.toString(facility.lon)
                    + "&sensor=false";

            Picasso.get()
                    .load(getMapURL)
                    .resize(300, 300)
                    .centerCrop()
                    .into(mapView);

            return rowView;
        }
    }
}
