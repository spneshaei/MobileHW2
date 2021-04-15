package edu.sharif.ce.mobile.mapapp.model.utils;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationCenter;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationID;


public class NetworkInterface {
//    private final static String API_KEY_FOR_COIN_MARKET_CAP = "8c4eece6-2099-4a21-982e-7880a4d2a090"; //"ae590806-c68e-46c5-8577-e5640c7d4b41";
//    private final static String API_KEY_FOR_COIN_API = "1D0238A5-FEDF-42EB-86F4-AE838AE2C9E7";

    public static void getCryptoData(final int start, int limit) {
//        OkHttpClient okHttpClient = new OkHttpClient();
//
//        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?limit="
//                .concat(String.valueOf(limit)).concat("&start=".concat(String.valueOf(start))))
//                .newBuilder();
//
//        String url = urlBuilder.build().toString();
//
//        final Request request = new Request.Builder().url(url)
//                .addHeader("X-CMC_PRO_API_KEY", API_KEY_FOR_COIN_MARKET_CAP)
//                .build();
//
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                Log.e("network", Objects.requireNonNull(e.getMessage()));
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//
//            }
//        });

    }
}

