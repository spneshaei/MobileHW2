package edu.sharif.ce.mobile.mapapp.model.notifhandling;

/**
 * Created by Seyyed Parsa Neshaei on 2/28/21
 * All Rights Reserved
 */
public class NotificationID {
    public static class TopRelatedSearches {
        public static int DATA_LOADED_FROM_CACHE = 1;
        public static int NO_INTERNET_CONNECTION = 2;
        public static int NEW_DATA_LOADED_FOR_RESTER = 3;
        public static int NEW_DATA_LOADED_FOR_UI = 4;
    }

    public static class Bookmarks {
        public static int DATA_LOADED_FROM_DB = 5;
        public static int DATA_REMOVED_FROM_DB = 6;
        public static int DATA_INSERTED_INTO_DB = 7;
    }
}
