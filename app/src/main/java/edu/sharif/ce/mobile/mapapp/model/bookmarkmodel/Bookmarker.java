package edu.sharif.ce.mobile.mapapp.model.bookmarkmodel;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationCenter;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationID;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.Subscriber;

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

    public static void reloadBookmarkList(Context context) {
        executor.execute(() -> {
            BookmarkDatabase db = BookmarkDatabase.getInstance(context);
            bookmarkList.clear();
            bookmarkList.addAll(db.bookmarkDao().getBookmarkList());
            NotificationCenter.notify(NotificationID.Bookmarks.DATA_LOADED_FROM_DB);
        });
    }

    public static void insertBookmark(Context context, String name, double lat, double lon) {
        executor.execute(() -> {
            BookmarkDatabase db = BookmarkDatabase.getInstance(context);
            Bookmark bookmark = new Bookmark(name, lat, lon);
            db.bookmarkDao().insertBookmark(bookmark);
            NotificationCenter.notify(NotificationID.Bookmarks.DATA_INSERTED_INTO_DB);
            reloadBookmarkList(context);
        });
    }

    public static void deleteBookmark(Context context, Bookmark bookmark) {
        executor.execute(() -> {
            BookmarkDatabase db = BookmarkDatabase.getInstance(context);
            db.bookmarkDao().deleteBookmark(bookmark);
            NotificationCenter.notify(NotificationID.Bookmarks.DATA_REMOVED_FROM_DB);
            reloadBookmarkList(context);
        });
    }

    public static void deleteAllBookmarks(Context context) {
        executor.execute(() -> {
            BookmarkDatabase db = BookmarkDatabase.getInstance(context);
            db.bookmarkDao().deleteTable();
            NotificationCenter.notify(NotificationID.Bookmarks.TABLE_REMOVED_FROM_DB);
            // TODO: Other tables to remove too??
            // TODO: Should we also save preferences in the preferences pane to the SQLite Room?
            reloadBookmarkList(context);
        });
    }

//    @Override
//    public boolean sendEmptyMessage(int what) {
//        if (what == NotificationID.Bookmarks.DATA_INSERTED_INTO_DB || what == NotificationID.Bookmarks.DATA_REMOVED_FROM_DB) {
//            reloadBookmarkList(getApplicationContext());
//        }
//        return false;
//    }
}
