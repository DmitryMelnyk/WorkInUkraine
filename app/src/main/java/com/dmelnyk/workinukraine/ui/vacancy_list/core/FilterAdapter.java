package com.dmelnyk.workinukraine.ui.vacancy_list.core;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;

import java.util.List;

/**
 * Created by d264 on 10/4/17.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    public final List<String> items;
    public final CallbackListener mCallback;

    public FilterAdapter(List<String> items, CallbackListener callbackListener) {
        this.items = items;
        this.mCallback = callbackListener;
    }

    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterAdapter.ViewHolder holder, int position) {
        final String title = items.get(position);
        holder.mNameTextView.setText(title);
        holder.mRemoveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onRemoveClicked(title);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameTextView;
        public ImageButton mRemoveImageButton;

        public ViewHolder(View itemView) {
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.tv_title);
            mRemoveImageButton = (ImageButton) itemView.findViewById(R.id.ib_remove);
        }
    }

    public interface CallbackListener {
        void onRemoveClicked(String item);
    }
}
