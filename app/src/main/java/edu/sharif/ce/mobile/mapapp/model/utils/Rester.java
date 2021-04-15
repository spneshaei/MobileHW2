package edu.sharif.ce.mobile.mapapp.model.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationCenter;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationID;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.Subscriber;

/**
 * Created by Seyyed Parsa Neshaei on 2/28/21
 * All Rights Reserved
 */
public class Rester implements Subscriber {
    private static final edu.sharif.ce.mobile.mapapp.model.utils.Rester ourInstance = new edu.sharif.ce.mobile.mapapp.model.utils.Rester();
    private ThreadPoolExecutor executor;
    private Date timeOfLastRequest = Calendar.getInstance().getTime();
    private boolean isFirstRequest = true;
    public static edu.sharif.ce.mobile.mapapp.model.utils.Rester getInstance() {
        return ourInstance;
    }

    private boolean isFirstRequest() {
        return isFirstRequest;
    }

    private void didFirstRequest() {
        isFirstRequest = false;
    }

    private synchronized long getTimeOfLastRequest() {
        if (isFirstRequest()) return 0;
        return timeOfLastRequest.getTime();
    }

    private synchronized void setTimeOfLastRequest(Date timeOfLastRequest) {
        didFirstRequest();
        this.timeOfLastRequest = timeOfLastRequest;
    }

    private Rester() {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    }

    private synchronized boolean isConnected() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
            sock.connect(sockaddr, timeoutMs);
            sock.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private synchronized String readFromFile(Context context, String file) throws Exception {
        InputStream inputStream = context.openFileInput(file);
        if (inputStream == null) return "";
        String ret = "";
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString;
        StringBuilder stringBuilder = new StringBuilder();
        while ((receiveString = bufferedReader.readLine()) != null) {
            stringBuilder.append("\n").append(receiveString);
        }
        inputStream.close();
        ret = stringBuilder.toString();
        return ret;
    }
    private synchronized void writeToFile(String file, Context context, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(file, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("WriteToFile-Rester", "File write failed: " + e.toString());
        }
    }

    @Override
    public boolean sendEmptyMessage(int what) {

//        if (what == NotificationID.Crypto.NEW_DATA_LOADED_FOR_RESTER) {
//            writeToFile("crypto.txt", MyApplication.getAppContext(), new Gson().toJson(Crypto.getCryptos()));
//        }
//        NotificationCenter.notify(NotificationID.Crypto.NEW_DATA_LOADED_FOR_UI);

        return false;
    }
}
