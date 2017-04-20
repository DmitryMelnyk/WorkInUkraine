package com.dmelnyk.workinukraine.helpers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.mvp.activity_webview.WebViewActivity;
import com.dmelnyk.workinukraine.db.JobDbSchema;
import com.dmelnyk.workinukraine.db.JobPool;
import com.dmelnyk.workinukraine.di.MyApplication;
import com.dmelnyk.workinukraine.di.component.DaggerDbComponent;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by dmitry on 14.03.17.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.MyViewHolder> {

    public enum type {
        TABVIEW, FAVORITE
    }

    @Inject
    JobPool jobPool;

    private ArrayList<Job> dataSet;
    private Context context;
    private type popupType;

    public CardViewAdapter(ArrayList<Job> jobs, Context context, type popupType ) {
        dataSet = jobs;
        this.context = context;
        this.popupType = popupType;

        injectDependency(context);
    }

    private void injectDependency(Context context) {
        DaggerDbComponent.builder()
                .applicationComponent(MyApplication.get(context).getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Job job = dataSet.get(position);
        TextView text = holder.textView;
        TextView date = holder.dateView;
        ImageButton menuButton = holder.menuButton;

        // creating PopupMenu:
        final PopupMenu popupMenu = new PopupMenu(context, menuButton);

        if (popupType.equals(type.FAVORITE)) {
            popupMenu.inflate(R.menu.card_menu_favorite); // for Favorite activity
        } else {
            popupMenu.inflate(R.menu.card_menu_tabview); // for Tabs, New, Recent activities
        }
        final MenuPopupHelper popupHelper = new MenuPopupHelper(
                context, (MenuBuilder) popupMenu.getMenu(), menuButton);
        popupHelper.setForceShowIcon(true);
        menuButton.setOnClickListener(view -> popupHelper.show());

        popupMenu.setOnMenuItemClickListener(view -> {
            switch (view.getItemId()) {
                case R.id.popup_add_item:
                    // insert job to db
                    saveDataToDb(job);
                    break;
                case R.id.popup_share_item:
                    // TODO: create share Intent with data (title + url)
                    sendIntent(job);
                    break;
                case R.id.popup_remove_item:
                    removeItemFromDb(job);
            }
            return true;
        });


        text.setText(job.getTitle());
        date.setText(job.getDate());

        text.setOnClickListener(
                view -> {
                    Intent webActivity = WebViewActivity
                            .newInstance(context, job);
                    context.startActivity(webActivity);
                });
    }

    private void sendIntent(Job job) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, job.getTitle() + ": " + job.getUrlCode());
        intent.setType("text/plain");
        context.startActivity(intent);
    }

    private void removeItemFromDb(Job job) {
        dataSet.remove(job);
        notifyDataSetChanged();
        jobPool.removeJobFromFavorite(job);
    }

    private void saveDataToDb(Job job) {
        boolean saved = jobPool.addJob(JobDbSchema.JobTable.FAVORITE, job);
        int message = saved
                ? R.string.card_view_adapter_job_added_to_favorite
                : R.string.card_view_adapter_job_exists_in_table;
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView dateView;
        ImageButton menuButton;

        public MyViewHolder(final View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.card_text_view);
            dateView = (TextView) itemView.findViewById(R.id.card_date);
            menuButton = (ImageButton) itemView.findViewById(R.id.popup);
        }
    }
}
