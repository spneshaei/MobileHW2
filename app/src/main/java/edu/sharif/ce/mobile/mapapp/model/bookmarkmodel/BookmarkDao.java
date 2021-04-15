package edu.sharif.ce.mobile.mapapp.model.bookmarkmodel;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Created by Seyyed Parsa Neshaei on 4/15/21
 * All Rights Reserved
 */

@Dao
public interface BookmarkDao {
    @Query("Select * from bookmark")
    List<Bookmark> getBookmarkList();

    @Insert
    void insertBookmark(Bookmark bookmark);

    @Delete
    void deleteBookmark(Bookmark bookmark);
}
