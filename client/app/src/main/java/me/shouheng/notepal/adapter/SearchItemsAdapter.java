package me.shouheng.notepal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.R;
import me.shouheng.notepal.util.ColorUtils;
import me.shouheng.notepal.util.TimeUtils;
import me.urakalee.next2.model.Note;

/**
 * Created by wangshouheng on 2017/5/8.
 */
public class SearchItemsAdapter extends RecyclerView.Adapter<SearchItemsAdapter.ViewHolder> {

    private List searchResults = new LinkedList();

    private Context context;

    private boolean isDarkTheme;
    private int accentColor, primaryColor;

    private final int NOTE = 2, STRING = 5;

    private OnItemSelectedListener onItemSelectedListener;

    public SearchItemsAdapter(Context context, OnItemSelectedListener onItemSelectedListener) {
        this.context = context;

        this.isDarkTheme = ColorUtils.isDarkTheme(context);
        this.accentColor = ColorUtils.accentColor(context);
        this.primaryColor = ColorUtils.primaryColor(context);
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(viewType, LayoutInflater.from(context).inflate(
                viewType == NOTE ? R.layout.notes_item_notebook : R.layout.item_section_title, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (isDarkTheme) holder.itemView.setBackgroundResource(R.color.dark_theme_foreground);

        if (viewType == NOTE) {
            onBindNote(holder, position);
        } else {
            if (holder.getItemViewType() == STRING) {
                holder.tvSectionTitle.setText((String) searchResults.get(position));
                holder.tvSectionTitle.setTextColor(primaryColor);
                holder.itemView.setBackgroundResource(
                        isDarkTheme ? R.color.dark_theme_background : R.color.light_theme_background);
            }
        }
    }

    private void onBindNote(ViewHolder holder, final int position) {
        Note note = (Note) searchResults.get(position);
        holder.tvNoteTitle.setText(note.getTitle());
        holder.tvAddedTime.setText(TimeUtils.getLongDateTime(context, note.getAddedTime()));
    }

    @Override
    public int getItemCount() {
        if (null != searchResults) {
            return searchResults.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (searchResults.get(position) instanceof Note) {
            return NOTE;
        }
        return STRING;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View itemView;

        TextView tvSectionTitle;

        TextView tvNoteTitle;
        TextView tvAddedTime;

        ImageView ivMore;

        ViewHolder(int viewType, View itemView) {
            super(itemView);

            this.itemView = itemView;

            if (viewType == NOTE) {
                tvSectionTitle = null;

                tvNoteTitle = itemView.findViewById(R.id.noteTitle);
                tvAddedTime = itemView.findViewById(R.id.noteTime);

                ivMore = itemView.findViewById(R.id.btnMore);
                if (ivMore != null) ivMore.setVisibility(View.GONE);
            } else {
                tvSectionTitle = itemView.findViewById(R.id.tv_section_title);

                tvNoteTitle = null;
                tvAddedTime = null;

                ivMore = itemView.findViewById(R.id.iv_more);
                if (ivMore != null) ivMore.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (getItemViewType()) {
                case NOTE:
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onNoteSelected((Note) searchResults.get(position), position);
                    }
                    break;
            }
        }
    }

    public void updateSearchResults(List searchResults) {
        this.searchResults = searchResults;
    }

    public interface OnItemSelectedListener {
        void onNoteSelected(Note note, int position);
    }
}
