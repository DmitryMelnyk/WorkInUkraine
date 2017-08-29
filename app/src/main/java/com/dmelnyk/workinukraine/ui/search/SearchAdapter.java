package com.dmelnyk.workinukraine.ui.search;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.data.RequestModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by d264 on 6/16/17.
 */

class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    public static final String SPLITTER = " / ";
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
        String request = requestModel.request().split(SPLITTER)[0];
        String city = requestModel.request().split(" / ")[1];
        int newVacanciesCount = requestModel.newVacanciesCount();

        holder.mCityTextView.setText(city);
        holder.mLetterTextView.setText(String.valueOf(request.charAt(0)).toUpperCase());
        holder.mRequestTextView.setText(request);
        holder.mNewVacanciesCountTextView.setText(String.valueOf(newVacanciesCount));

        if (newVacanciesCount > 0) {
            holder.mNewVacanciesCountTextView.setVisibility(View.VISIBLE);
            holder.mNewVacanciesCountTextView.setText(String.valueOf(newVacanciesCount));
            holder.mCityTextView.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.white_soft));
            holder.mRequestTextView.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.white_soft));
            holder.mLetterTextView.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.white_soft));
            holder.mItemLayout.setBackgroundColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        } else {
            holder.mNewVacanciesCountTextView.setVisibility(View.GONE);
            holder.mCityTextView.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.mRequestTextView.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.mLetterTextView.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.mItemLayout.setBackgroundColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.white_soft));
        }

//        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//        String updated = timeFormat.format(new Date(requestModel.updated()));
    }

    @Override
    public int getItemCount() {
        return mRequestModels.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_layout) RelativeLayout mItemLayout;
        @BindView(R.id.new_vacancies_text_view) TextView mNewVacanciesCountTextView;
        @BindView(R.id.letter_text_view) TextView mLetterTextView;
        @BindView(R.id.request_text_view) TextView mRequestTextView;
        @BindView(R.id.request_city_text_view) TextView mCityTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.item_layout})
        public void onViewClicked(View view) {
            switch (view.getId()) {
//                case R.id.button_remove:
//                    mCallback.onButtonRemoveClicked(mRequestTextView.getText().toString());
//                    break;
                case R.id.item_layout:
                    String fullRequest = mRequestTextView.getText().toString()
                            + SPLITTER + mCityTextView.getText().toString();
                    mCallback.onItemClicked(fullRequest);
                    break;
            }
        }
    }
}
