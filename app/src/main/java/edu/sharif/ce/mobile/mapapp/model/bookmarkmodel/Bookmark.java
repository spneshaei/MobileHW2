package edu.sharif.ce.mobile.mapapp.model.bookmarkmodel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seyyed Parsa Neshaei on 4/15/21
 * All Rights Reserved
 */
@Entity(tableName = "bookmark")
public class Bookmark implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "lat")
    private double lat;
    @ColumnInfo(name = "lon")
    private double lon;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public Bookmark(int id, String name, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    @Ignore
    public Bookmark(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public static Bookmark getBookmarkByName(String name, List<Bookmark> bookmarks) {
        for (Bookmark bookmark : bookmarks) {
            if (bookmark.name.equals(name))
                return bookmark;
        }
        return null;
    }
}
