package edu.sharif.ce.mobile.mapapp.ui.home;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import edu.sharif.ce.mobile.mapapp.R;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmark;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmarker;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationCenter;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationID;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.Subscriber;

public class HomeFragment extends Fragment {

    BookmarkRecyclerViewAdapter adapter;
    ArrayList<Bookmark> filteredBookmarks = new ArrayList<>();
    String searchTerm = "";
    TextView noBookmarksText;

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
                    fragment.notifyDataSetChanged();
                }
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        noBookmarksText = root.findViewById(R.id.no_bookmarks_text);

        RecyclerView recyclerView = root.findViewById(R.id.bookmarksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notifyDataSetChanged();
        adapter.setClickListener((view, position) -> openBookmark(position));
        adapter.setDeleteClickListener((view, position) -> showDeleteAlertDialog(position));
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

    public void openBookmark(int position) {
        Bookmark bookmark = adapter.getItem(position);
        // TODO: Use that bookmark and open it in the second tab...
        Log.d("BOOKMARKName", bookmark.getName());
    }

    public void deleteBookmark(Bookmark bookmark) {
        Bookmarker.deleteBookmark(getContext(), bookmark);
    }

    private void showDeleteAlertDialog(int position) {
        Bookmark bookmark = adapter.getItem(position);
        String messageBuilder = getString(R.string.delete_bookmark_message) + "\n" +
                bookmark.getName() + "\n" +
                "Lat: " + bookmark.getLat() + ", Lng: " + bookmark.getLon();
        MaterialAlertDialogBuilder alertBuilder = new MaterialAlertDialogBuilder(getContext());
        alertBuilder.setTitle(getString(R.string.delete_bookmark_title))
                .setMessage(messageBuilder)
                .setPositiveButton(getString(R.string.proceed_button), (dialogInterface, i) -> deleteBookmark(bookmark))
                .setCancelable(true);
        alertBuilder.show();

    }

    public void notifyDataSetChanged() {
        filteredBookmarks.clear();
        if (searchTerm.equals("")) {
            filteredBookmarks.addAll(Bookmarker.getBookmarkList());
        } else {
            for (Bookmark bookmark : Bookmarker.getBookmarkList()) {
                if (bookmark.getName().contains(searchTerm)) filteredBookmarks.add(bookmark);
            }
        }
        if (adapter == null) {
            adapter = new BookmarkRecyclerViewAdapter(getContext(), filteredBookmarks);
        } else {
            adapter.notifyDataSetChanged();
        }
        noBookmarksText.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}