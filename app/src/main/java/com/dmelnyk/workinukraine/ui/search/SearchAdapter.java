package com.dmelnyk.workinukraine.ui.search;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.models.RequestModel;

import java.lang.annotation.Retention;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by d264 on 6/16/17.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    public static final String SPLITTER = " / ";
    private ArrayList<RequestModel> mRequestModels;
    private AdapterCallback mCallback;

    public static final int MENU_EDIT = 1;
    public static final int MENU_REMOVE = 2;
    @IntDef({MENU_EDIT, MENU_REMOVE}) public @interface MenuType {}

    // Callback interface
    public interface AdapterCallback {
        void onItemClicked(RequestModel item);
        void onMenuItemClicked(String item, @MenuType int menu);
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
            holder.mLetterTextView.setShadowLayer(3, 2, 2, R.color.text_shadow_white);
            holder.mItemLayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                    R.drawable.request_bg_dark));
        } else {
            holder.mNewVacanciesCountTextView.setVisibility(View.GONE);
            holder.mCityTextView.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.mRequestTextView.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.mLetterTextView.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.mLetterTextView.setShadowLayer(3, -2, -2, R.color.blue_dark);
            holder.mItemLayout.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                    R.drawable.request_bg_light));
        }

        holder.mItemLayout.setOnClickListener(view -> {
            mCallback.onItemClicked(requestModel);
        });
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

            PopupMenu popupMenu;
            popupMenu = new PopupMenu(itemView.getContext(), mLetterTextView, Gravity.CENTER_VERTICAL);

            try {
                Field field = popupMenu.getClass().getDeclaredField("mPopup");
                field.setAccessible(true);
                Object menuPopupHelper = field.get(popupMenu);
                Method setForceIcons = menuPopupHelper.getClass().getDeclaredMethod("setForceShowIcon", Boolean.TYPE);
                setForceIcons.invoke(menuPopupHelper, true);
            } catch (Exception e) {
                Timber.e(e);
            }

            popupMenu.getMenuInflater().inflate(R.menu.search_request, popupMenu.getMenu());
            // OnLongClick listener for removing request
            mItemLayout.setOnLongClickListener(view -> {
                popupMenu.show();
                return false;
            });

            popupMenu.setOnMenuItemClickListener(view -> {
                switch (view.getItemId()) {
                    case R.id.popup_edit_item:
                        mCallback.onMenuItemClicked(getFullRequest(), MENU_EDIT);
                        break;
                    case R.id.popup_remove_item:
                        mCallback.onMenuItemClicked(getFullRequest(), MENU_REMOVE);
                        break;
                }
                return false;
            });
        }

        @NonNull
        private String getFullRequest() {
            return mRequestTextView.getText().toString()
                                    + SPLITTER + mCityTextView.getText().toString();
        }
    }


}
