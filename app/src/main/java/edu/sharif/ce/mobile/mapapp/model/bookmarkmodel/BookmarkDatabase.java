package edu.sharif.ce.mobile.mapapp.model.bookmarkmodel;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Created by Seyyed Parsa Neshaei on 4/15/21
 * All Rights Reserved
 */

@Database(entities = Bookmark.class, exportSchema = false, version = 1)
public abstract class BookmarkDatabase extends RoomDatabase {
    private static final String DB_NAME = "bookmark_db";
    private static BookmarkDatabase instance;

    public static synchronized BookmarkDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    BookmarkDatabase.class,
                    DB_NAME).fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public abstract BookmarkDao bookmarkDao();
}
