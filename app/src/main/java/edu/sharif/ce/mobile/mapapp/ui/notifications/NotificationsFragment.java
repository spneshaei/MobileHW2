package edu.sharif.ce.mobile.mapapp.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.UUID;

import edu.sharif.ce.mobile.mapapp.R;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmark;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmarker;

public class NotificationsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final Button btnAddRandomBookmark = root.findViewById(R.id.btnAddRandomBookmark);
        final Button btnClearData = root.findViewById(R.id.btnClearAllData);
        btnAddRandomBookmark.setOnClickListener(view -> insertRandomBookmark());
        btnClearData.setOnClickListener(view -> clearData());
        return root;
    }

    public void clearData() {
        // TODO: Alert view before cleaning!
        Bookmarker.deleteAllBookmarks(getContext());
        // TODO: Also delete SharedPrefs
        // getContext().getSharedPreferences("YOUR_PREFS", 0).edit().clear().commit();
        // TODO: Also delete caches
        // TODO: Rotation problem in app!!!
    }

    public void insertRandomBookmark() {
        Bookmarker.insertBookmark(getContext(), UUID.randomUUID().toString(), 1.0, 2.0);
    }
}