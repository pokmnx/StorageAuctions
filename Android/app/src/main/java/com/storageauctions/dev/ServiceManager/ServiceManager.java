package com.storageauctions.dev.ServiceManager;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ServiceManager {

    private static ServiceManager g_ServiceManager;
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static final int TIME_OUT = 70000;

    private final String endPoint = "https://dev3.storageauctions.net/block";
    private Context mContext = null;

    private ServiceManager() {

    }

    public static ServiceManager sharedManager() {
        if (g_ServiceManager == null) {
            g_ServiceManager = new ServiceManager();
            client.setTimeout(TIME_OUT);
            client.setConnectTimeout(TIME_OUT);
            client.setResponseTimeout(TIME_OUT);
        }
        return g_ServiceManager;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void login(String username, String password, JsonHttpResponseHandler handler) {
        String url = endPoint + "/user/login";
        RequestParams params = new RequestParams();
        params.add("email", username);
        params.add("password", password);
        postRequest(url, params, handler);
    }

    public void signup(String email, String password, String username, String phone, String name, String role, JsonHttpResponseHandler handler) {
        String url = endPoint + "/user/set";
        RequestParams params = new RequestParams();
        params.add("email", email);
        params.add("password", password);
        params.add("username", username);
        params.add("phone", phone);
        params.add("name", name);
        params.add("role", role);

        postRequest(url, params, handler);
    }

    public void addNewFacility(String name, String email, String website, String phone, String taxRate, String commissionRate, String terms, String[] address, JsonHttpResponseHandler handler) {
        String url = endPoint + "/facility/set";
        RequestParams params = new RequestParams();
        params.add("name", name);
        params.add("website", website);
        params.add("email", email);
        params.add("commission_rate", commissionRate);
        params.add("taxrate", taxRate);
        params.add("terms", terms);
        params.add("lat", address[0]);
        params.add("lon", address[1]);
        params.add("acc", address[2]);
        params.add("phone", phone);
        params.add("address", address[3]);
        params.add("city", address[4]);
        params.add("postal_code", address[5]);
        params.add("state_code", address[6]);

        postRequest(url, params, handler);
    }

    public void createAuction(AuctionRequest request, JsonHttpResponseHandler handler) {
        String url = endPoint + "/auction/add";
        RequestParams params = new RequestParams();

        params.add("amount_owed", request.amount_owed);
        params.add("alt_unit_number", request.alt_unit_number);
        params.add("reserve", request.reserve);
        params.add("time_end", request.time_end);
        params.add("terms", request.terms);
        params.add("prebid_close", request.prebid_close);
        params.add("lock_tag", request.lock_tag);
        params.add("fees", request.fees);
        params.add("batch_email", request.batch_email);
        params.add("cleanout_other", request.cleanout_other);
        params.add("payment", request.payment);
        params.add("time_st_zone", request.time_st_zone);
        params.add("cleanout", request.cleanout);
        params.add("access", request.access);
        params.add("auction_type", request.auction_type);
        params.add("time_start", request.time_start);
        params.add("facility_id", request.facility_id);
        params.add("unit_size", request.unit_size);
        params.add("descr", request.descr);
        params.add("unit_number", request.unit_number);
        params.add("time_end_zone", request.time_end_zone);
        params.add("save_terms", request.save_terms);
        params.add("payment_other", request.payment_other);
        params.add("tenant_name", request.tenant_name);

        postRequest(url, params, handler);
    }

    public void getAllFacilities(JsonHttpResponseHandler handler) {
        String url = endPoint + "/user/facility/get";
        getRequest(url, handler);
    }

    public void getRequest(String url, JsonHttpResponseHandler handler) {
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", "Basic ZGV2OnNhc2E=");
        client.get(url, handler);
    }

    public void postRequest(String url, RequestParams params, JsonHttpResponseHandler handler) {
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", "Basic ZGV2OnNhc2E=");
        client.post(url, params, handler);
    }

}
