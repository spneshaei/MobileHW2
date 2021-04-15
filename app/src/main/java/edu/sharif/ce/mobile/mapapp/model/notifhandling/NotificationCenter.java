package edu.sharif.ce.mobile.mapapp.model.notifhandling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Seyyed Parsa Neshaei on 2/28/21
 * All Rights Reserved
 */
public class NotificationCenter {
    private static HashMap<Integer, ArrayList<edu.sharif.ce.mobile.mapapp.model.notifhandling.Subscriber>> subscribers = new HashMap<>();

    public static void registerForNotification(edu.sharif.ce.mobile.mapapp.model.notifhandling.Subscriber subscriber, int id) {
        if (!subscribers.containsKey(id) || subscribers.get(id) == null) {
            ArrayList<edu.sharif.ce.mobile.mapapp.model.notifhandling.Subscriber> newList = new ArrayList<>();
            newList.add(subscriber);
            subscribers.put(id, newList);
            return;
        }
        if (!Objects.requireNonNull(subscribers.get(id)).contains(subscriber)) {
            Objects.requireNonNull(subscribers.get(id)).add(subscriber);
        }
    }

    public static void notify(int id) {
        if (!subscribers.containsKey(id)) return;
        ArrayList<edu.sharif.ce.mobile.mapapp.model.notifhandling.Subscriber> arrayList = subscribers.get(id);
        if (arrayList == null) return;
        for (edu.sharif.ce.mobile.mapapp.model.notifhandling.Subscriber subscriber : arrayList) {
            subscriber.sendEmptyMessage(id);
        }
    }
}
