package edu.sharif.ce.mobile.mapapp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import edu.sharif.ce.mobile.mapapp.R;
import edu.sharif.ce.mobile.mapapp.model.bookmarkmodel.Bookmark;

/**
 * Created by Seyyed Parsa Neshaei on 4/15/21
 * All Rights Reserved
 */
public class BookmarkRecyclerViewAdapter extends RecyclerView.Adapter<BookmarkRecyclerViewAdapter.ViewHolder> {
    private List<Bookmark> data;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;
    private ItemClickListener deleteClickListener;

    public BookmarkRecyclerViewAdapter(Context context, List<Bookmark> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.bookmark_recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bookmark bookmark = data.get(position);
        holder.txtTitle.setText(bookmark.getName());
        DecimalFormat df = new DecimalFormat("#.###");
        holder.txtLatLon.setText("Lat: " + df.format(bookmark.getLat()) + " - Lon: " + df.format(bookmark.getLon()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTitle;
        TextView txtLatLon;
        ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.bookmarksRecyclerView_txtTitle);
            txtLatLon = itemView.findViewById(R.id.bookmarksRecyclerView_txtLatLon);
            btnDelete = itemView.findViewById(R.id.bookmarksRecyclerView_btnDelete);
            btnDelete.setOnClickListener(view -> {
                if (deleteClickListener != null) deleteClickListener.onItemClick(view, getAdapterPosition());
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    Bookmark getItem(int id) {
        return data.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    void setDeleteClickListener(ItemClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
