package com.dmelnyk.workinukraine.ui.search;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.services.SearchVacanciesService;
import com.dmelnyk.workinukraine.ui.dialogs.delete.DialogDelete;
import com.dmelnyk.workinukraine.ui.dialogs.downloading.DialogDownloading;
import com.dmelnyk.workinukraine.ui.dialogs.request.DialogRequest;
import com.dmelnyk.workinukraine.ui.search.Contract.ISearchPresenter;
import com.dmelnyk.workinukraine.ui.search.di.DaggerSearchComponent;
import com.dmelnyk.workinukraine.ui.search.di.SearchModule;
import com.dmelnyk.workinukraine.ui.vacancy.VacancyActivity;
import com.dmelnyk.workinukraine.utils.BaseFragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.dmelnyk.workinukraine.ui.search.SearchAdapter.MENU_EDIT;
import static com.dmelnyk.workinukraine.ui.search.SearchAdapter.MENU_REMOVE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SearchFragment extends BaseFragment implements
        Contract.ISearchView,
        SearchAdapter.AdapterCallback,
        DialogDownloading.CallbackLister,
        DialogRequest.CallbackListener,
        DialogDelete.DialogDeleteCallbackListener {

    private static final String TAG_DIALOG_DOWNLOADING = "downloading_dialog";
    private static final String TAG_DIALOG_REQUEST = "request_dialog";
    private static final String TAG_DIALOG_DELETE = "delete_dialog";
    private static final String KEY_DIALOG_STACK_LEVEL = "dialog_stack_level";
    private static final int REQUEST_CODE_VACANCY_ACTIVITY = 1001;

    public static final String ARGS_RUN_SEARCHING = "run_downloading";

    @BindView(R.id.backImageView) ImageView mBackImageView;
    @BindView(R.id.settings_image_view) ImageView mSettingsImageView;
    @BindView((R.id.recyclerView)) RecyclerView mRecyclerView;
    @BindView(R.id.buttonAdd) ImageView mButtonAdd;
    @BindView(R.id.buttonSearch) ImageView mButtonSearch;
    @BindView(R.id.vacancies_count_text_view) TextView mVacanciesCountTextView;
    @BindView(R.id.new_vacancies_text_view) TextView mNewVacanciesTextView;
    @BindView(R.id.refreshed_text_view) TextView mLastUpdateTimeTextView;
    @BindView(R.id.new_text_view) TextView mNewTextView;
    Unbinder unbinder;

    @Inject
    ISearchPresenter presenter;

    private OnFragmentInteractionListener mListener;
    private static String sItemClickedRequest = "";

    private DialogDelete mDialogDelete;
    private DialogDownloading mDialogDownloading;
    private DialogRequest mDialogRequest;
    private ArrayList<RequestModel> mRequestsList;
    private SearchAdapter mAdapter;

    private final BroadcastReceiver mDownloadingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("ACTION_CODE = " + intent.getAction());

            String request = intent.getStringExtra(SearchVacanciesService.KEY_REQUEST);
            switch (intent.getAction()) {
                case SearchVacanciesService.ACTION_FINISHED:
                    // updating data after searching vacancies
                    presenter.updateData();
                    break;

                case SearchVacanciesService.ACTION_DOWNLOADING_IN_PROGRESS:
//                    sTotalVacanciesCount += intent.getIntExtra(
//                            SearchVacanciesService.KEY_TOTAL_VACANCIES_COUNT, -1);
//                    Toast.makeText(context, request + sTotalVacanciesCount, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private boolean mRunDownloading;

    private void resetDialogDownloading() {
        mDialogDownloading = null;
        enableDialogButtons(true);
        mDialogStackLevel = 0;
    }

    private int mDialogStackLevel = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerSearchComponent.builder()
                .dbModule(new DbModule(getContext()))
                .searchModule(new SearchModule())
                .build()
                .inject(this);

        mRequestsList = new ArrayList<>();
        mRunDownloading = getArguments() != null
                ? getArguments().getBoolean(ARGS_RUN_SEARCHING)
                : false;

        Log.e("1010", "SearchFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        unbinder = ButterKnife.bind(this, view);

        mAdapter = new SearchAdapter(mRequestsList);
        mAdapter.setAdapterListener(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(mAdapter);

        createToolbarMenu();

        // Restores state
        if (savedInstanceState != null) {
            mDialogStackLevel = savedInstanceState.getInt(KEY_DIALOG_STACK_LEVEL);
        }
        return view;
    }

    private void createToolbarMenu() {
        final PopupMenu popupMenu = new PopupMenu(getContext(), mSettingsImageView);
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popupMenu);
            Method setForceIcons = menuPopupHelper.getClass().getDeclaredMethod("setForceShowIcon", Boolean.TYPE);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            Timber.e(e);
        }

        popupMenu.getMenuInflater().inflate(R.menu.search_toolbar, popupMenu.getMenu());
        mSettingsImageView.setOnClickListener(view -> popupMenu.show());

        popupMenu.setOnMenuItemClickListener(view -> {
            switch (view.getItemId()) {
                case R.id.menu_clear_requests:
                    mDialogDelete = DialogDelete.getInstance(
                            getString(R.string.search_toolbar_remove_all_requests),
                            DialogDelete.REMOVE_ALL_REQUESTS);
                    mDialogDelete.setCallback(this);
                    mDialogDelete.show(getFragmentManager(), TAG_DIALOG_DELETE);
                    break;
            }
            return true;
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.getClass() + " must implement SearchFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreDialogs();
        presenter.bindView(this);

        // registering downloading receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SearchVacanciesService.ACTION_FINISHED);
        filter.addAction(SearchVacanciesService.ACTION_DOWNLOADING_IN_PROGRESS);
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(mDownloadingBroadcastReceiver, filter);

//        presenter.getFreshRequests();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_DIALOG_STACK_LEVEL, mDialogStackLevel);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(mDownloadingBroadcastReceiver);
    }

    private void restoreDialogs() {
        // restoring DeleteDialog if needed
        mDialogDelete = (DialogDelete) getFragmentManager().findFragmentByTag(TAG_DIALOG_DELETE);
        if (mDialogDelete != null) {
            mDialogDelete.setCallback(this);
        }

        // restoring RequestDialog if needed
        mDialogRequest = (DialogRequest) getFragmentManager().findFragmentByTag(TAG_DIALOG_REQUEST);
        if (mDialogRequest != null) {
            mDialogRequest.setCallback(this);
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

    @OnClick({R.id.backImageView, R.id.buttonAdd, R.id.buttonSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                openMainMenuCallback();
                break;
            case R.id.buttonAdd:
                showDialogRequest(null);
                break;
            case R.id.buttonSearch:
                showDialogDownloading();
                break;
        }
    }

    private void showDialogRequest(@Nullable String request) {
        // Prevents creating more then one dialog at a time
        if (mDialogStackLevel == 0) {
            mDialogStackLevel = 1;

            enableDialogButtons(false);

            mDialogRequest = new DialogRequest();
            if (request != null) {
                Bundle args = new Bundle();
                args.putString(DialogRequest.ARG_EDIT_REQUEST, request);
                mDialogRequest.setArguments(args);
            }

            mDialogRequest.show(getFragmentManager(), TAG_DIALOG_REQUEST);
            mDialogRequest.setCallback(this);
        }
    }

    /**
     * Runs service for search vacancies. Also can be called from NavigationActivity
     */
    public void showDialogDownloading() {
        // Prevents creating more then one dialog at a time
        if (mDialogStackLevel == 0) {
            mDialogStackLevel = 1;
            enableDialogButtons(false);

            // starts searching service
            startSearchVacanciesService();

            mDialogDownloading = DialogDownloading.newInstance(true, 0);
            mDialogDownloading.setCallback(this);
            mDialogDownloading.show(getFragmentManager(), TAG_DIALOG_DOWNLOADING);
        }
    }

    private void enableDialogButtons(boolean disable) {
        mButtonAdd.setEnabled(disable);
        mButtonSearch.setEnabled(disable);
    }

    private void startSearchVacanciesService() {
        Intent searchService = new Intent(
                getContext().getApplicationContext(), SearchVacanciesService.class);

        searchService.putExtra(SearchVacanciesService.EXTRA_MODE, SearchVacanciesService.MODE_SEARCH);
        getContext().startService(searchService);
    }

    @Override
    public void updateData(ArrayList<RequestModel> data) {
        Timber.d(" updateData(ArrayList<RequestModel> data)");
        mRequestsList.clear();
        mRequestsList.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateNewVacanciesCount(int newVacanciesCount) {
        mNewVacanciesTextView.setVisibility(
                newVacanciesCount == 0 ? View.GONE : View.VISIBLE);
        mNewTextView.setVisibility(
                newVacanciesCount == 0 ? View.GONE : View.VISIBLE);
        mNewVacanciesTextView.setText("" + newVacanciesCount);
    }

    @Override
    public void updateVacanciesCount(int allVacanciesCount) {
        mVacanciesCountTextView.setText("" + allVacanciesCount);

        // update vacancies count in main menu
        mListener.setVacanciesCount(allVacanciesCount);

        // restoring DownloadingDialog if needed
        mDialogDownloading = (DialogDownloading) getFragmentManager().findFragmentByTag(TAG_DIALOG_DOWNLOADING);
        if (mDialogDownloading != null) {
            mDialogDownloading.setCallback(this);
            if (SearchVacanciesService.sIsDownloadingFinished) {
                mDialogDownloading.downloadingFinished(allVacanciesCount);
            }
        }
    }

    @Override
    public void updateLastSearchTime(String updated) {
        mLastUpdateTimeTextView.setText(getString(R.string.refreshed) + ": " + updated);
    }

    @Override
    public void showErrorMessage(String message) {
//        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        Toast.makeText(getContext(), getString(R.string.errors_db_request_already_exists), Toast.LENGTH_SHORT).show();
    }

    // SearchAdapter.AdapterCallback for open DialogDelete
    @Override
    public void onMenuItemClicked(String request, @SearchAdapter.MenuType int menu) {
        Timber.d("onMenuItemClicked on item " + request);
        sItemClickedRequest = request;

        switch (menu) {
            case MENU_EDIT:
                showDialogRequest(request);
                break;
            case MENU_REMOVE:
                createDeleteDialog();
                break;
        }
    }

    private void createDeleteDialog() {
        mDialogDelete = DialogDelete.getInstance(
                getString(R.string.delete_request),
                DialogDelete.REMOVE_ONE_REQUEST);
        mDialogDelete.setCallback(this);
        mDialogDelete.show(getFragmentManager(), TAG_DIALOG_DELETE);
    }

    // SearchAdapter.AdapterCallback for open Item Fragment
    @Override
    public void onItemClicked(String itemRequestTitle) {
        Timber.d("SearchAdapter.AdapterCallback.onItemClicked. Item = " + itemRequestTitle);
        sItemClickedRequest = itemRequestTitle;
        Intent vacancyActivityIntent = new Intent(getContext(), VacancyActivity.class);
        vacancyActivityIntent.setAction(itemRequestTitle);
        startActivityForResult(vacancyActivityIntent, REQUEST_CODE_VACANCY_ACTIVITY);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void onOkClickedInDownloadingDialog() {
        resetDialogDownloading();
        // close NavigationActivity's menu in case
        // downloading was started from NavigationActivity
        closeMainMenuCallback();
    }

    // CallbackListener add item
    @Override
    public void onTakeRequest(String request, @DialogRequest.MODE int mode) {
        Log.e("1010", "DialogRequest. Mode=" + mode);
        switch (mode) {
            case DialogRequest.MODE_ADD_REQUEST:
                presenter.addRequest(request);
                break;
            case DialogRequest.MODE_EDIT_REQUEST:
                presenter.removeRequest(sItemClickedRequest);
                presenter.addRequest(request);
                break;
        }

        resetDialogRequest();
    }

    @Override
    public void dialogDismissed() {
        resetDialogRequest();
    }

    private void resetDialogRequest() {
        mDialogRequest = null;
        enableDialogButtons(true);
        mDialogStackLevel = 0;
    }

    // CallbackListener remove item
    @Override
    public void onRemoveClicked(@DialogDelete.RemoveCode String removeCode) {
        Timber.d("onRemoveClicked clicked. RequestCode=" + removeCode);

        switch (removeCode) {
            case DialogDelete.REMOVE_ALL_REQUESTS:
                presenter.clearAllRequest();
                break;
            case DialogDelete.REMOVE_ONE_REQUEST:
                presenter.removeRequest(sItemClickedRequest);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("999", "onActivityResult in SearchFragment");
        if (requestCode == REQUEST_CODE_VACANCY_ACTIVITY) {
            switch (resultCode) {
                case RESULT_OK:
                    // update request table (now all 'new' vacancies become 'recent')
                    presenter.getFreshRequests();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(getContext(), R.string.no_vacancies_found, Toast.LENGTH_LONG)
                            .show();
                    break;
            }
        }
    }

    public interface OnFragmentInteractionListener {
        // for updating vacancies number count
        void setVacanciesCount(int vacancies);
    }
}
