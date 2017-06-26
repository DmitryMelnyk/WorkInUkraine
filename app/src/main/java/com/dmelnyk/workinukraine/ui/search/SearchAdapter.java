package com.dmelnyk.workinukraine.ui.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.business.search.model.SearchRequestModel;
import com.dmelnyk.workinukraine.utils.MyBounceInterpolator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by d264 on 6/16/17.
 */

class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    private SearchRequestModel[] mRequestModels;
    private AdapterCallback mCallback;

    // Callback interface
    public interface AdapterCallback {
        void onViewClicked(int item);
    }

    public void setAdapterListener(AdapterCallback mCallback) {
        this.mCallback = mCallback;
    }

    public SearchAdapter(SearchRequestModel[] items) {
        this.mRequestModels = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item_cardview, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        SearchRequestModel requestModel = mRequestModels[position];
        // TODO

        holder.requestTextView.setText(requestModel.getRequest());
        holder.cityTextView.setText(requestModel.getCity());
        holder.jobCountTextView.setText("" + requestModel.getJobCount());
        holder.dataTextView.setText(requestModel.getLastUpdate());
    }

    @Override
    public int getItemCount() {
        return mRequestModels.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.editButton)
        ImageView editButton;
        @BindView(R.id.playButton)
        ImageView playButton;
        @BindView(R.id.removeButton)
        ImageView removeButton;
        @BindView(R.id.blacklistButton)
        ImageView blacklistButton;
        @BindView(R.id.avatarImage)
        ImageView avatarImage;
        @BindView(R.id.requestTextView)
        TextView requestTextView;
        @BindView(R.id.cityTextView)
        TextView cityTextView;
        @BindView(R.id.jobCountTextView)
        TextView jobCountTextView;
        @BindView(R.id.dataTextView)
        TextView dataTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        @OnClick({R.id.editButton, R.id.playButton, R.id.removeButton, R.id.blacklistButton, R.id.avatarImage})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.editButton:
                    break;
                case R.id.playButton:
                    Toast.makeText(itemView.getContext(), "Play", Toast.LENGTH_SHORT).show();
                    // initialize animations;
                    Animation scaleAnimation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.scale_anim);
                    MyBounceInterpolator interpolator = new MyBounceInterpolator(0.5, 20);
                    scaleAnimation.setInterpolator(interpolator);
                    playButton.setAnimation(scaleAnimation);
                    playButton.startAnimation(scaleAnimation);
                    break;
                case R.id.removeButton:
                    break;
                case R.id.blacklistButton:
                    break;
                case R.id.avatarImage:
                    break;
            }
        }
    }
}
