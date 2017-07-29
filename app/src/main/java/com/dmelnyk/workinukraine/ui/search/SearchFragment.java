package com.dmelnyk.workinukraine.ui.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.data.RequestModel;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.mvp.dialog_request.DialogRequest;
import com.dmelnyk.workinukraine.services.SearchVacanciesService;
import com.dmelnyk.workinukraine.ui.dialogs.delete.DialogDelete;
import com.dmelnyk.workinukraine.ui.dialogs.downloading.DialogDownloading;
import com.dmelnyk.workinukraine.ui.search.Contract.ISearchPresenter;
import com.dmelnyk.workinukraine.ui.search.di.DaggerSearchComponent;
import com.dmelnyk.workinukraine.ui.search.di.SearchModule;
import com.dmelnyk.workinukraine.ui.vacancy.ScrollingActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SearchFragment extends Fragment implements
        Contract.ISearchView,
        SearchAdapter.AdapterCallback,
        DialogRequest.DialogRequestCallbackListener,
        DialogDelete.DialogDeleteCallbackListener,
        DialogDownloading.DialogDownloadCallbackListener {

    private static final String TAG_DIALOG_DOWNLOADING = "downloading dialog";
    private static final String TAG_DIALOG_REQUEST = "request dialog";

    @BindView(R.id.backImageView) ImageView mBackImageView;
    @BindView(R.id.addImageView) ImageView mAddImageView;
    @BindView((R.id.recyclerView)) RecyclerView mRecyclerView;
    @BindView(R.id.buttonAdd) ImageView mButtonAdd;
    @BindView(R.id.buttonSearch) ImageView mButtonSearch;
    Unbinder unbinder;

    @Inject
    ISearchPresenter presenter;

    private OnFragmentInteractionListener mListener;
    private static String sItemClickedRequest = "";
    private static boolean sIsDialogDownloadingOpen = false;
    private static boolean sIsDialogRequestOpen = false;

    private DialogDownloading mDialogDownloading;
    private DialogRequest mDialogRequest;
    private ArrayList<RequestModel> mRequestsList;

    private final BroadcastReceiver mDownloadingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d(" ACTION_CODE = " + intent.getAction());

            String request = intent.getStringExtra(SearchVacanciesService.KEY_REQUEST);
            switch (intent.getAction()) {
                case SearchVacanciesService.ACTION_FINISHED:
                    mDialogDownloading.downloadingFinished();
                    Toast.makeText(context, "Download finished!", Toast.LENGTH_SHORT).show();
                    break;

                case SearchVacanciesService.ACTION_DOWNLOADING_IN_PROGRESS:
                    int vacanciesCount = intent.getIntExtra(
                            SearchVacanciesService.KEY_VACANCIES_COUNT, -1);

                    Toast.makeText(context, request +vacanciesCount, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerSearchComponent.builder()
                .dbModule(new DbModule(getContext()))
                .searchModule(new SearchModule())
                .build()
                .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);

        presenter.bindView(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreDialogs();
        // registering downloading receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SearchVacanciesService.ACTION_FINISHED);
        filter.addAction(SearchVacanciesService.ACTION_DOWNLOADING_IN_PROGRESS);
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(mDownloadingBroadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(mDownloadingBroadcastReceiver);
    }

    private void restoreDialogs() {
        if (sIsDialogDownloadingOpen) {
            mDialogDownloading = (DialogDownloading) getFragmentManager()
                    .findFragmentByTag(TAG_DIALOG_DOWNLOADING);
            // initialize callbackListener
            mDialogDownloading.setDialogDownloadingCallbackListener(this);
            mButtonAdd.postDelayed(() -> {
                mDialogDownloading.downloadingFinished();
                sIsDialogDownloadingOpen = false;
            }, 1000);
        }

        if (sIsDialogRequestOpen) {
            mDialogRequest = (DialogRequest) getFragmentManager()
                    .findFragmentByTag(TAG_DIALOG_REQUEST);
            // restore callback interface
            mDialogRequest.setCallbackInterface(this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDialogPeriodInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        presenter.unbindView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.backImageView, R.id.addImageView, R.id.buttonAdd, R.id.buttonSearch})
    public void onViewClicked(View view) {
        // TODO update dialog
        switch (view.getId()) {
            case R.id.backImageView:
                mListener.onFragmentInteraction();
                break;
            case R.id.addImageView:
                showDialogRequest();
                break;
            case R.id.buttonAdd:
                showDialogRequest();
                break;
            case R.id.buttonSearch:
                showDialogDownloading();
                startSearchVacanciesService();
                break;
        }
    }

    private void showDialogRequest() {
        DialogRequest dialog =
                DialogRequest.getInstance(new Handler());
        dialog.setCallbackInterface(this);
        dialog.show(getFragmentManager(), TAG_DIALOG_REQUEST);
        sIsDialogRequestOpen = true;
    }

    private void showDialogDownloading() {
        mDialogDownloading = DialogDownloading.newInstance();
        mDialogDownloading.setDialogDownloadingCallbackListener(this);
        mDialogDownloading.show(getFragmentManager(), TAG_DIALOG_DOWNLOADING);
        sIsDialogDownloadingOpen = true;
    }


    private void startSearchVacanciesService() {
        Intent searchService = new Intent(
                getContext().getApplicationContext(), SearchVacanciesService.class);
        searchService.putParcelableArrayListExtra(SearchVacanciesService.KEY_REQUESTS, mRequestsList);
        searchService.putExtra(SearchVacanciesService.KEY_MODE, SearchVacanciesService.MODE_SEARCH);
        getContext().startService(searchService);
    }

    @Override
    public void updateData(ArrayList<RequestModel> data) {
        Timber.d(" updateData(ArrayList<RequestModel> data)");
        mRequestsList = data;

        SearchAdapter adapter = new SearchAdapter(data);
        adapter.setAdapterListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(getContext(), getString(R.string.errors_db_request_already_exists), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void restoreSavedState(String time) {
        // Todo:
    }

    // SearchAdapter.AdapterCallback for open DialogDelete
    @Override
    public void onButtonRemoveClicked(String item) {
        Timber.d("onButtonRemoveClicked on item " + item);
        sItemClickedRequest = item;
        DialogDelete dialogDelete = DialogDelete.getInstance(getString(R.string.delete_request));
        dialogDelete.setCallback(this);
        dialogDelete.show(getFragmentManager(), null);
    }

    // SearchAdapter.AdapterCallback for open Item Fragment
    @Override
    public void onItemClicked(String itemRequestTitle) {
        Timber.d("SearchAdapter.AdapterCallback.onItemClicked. Item = " + itemRequestTitle);
        //TODO: open ItemFragment
        sItemClickedRequest = itemRequestTitle;
        Toast.makeText(getContext(), "item clicked: " + itemRequestTitle, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getContext(), ScrollingActivity.class);
        i.setAction(itemRequestTitle);
        startActivity(i);
    }

    // DialogRequestCallbackListener add item
    @Override
    public void onTakeRequest(String request) {
        presenter.addNewRequest(request);
    }

    @Override
    public void onDismissDialogRequest() {
        sIsDialogRequestOpen = false;
    }

    // DialogRequestCallbackListener remove item
    @Override
    public void onRemoveRequest() {
        Timber.d("onRemoveRequest clicked. Item = " + sItemClickedRequest);
        presenter.removeRequest(sItemClickedRequest);
    }

    // DialogDownloadCallbackListener dismiss dialog
    @Override
    public void onDismissDialogDownloading() {
        sIsDialogDownloadingOpen = false;
    }

    public interface OnFragmentInteractionListener {
        // for open NavigationDrawer
        void onFragmentInteraction();
    }
}