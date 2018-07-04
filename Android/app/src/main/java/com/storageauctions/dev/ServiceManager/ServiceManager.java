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
