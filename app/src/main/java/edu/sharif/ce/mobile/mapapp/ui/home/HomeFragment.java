package edu.sharif.ce.mobile.mapapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import edu.sharif.ce.mobile.mapapp.MainActivity;
import edu.sharif.ce.mobile.mapapp.R;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmark;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmarker;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationCenter;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.NotificationID;
import edu.sharif.ce.mobile.mapapp.model.notifhandling.Subscriber;
import edu.sharif.ce.mobile.mapapp.ui.dashboard.DashboardFragment;

public class HomeFragment extends Fragment {

    BookmarkRecyclerViewAdapter adapter;
    ArrayList<Bookmark> filteredBookmarks = new ArrayList<>();
    String searchTerm = "";
    TextView noBookmarksText;
    EditText txtSearch;

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
        txtSearch = root.findViewById(R.id.txtSearch);

        RecyclerView recyclerView = root.findViewById(R.id.bookmarksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notifyDataSetChanged();
        adapter.setClickListener((view, position) -> openBookmark(position));
        adapter.setDeleteClickListener((view, position) -> showDeleteAlertDialog(position));
        recyclerView.setAdapter(adapter);

        NotificationCenter.registerForNotification(this.handler, NotificationID.Bookmarks.DATA_LOADED_FROM_DB);
        Bookmarker.reloadBookmarkList(getContext());

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchTerm = String.valueOf(charSequence);
                notifyDataSetChanged();
            }
            @Override public void afterTextChanged(Editable editable) { }
        });

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (adapter != null) Bookmarker.reloadBookmarkList(getContext());
    }

    public void openBookmark(int position) {
        Bookmark bookmark = adapter.getItem(position);

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        Bundle args = new Bundle();
        args.putSerializable("bookmark", bookmark);
        navController.navigate(R.id.navigation_dashboard, args);
        Log.d("BOOKMARKName", bookmark.getName());
    }

    public void deleteBookmark(Bookmark bookmark) {
        Bookmarker.deleteBookmark(getContext(), bookmark);
    }

    private void showDeleteAlertDialog(int position) {
        Bookmark bookmark = adapter.getItem(position);
        String messageBuilder = getString(R.string.delete_bookmark_message) + "\n" +
                bookmark.getName() + "\n" +
                "Latitute: " + bookmark.getLat() + ", Longitude: " + bookmark.getLon();
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
        } else adapter.notifyDataSetChanged();
        noBookmarksText.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}