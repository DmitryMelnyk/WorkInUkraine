package com.dmelnyk.workinukraine.ui.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.data.RequestModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by d264 on 6/16/17.
 */

class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private ArrayList<RequestModel> mRequestModels;
    private AdapterCallback mCallback;

    // Callback interface
    public interface AdapterCallback {
        void onItemClicked(String item);
        void onButtonRemoveClicked(String item);
    }

    public void setAdapterListener(AdapterCallback mCallback) {
        this.mCallback = mCallback;
    }

    public SearchAdapter(ArrayList<RequestModel> items) {
        this.mRequestModels = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);

        return new MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        RequestModel requestModel = mRequestModels.get(position);
        // TODO

        if (requestModel.updated() == -1l) {
            holder.mTextViewUpdated.setText("-");
        } else {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String updated = timeFormat.format(new Date(requestModel.updated()));
            holder.mTextViewUpdated.setText(updated);
        }

        holder.mTextViewTitle.setText(requestModel.request());
        holder.mTextViewCount.setText("" + requestModel.vacanciesCount());


    }

    @Override
    public int getItemCount() {
        return mRequestModels.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_title)
        TextView mTextViewTitle;
        @BindView(R.id.text_view_count)
        TextView mTextViewCount;
        @BindView(R.id.text_view_updated)
        TextView mTextViewUpdated;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.button_remove, R.id.item_layout})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.button_remove:
                    mCallback.onButtonRemoveClicked(mTextViewTitle.getText().toString());
                    break;
                case R.id.item_layout:
                    mCallback.onItemClicked(mTextViewTitle.getText().toString());
                    break;
            }
        }
    }
}
