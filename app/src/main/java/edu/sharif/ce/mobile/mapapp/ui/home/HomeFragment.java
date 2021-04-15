package edu.sharif.ce.mobile.mapapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;

import edu.sharif.ce.mobile.mapapp.R;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmark;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmarker;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationCenter;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationID;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.Subscriber;

public class HomeFragment extends Fragment {

    BookmarkRecyclerViewAdapter adapter;

    private final WeakHandler handler = new WeakHandler(this);

    private static class WeakHandler extends Handler implements Subscriber {
        private final WeakReference<HomeFragment> fragment;

        public WeakHandler(HomeFragment fragment) {
            this.fragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            HomeFragment fragment = this.fragment.get();
            if (fragment != null) {
                if (msg.what == NotificationID.Bookmarks.DATA_LOADED_FROM_DB) {
                    fragment.adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.bookmarksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookmarkRecyclerViewAdapter(getContext(), Bookmarker.getBookmarkList());
        adapter.setClickListener((view, position) -> {
            Bookmark bookmark = adapter.getItem(position);
            // TODO: Use that bookmark and open it in the second tab...
            Log.d("BOOKMARKName", bookmark.getName());
        });
        recyclerView.setAdapter(adapter);

        NotificationCenter.registerForNotification(this.handler, NotificationID.Bookmarks.DATA_LOADED_FROM_DB);
        Bookmarker.reloadBookmarkList(getContext());

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (adapter != null) Bookmarker.reloadBookmarkList(getContext());
    }
}