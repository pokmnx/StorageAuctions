package com.storageauctions.dev.ServiceManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.TextView;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


public class ServiceManager {

    private static ServiceManager g_ServiceManager;
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static OkHttpClient okClient;
    private static final int TIME_OUT = 70000;

    private final String endPoint = "https://dev3.storageauctions.net/block";
    private Context mContext = null;

    public int user_id;
    public String user_name;
    public boolean bLoginFirstTime = false;

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
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(mContext));
        okClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .build();
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

    public void setAuction(AuctionRequest request, JsonHttpResponseHandler handler) {
        String url = endPoint + "/auction/set";

        RequestParams params = new RequestParams();

        params.add("id", request.auct_id);
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

        if (request.status != null && request.status.compareTo("6") == 0) {
            params.add("status", request.status);
        }

        postRequest(url, params, handler);
    }

    public void getAllAuction(JsonHttpResponseHandler handler) {
        String url = endPoint + "/user/selling";
        getRequest(url, handler);
    }

    public void getAllFacilities(JsonHttpResponseHandler handler) {
        String url = endPoint + "/user/facility/get";
        getRequest(url, handler);
    }

    public void getAuctionDetail(int auctionID, JsonHttpResponseHandler handler) {
        String url = endPoint + "/auction/get/" + auctionID;
        getRequest(url, handler);
    }

    public void setImage(int auctionID, String filePath, JsonHttpResponseHandler handler) {
        String url = endPoint + "/auction/media/set";
        RequestParams params = new RequestParams();
        File file = new File(filePath);
        try {
            params.put("auction_id", String.format("%d", auctionID));
            params.setForceMultipartEntityContentType(true);
            params.put("media", file, "image/jpeg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        client.addHeader("Authorization", "Basic ZGV2OnNhc2E=");
        client.post(url, params, handler);
    }

    public void deleteImage(int auctionID, String mediaID, JsonHttpResponseHandler handler) {
        String url = endPoint + "/auction/media/delete";
        RequestParams params = new RequestParams();
        params.add("id", mediaID);
        postRequest(url, params, handler);
    }

    public void setImage(Context context, final int auctionID, final String filePath, final Callback callback) {
        mContext = context;
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(mContext));
        okClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .build();
        String loginURL = endPoint + "/user/login";
        RequestBody reqBody = new FormBody.Builder()
                .add("email", "ro.bert.miner@gmail.com")
                .add("password", "miner001").build();
        Request req = new Request.Builder().url(loginURL)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic ZGV2OnNhc2E=")
                .post(reqBody)
                .build();

        okClient.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("error", "login error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("success", "login success");
                String jsonStr = response.body().string();
                try {
                    JSONObject loginJSON = new JSONObject(jsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = endPoint + "/auction/media/set";
                File file = new File(filePath);
                try {
                    String fileName = file.getName();
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("auction_id", String.format("%d", auctionID))
                            .addFormDataPart("media", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .header("Authorization", "Basic ZGV2OnNhc2E=")
                            .post(requestBody)
                            .build();

                    okClient.newCall(request).enqueue(callback);
                } catch (Exception ex) {
                    // Handle the error
                }
            }
        });
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

    public void setTypeFace(Context context, TextView view, int fontId) {
        if (context == null || view == null)
            return;
        Typeface inputTypeface = ResourcesCompat.getFont(context, fontId);
        view.setTypeface(inputTypeface);
    }

    public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }
}
