package com.dmelnyk.workinukraine.ui.search;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by d264 on 10/9/17.
 */

public class RecyclerViewModified extends RecyclerView {

    private View emptyView;
    private final AdapterDataObserver adapterDataObserver =
            new AdapterDataObserver() {
                @Override
                public void onChanged() {
                    checkAdapterIsEmpty();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    checkAdapterIsEmpty();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    checkAdapterIsEmpty();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    checkAdapterIsEmpty();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                    checkAdapterIsEmpty();
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    checkAdapterIsEmpty();
                }
            };

    public RecyclerViewModified(Context context) {
        super(context);
    }

    public RecyclerViewModified(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewModified(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void checkAdapterIsEmpty() {
        if (emptyView != null && getAdapter() != null) {
            final boolean emptyViewIsVisible = getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewIsVisible ? View.VISIBLE : View.GONE);
//            setVisibility(emptyViewIsVisible ? View.INVISIBLE : View.VISIBLE);
            if (emptyViewIsVisible) {
                setAlpha(0);
            } else {
                setAlpha(1);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter previousAdapter = getAdapter();
        if (previousAdapter != null) {
            previousAdapter.unregisterAdapterDataObserver(adapterDataObserver);
        }

        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(adapterDataObserver);
        }

        checkAdapterIsEmpty();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkAdapterIsEmpty();
    }
}
