package edu.sharif.ce.mobile.mapapp.model.bookmarkmodel;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationCenter;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationID;

/**
 * Created by Seyyed Parsa Neshaei on 4/15/21
 * All Rights Reserved
 */
public class Bookmarker {
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    private static final List<Bookmark> bookmarkList = new ArrayList<>();

    public static List<Bookmark> getBookmarkList() {
        return bookmarkList;
    }

    // Attention: the next method MUST be run on a worker's thread and not on the UI thread
    public static void reloadBookmarkList(Context context) {
        executor.execute(() -> {
            BookmarkDatabase db = BookmarkDatabase.getInstance(context);
            bookmarkList.clear();
            bookmarkList.addAll(db.bookmarkDao().getBookmarkList());
            NotificationCenter.notify(NotificationID.Bookmarks.DATA_LOADED_FROM_DB);
        });
    }

    // Attention: the next method MUST be run on a worker's thread and not on the UI thread
    public static void insertBookmark(Context context, String name, double lat, double lon) {
        executor.execute(() -> {
            BookmarkDatabase db = BookmarkDatabase.getInstance(context);
            Bookmark bookmark = new Bookmark(name, lat, lon);
            db.bookmarkDao().insertBookmark(bookmark);
            NotificationCenter.notify(NotificationID.Bookmarks.DATA_INSERTED_INTO_DB);
        });
    }

    // Attention: the next method MUST be run on a worker's thread and not on the UI thread
    public static void deleteBookmark(Context context, Bookmark bookmark) {
        executor.execute(() -> {
            BookmarkDatabase db = BookmarkDatabase.getInstance(context);
            db.bookmarkDao().deleteBookmark(bookmark);
            NotificationCenter.notify(NotificationID.Bookmarks.DATA_REMOVED_FROM_DB);
        });
    }
}
