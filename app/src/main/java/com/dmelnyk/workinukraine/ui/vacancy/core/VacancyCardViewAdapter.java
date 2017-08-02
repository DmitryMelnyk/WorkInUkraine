package com.dmelnyk.workinukraine.ui.vacancy.core;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.data.VacancyModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by dmitry on 14.03.17.
 */

public class VacancyCardViewAdapter extends RecyclerView.Adapter<VacancyCardViewAdapter.MyViewHolder> {

    public static final int TYPE_TABVIEW = 1;
    public static final int TYPE_FAVORITE = 2;
    public static final int MENU_SAVE = 3;
    public static final int MENU_REMOVE = 4;
    public static final int MENU_SHARE = 5;

    private static String PARENT_CLASS = "";
    private OnAdapterInteractionListener mListener;
    private Context mContext;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {MENU_SAVE, MENU_REMOVE, MENU_SHARE})
    public @interface VacancyPopupMenuType { }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = { TYPE_FAVORITE, TYPE_TABVIEW })
    public @interface CardViewType {}

    private ArrayList<VacancyModel> mDataSet;
    private int mCardViewType;

    public VacancyCardViewAdapter(ArrayList<VacancyModel> vacancies,
                                  int cardViewType) {
        mDataSet = vacancies;
        mCardViewType = cardViewType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        PARENT_CLASS = parent.getClass().getSimpleName();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vacancy_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final VacancyModel vacancyModel = mDataSet.get(position);
        holder.mTextView.setText(vacancyModel.title());
        holder.mDateTextView.setText(vacancyModel.date());

        // creating PopupMenu:
        final PopupMenu popupMenu = new PopupMenu(mContext, holder.mMenuButton);

        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popupMenu);
            Method setForceIcons = menuPopupHelper.getClass().getDeclaredMethod("setForceShowIcon", Boolean.TYPE);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            Timber.e(e);
        }

        if (mCardViewType == TYPE_FAVORITE) {
            popupMenu.getMenuInflater().inflate(R.menu.card_menu_favorite, popupMenu.getMenu()); // for Favorite activity
        } else {
            popupMenu.getMenuInflater().inflate(R.menu.card_menu_tabview, popupMenu.getMenu()); // for Tabs, New, Recent activities
        }

        popupMenu.setOnMenuItemClickListener(view -> {
            switch (view.getItemId()) {
                case R.id.popup_add_item:
                    popupClicked(position, MENU_SAVE);
                    break;
                case R.id.popup_share_item:
                   popupClicked(position, MENU_SHARE);
                    break;
                case R.id.popup_remove_item:
                    popupClicked(position, MENU_REMOVE);
                    break;
            }
            return true;
        });

        holder.mMenuButton.setOnClickListener(view -> popupMenu.show());

        holder.mCardviewLayout.setOnClickListener( view -> {
            if (view.getId() != R.id.popup_menu) {
                itemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        TextView mDateTextView;
        ImageButton mMenuButton;
        RelativeLayout mCardviewLayout;

        public MyViewHolder(final View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.body_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.card_date);
            mMenuButton = (ImageButton) itemView.findViewById(R.id.popup_menu);
            mCardviewLayout = (RelativeLayout) itemView.findViewById(R.id.item_layout);
        }
    }

    public interface OnAdapterInteractionListener {
        void onAdapterInteractionItemClicked(VacancyModel vacancyClicked);
        void onAdapterInteractionPopupMenuClicked(VacancyModel vacancyClicked, @VacancyPopupMenuType int type);
    }

    public void setOnAdapterInteractionListener(OnAdapterInteractionListener listener) {
        mListener = listener;
    }

    private void popupClicked(int position, @VacancyPopupMenuType int action) {
        if (mListener == null) {
            throw new ClassCastException(PARENT_CLASS
                    + " must implement " + OnAdapterInteractionListener.class);
        } else mListener.onAdapterInteractionPopupMenuClicked(mDataSet.get(position), action);
    }

    private void itemClicked(int position) {
        if (mListener == null) {
            throw new ClassCastException(PARENT_CLASS
                    + " must implement " + OnAdapterInteractionListener.class);
        } else mListener.onAdapterInteractionItemClicked(mDataSet.get(position));
    }

}
