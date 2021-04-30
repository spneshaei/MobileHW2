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

import edu.sharif.ce.mobile.mapapp.R;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmark;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationCenter;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationID;
import edu.sharif.ce.mobile.mapapp.ui.dashboard.DashboardFragment;


public class NetworkInterface {
    private final static String API_KEY_FOR_MAP_BOX = "pk.eyJ1IjoiYWJvb3RzIiwiYSI6ImNrbmtta2JpYjA5aDAyd21wOWhvOXVpc3IifQ.ykVD39lvqcfKuAM03mGWPg";
    public static ArrayList<String> searchNames=new ArrayList<>();

    public static void getLocData(String location) {
        OkHttpClient okHttpClient = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.mapbox.com/geocoding/v5/mapbox.places/"
                .concat(String.valueOf(location)).concat(".json?access_token=").concat(API_KEY_FOR_MAP_BOX))
                .newBuilder();

        String url = urlBuilder.build().toString();

        final Request request = new Request.Builder().url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("network", Objects.requireNonNull(e.getMessage()));
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("network", response.body().string());
                } else {
                    String body = response.body().string();
                    Log.d("response", body);
                    try {
                        ArrayList<Bookmark> bookmarks = new ArrayList<>();
                        ArrayList<String> names = new ArrayList<>();
                        JSONObject outerObj = new JSONObject(body);
                        JSONArray data_array = new JSONArray(outerObj.getString("features"));
                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject object = (JSONObject) data_array.get(i);
                            JSONArray inner_object = object.getJSONArray("center");
                            String name = object.getString("place_name");
                            double lon = Double.parseDouble(String.valueOf(inner_object.get(0)));
                            double lat = Double.parseDouble(String.valueOf(inner_object.get(1)));
                            bookmarks.add(new Bookmark(name,lat,lon));
                            names.add(name);
                        }
                        searchNames.clear();
                        searchNames.addAll(names);
                        System.out.println(searchNames);
                        NotificationCenter.notify(NotificationID.TopRelatedSearches.NEW_DATA_LOADED_FOR_UI);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}

