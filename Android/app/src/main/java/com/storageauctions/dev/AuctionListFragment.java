package com.storageauctions.dev;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.storageauctions.dev.ServiceManager.Auction;
import com.storageauctions.dev.ServiceManager.AuctionMedia;
import com.storageauctions.dev.ServiceManager.AuctionMeta;
import com.storageauctions.dev.ServiceManager.ServiceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

public class AuctionListFragment extends Fragment {

    public ArrayList<Auction> mAuctionList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auctions, container, false);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.status_tab);
        final ListView listView = (ListView) view.findViewById(R.id.auction_list);

        mAuctionList = new ArrayList<Auction>();
        ProgressView.sharedView().show(getContext());
        ServiceManager.sharedManager().getAllAuction(new JsonHttpResponseHandler() {
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
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray dicArr = response.getJSONArray("auctions");
                    mAuctionList = new ArrayList<Auction>();
                    for (int index = 0; index < dicArr.length(); index++) {
                        JSONObject dic = dicArr.getJSONObject(index);
                        Auction auction = getAuctionFromDic(dic);
                        if (auction.user_id == ServiceManager.sharedManager().user_id) {
                            if (auction.status == 1 || auction.status == 88 || auction.status == 6 || auction.status == 7 || auction.status == 8) {
                                mAuctionList.add(auction);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ProgressView.sharedView().dismiss();
                        AuctionListAdapter adapter = new AuctionListAdapter(getContext(), savedInstanceState, mAuctionList);
                        listView.setAdapter(adapter);
                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Auction auction = mAuctionList.get(position);
                ProgressView.sharedView().show();

                ServiceManager.sharedManager().getAuctionDetail(auction.auct_id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        final JSONObject res = response;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressView.sharedView().dismiss();
                                AuctionDetailFragment fragment = new AuctionDetailFragment();
                                fragment.mAuction = getAuctionFromDic(res);

                                if (fragment != null) {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, fragment);
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String str, Throwable throwable) {
                        ProgressView.sharedView().show("Loading Failed");
                    }
                });
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int[] filters = {-1, 1, 88, 7};
                final ArrayList<Auction> array = new ArrayList<Auction>();
                int filter = filters[tab.getPosition()];

                if (filter == -1) {
                    for (int i = 0; i < mAuctionList.size(); i++) {
                        array.add(mAuctionList.get(i));
                    }
                } else {
                    for (int i = 0; i < mAuctionList.size(); i++) {
                        Auction auction = mAuctionList.get(i);
                        if (tab.getPosition() < 3) {
                            if (auction.status == filter) {
                                array.add(mAuctionList.get(i));
                            }
                        } else {
                            if (auction.status == 6 || auction.status == 7 || auction.status == 8) {
                                array.add(mAuctionList.get(i));
                            }
                        }
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AuctionListAdapter adapter = new AuctionListAdapter(getContext(), savedInstanceState, array);
                        listView.setAdapter(adapter);
                    }
                });
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    public Auction getAuctionFromDic(JSONObject dic) {
        Auction auction = new Auction();
        try { auction.user_id = dic.getInt("user_id"); }catch (JSONException e) { e.printStackTrace(); }

        try { auction.status = dic.getInt("status"); }catch (JSONException e) { e.printStackTrace(); }


            try { auction.auct_id = dic.getInt("id"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.facility_id = dic.getInt("facility_id"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.auction_online_bid_id = dic.getInt("auction_online_bid_id"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.failed_reason = dic.getString("failed_reason"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.descr = dic.getString("descr"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.time_start = getDateFrom(dic.getString("time_start")); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.time_st_zone = dic.getString("time_st_zone"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.time_end = getDateFrom(dic.getString("time_end")); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.time_end_zone = dic.getString("time_end_zone"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.amount_owed = dic.getString("amount_owed"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.fees = dic.getDouble("fees"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.tenant_name = dic.getString("tenant_name"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.unit_number = dic.getString("unit_number"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.unit_size = dic.getInt("unit_size"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.lock_tag = dic.getString("lock_tag"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.time_created = getDateFrom(dic.getString("time_created")); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.time_updated = getDateFrom(dic.getString("time_updated")); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.bid_count = dic.getInt("bid_count"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.winner_code = dic.getInt("winner_code"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.seller_fee = dic.getDouble("seller_fee"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.buyer_fee = dic.getDouble("buyer_fee"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.emailed = dic.getInt("emailed"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.terms = dic.getString("terms"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.queue_flag = dic.getInt("queue_flag"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.auction_id = dic.getString("auction_id"); }catch (JSONException e) { e.printStackTrace(); }
            auction.meta = new AuctionMeta();
            try {
                JSONObject metaDic = dic.getJSONObject("meta");
                if (metaDic.length() > 0) {
                    try { auction.meta.cleanout = metaDic.getInt("cleanout"); }catch (JSONException e) { e.printStackTrace(); }
                    try { auction.meta.cleanout_other = metaDic.getString("cleanout_other"); }catch (JSONException e) { e.printStackTrace(); }
                    try { auction.meta.payment = metaDic.getInt("payment"); }catch (JSONException e) { e.printStackTrace(); }
                    try { auction.meta.payment_other = metaDic.getString("payment_other"); }catch (JSONException e) { e.printStackTrace(); }
                    try { auction.meta.access = metaDic.getInt("access"); }catch (JSONException e) { e.printStackTrace(); }
                    try { auction.meta.currentBidPrice = metaDic.getDouble("ibid_current_price"); }catch (JSONException e) { e.printStackTrace(); }
                    try {
                        JSONArray imageArr = metaDic.getJSONArray("ibid_images");
                        if (imageArr.length() > 0) {
                            auction.meta.images = new ArrayList<String>();
                            for (int j = 0; j < imageArr.length(); j++) {
                                try {
                                    JSONObject imageInfo = imageArr.getJSONObject(j);
                                    Iterator<String> keys = imageInfo.keys();
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        auction.meta.images.add(key);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }catch (JSONException e) { e.printStackTrace(); }

                } else {
                    auction.meta = null;
                }
            }
            catch (JSONException e) { e.printStackTrace(); }

            try { auction.tax = dic.getDouble("tax"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.remote_id = dic.getString("remote_id"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.source_id = dic.getString("source_id"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.reserve = dic.getDouble("reserve"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.bid_schedule_id = dic.getInt("bid_schedule_id"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.alt_unit_number = dic.getString("alt_unit_number"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.batch_email = dic.getInt("batch_email"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.title = dic.getString("title"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.facility_name = dic.getString("facility_name"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.address = dic.getString("address"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.city = dic.getString("city"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.state_code = dic.getString("state_code"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.amount = dic.getDouble("amount"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.bid_user_id = dic.getInt("bid_user_id"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.media_auction_id = dic.getInt("media_auction_id"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.photo_path_size0 = dic.getString("photo_path_size0"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.photo_path_size1 = dic.getString("photo_path_size1"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.photo_path_size2 = dic.getString("photo_path_size2"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.photo_path_size3 = dic.getString("photo_path_size3"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.view_count = dic.getInt("view_count"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.os_date = dic.getString("os_date"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.os_time = dic.getString("os_time"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.url = dic.getString("url"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.group = dic.getString("group"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.type = dic.getString("type"); }catch (JSONException e) { e.printStackTrace(); }
            try { auction.category = dic.getString("category"); }catch (JSONException e) { e.printStackTrace(); }
            try {
                JSONArray mediaData = dic.getJSONArray("all_media");
                auction.mediaArray = new ArrayList<AuctionMedia>();
                for (int ind = 0; ind < mediaData.length(); ind++) {
                    JSONObject data = mediaData.getJSONObject(ind);
                    AuctionMedia media = new AuctionMedia();
                    media.mediaID = data.getInt("id");
                    media.type = data.getString("type");
                    media.height0 = data.getString("height_0");
                    media.height1 = data.getString("height_1");
                    media.height2 = data.getString("height_2");
                    media.height3 = data.getString("height_3");
                    media.url0 = data.getString("photo_url_size0");
                    media.url1 = data.getString("photo_url_size1");
                    media.url2 = data.getString("photo_url_size2");
                    media.url3 = data.getString("photo_url_size3");
                    media.width0 = data.getString("width_0");
                    media.width1 = data.getString("width_1");
                    media.width2 = data.getString("width_2");
                    media.width3 = data.getString("width_3");
                    auction.mediaArray.add(media);
                }
            }catch (JSONException e) { e.printStackTrace(); }

        return auction;
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

    public class AuctionListAdapter extends BaseAdapter {

        LayoutInflater mLayoutInflater;
        ArrayList<Auction> mAuctionList;

        public AuctionListAdapter(Context fragment, Bundle savedInstance, ArrayList<Auction> list) {
            mLayoutInflater = LayoutInflater.from(fragment);
            mAuctionList = list;
        }

        @Override
        public int getCount() {
            return mAuctionList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAuctionList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = mLayoutInflater.inflate(R.layout.list_auction, parent, false);

            final Auction auction = mAuctionList.get(position);

            ImageButton viewButton = (ImageButton) rowView.findViewById(R.id.view_button);
            viewButton.setFocusable(false);
            viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressView.sharedView().show();

                    ServiceManager.sharedManager().getAuctionDetail(auction.auct_id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            final JSONObject res = response;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AuctionDetailFragment fragment = new AuctionDetailFragment();
                                    fragment.mAuction = getAuctionFromDic(res);

                                    if (fragment != null) {
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, fragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            ProgressView.sharedView().show("Loading Failed");
                        }
                    });
                }
            });

            ImageView imageView = (ImageView) rowView.findViewById(R.id.auction_cell_image);
            TextView auctionTitle = (TextView) rowView.findViewById(R.id.auction_cell_title);
            TextView auctionAddress = (TextView) rowView.findViewById(R.id.auction_cell_address);
            TextView auctionViews = (TextView) rowView.findViewById(R.id.auction_cell_view_count);
            TextView auctionBids = (TextView) rowView.findViewById(R.id.auction_cell_bid_count);
            TextView timeLeft = (TextView) rowView.findViewById(R.id.auction_cell_time_left);
            TextView currentBid = (TextView) rowView.findViewById(R.id.auction_cell_current_bid);
            TextView status = (TextView) rowView.findViewById(R.id.auction_cell_status);

            ServiceManager.sharedManager().setTypeFace(getContext(), auctionTitle, R.font.montserrat_medium);
            ServiceManager.sharedManager().setTypeFace(getContext(), auctionAddress, R.font.montserrat_extralight);
            ServiceManager.sharedManager().setTypeFace(getContext(), auctionViews, R.font.montserrat_medium);
            ServiceManager.sharedManager().setTypeFace(getContext(), auctionBids, R.font.montserrat_medium);
            ServiceManager.sharedManager().setTypeFace(getContext(), timeLeft, R.font.montserrat_medium);
            ServiceManager.sharedManager().setTypeFace(getContext(), currentBid, R.font.montserrat_medium);
            ServiceManager.sharedManager().setTypeFace(getContext(), status, R.font.montserrat_medium);

            ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) rowView.findViewById(R.id.view_title), R.font.montserrat_extralight);
            ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) rowView.findViewById(R.id.time_title), R.font.montserrat_extralight);
            ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) rowView.findViewById(R.id.bid_title), R.font.montserrat_extralight);
            ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) rowView.findViewById(R.id.current_bid_title), R.font.montserrat_extralight);
            ServiceManager.sharedManager().setTypeFace(getContext(), (TextView) rowView.findViewById(R.id.status_title), R.font.montserrat_extralight);

            Picasso.get().load(auction.photo_path_size1).into(imageView);

            auctionTitle.setText(auction.title);
            auctionAddress.setText(auction.address);
            auctionViews.setText(Integer.toString(auction.view_count));
            auctionBids.setText(Integer.toString(auction.bid_count));
            long diff = this.getDifferentDate(auction.time_end);
            timeLeft.setText(String.format("%dd Left", diff));

            if (auction.meta != null && auction.meta.currentBidPrice != 0) {
                currentBid.setText("$" + String.format("%1$,.2f", auction.meta.currentBidPrice));
            } else {
                currentBid.setText("$0");
            }

            switch (auction.status) {
                case 0:
                    status.setText("INACTIVE");
                    break;
                case 1:
                    status.setText("ACTIVE");
                    break;
                case 2:
                    status.setText("CLOSED");
                    break;
                case 3:
                    status.setText("PAID");
                    break;
                case 4:
                    status.setText("UNSOLD");
                    break;
                case 5:
                    status.setText("FAILED PAYMENT");
                    break;
                case 6:
                    status.setText("CANCELED");
                    break;
                case 7:
                    // status.setText("Status: CANCELED PAID");
                    status.setText("CANCELED");
                    break;
                case 8:
                    // status.setText("Status: CANCELLED FAILED");
                    status.setText("CANCELED");
                    break;
                case 9:
                    status.setText("FAILED FINAL");
                    break;
                case 10:
                    status.setText("PENDING");
                    break;
                case 11:
                    status.setText("ONSITE");
                    break;
                case 88:
                    status.setText("UPCOMING");
                    break;
                default:
                    status.setText("PROCESSING");
                    break;
            }

            return rowView;
        }

        public long getDifferentDate(Date endDate) {
            long diffSecs = endDate.getTime() - new Date().getTime();
            if (diffSecs > 0)
                return TimeUnit.DAYS.convert(diffSecs, TimeUnit.MILLISECONDS);
            else
                return 0;
        }
    }
}
