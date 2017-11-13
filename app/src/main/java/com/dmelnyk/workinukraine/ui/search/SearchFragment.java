package com.dmelnyk.workinukraine.ui.search;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.services.search.SearchVacanciesService;
import com.dmelnyk.workinukraine.ui.dialogs.delete.DialogDelete;
import com.dmelnyk.workinukraine.ui.dialogs.downloading.DialogDownloading;
import com.dmelnyk.workinukraine.ui.dialogs.request.DialogRequest;
import com.dmelnyk.workinukraine.ui.search.Contract.ISearchPresenter;
import com.dmelnyk.workinukraine.ui.search.di.DaggerSearchComponent;
import com.dmelnyk.workinukraine.ui.search.di.SearchModule;
import com.dmelnyk.workinukraine.ui.vacancy_list.VacancyListActivity;
import com.dmelnyk.workinukraine.utils.BaseFragment;
import com.dmelnyk.workinukraine.utils.NetUtils;
import com.dmelnyk.workinukraine.utils.NetworkChangeReceiver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

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
    private static final String KEY_IS_CONNECTED = "KEY_IS_CONNECTED";
    private static final int REQUEST_CODE_VACANCY_ACTIVITY = 1001;

    public static final String ARGS_RUN_SEARCHING = "run_downloading";

    @BindView(R.id.backImageView) ImageView mBackImageView;
    @BindView(R.id.settings_image_view) ImageView mSettingsImageView;
    @BindView((R.id.recyclerView)) RecyclerViewModified mRecyclerView;
    @BindView(R.id.buttonAdd) ImageView mButtonAdd;
    @BindView(R.id.buttonSearch) ImageView mButtonSearch;
    @BindView(R.id.vacancies_count_text_view) TextView mVacanciesCountTextView;
    @BindView(R.id.new_vacancies_text_view) TextView mNewVacanciesTextView;
    @BindView(R.id.refreshed_text_view) TextView mLastUpdateTimeTextView;
    @BindView(R.id.new_text_view) TextView mNewTextView;
    @BindView(R.id.tv_inet_connection) TextView mInternetStatusTextView;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    Unbinder unbinder;

    private View emptyView;

    @Inject
    ISearchPresenter presenter;

    private OnFragmentInteractionListener mListener;
    private static String sItemClickedRequest = "";
    private boolean mIsConnected;

    private DialogDelete mDialogDelete;
    private DialogRequest mDialogRequest;
    private DialogDownloading mDialogDownloading;
    private ArrayList<RequestModel> mRequestsList;
    private SearchAdapter mAdapter;

    int mFoundVacancyCunt = 0;
    private final BroadcastReceiver mDownloadingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("ACTION_CODE = " + intent.getAction());

            String request = intent.getStringExtra(SearchVacanciesService.KEY_REQUEST);
            switch (intent.getAction()) {
                case SearchVacanciesService.ACTION_FINISHED:
                    // updating data after searching vacancies
                    int finalCount = intent.getIntExtra(SearchVacanciesService.KEY_TOTAL_VACANCIES_COUNT, 0);
                    Toast.makeText(context, "Founded vacancies=" + finalCount, Toast.LENGTH_SHORT).show();
                    presenter.downloadingFinished(finalCount);
                    break;
                case SearchVacanciesService.ACTION_DOWNLOADING_IN_PROGRESS:
                    mFoundVacancyCunt += intent.getIntExtra(SearchVacanciesService.KEY_TOTAL_VACANCIES_COUNT, 0);
                    if (mDialogDownloading != null) {
                        mDialogDownloading.updateVacanciesCount(mFoundVacancyCunt);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private final BroadcastReceiver mConnectionChangingReseiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(getClass().getSimpleName(), "receved broadcast for internet connection changed!");

            boolean isConnected = intent.getBooleanExtra(NetworkChangeReceiver.EXTRA_NETWORK_IS_AVAILABLE, false);
            mIsConnected = isConnected;
            presenter.onInternetStatusChanged(isConnected);
        }
    };

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        createToolbarMenu();

        // Restores state
        if (savedInstanceState != null) {
            mDialogStackLevel = savedInstanceState.getInt(KEY_DIALOG_STACK_LEVEL);
            mIsConnected = savedInstanceState.getBoolean(KEY_IS_CONNECTED);
        }

        emptyView = view.findViewById(R.id.emptyView);
        emptyView.findViewById(R.id.btn_add).setOnClickListener(buttonAdd -> {
            showDialogRequest(null);
        });
        instantiateRecyclerView();
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

        IntentFilter connectionStatusFilter = new IntentFilter();
        connectionStatusFilter.addAction(NetworkChangeReceiver.ACTION_NETWORK_STATE_CHANGED);
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(mConnectionChangingReseiver, connectionStatusFilter);

        mIsConnected = NetUtils.isNetworkReachable(getContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_DIALOG_STACK_LEVEL, mDialogStackLevel);
        outState.putBoolean(KEY_IS_CONNECTED, mIsConnected);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbindSearchService();
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(mDownloadingBroadcastReceiver);
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(mConnectionChangingReseiver);
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
                if(!mIsConnected) {
                    Toast.makeText(getContext(), R.string.msg_no_inet_connection_long, Toast.LENGTH_SHORT).show();
                } else {
                    showDialogDownloading();
                }
                break;
        }
    }

    private void instantiateRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setEmptyView(emptyView);
        mAdapter = new SearchAdapter(mRequestsList);
        mAdapter.setAdapterListener(this);
        mRecyclerView.setAdapter(mAdapter);
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
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean getInternetStatus() {
        return NetUtils.isNetworkReachable(getContext().getApplicationContext());
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
        Intent intentSearchService = new Intent(getContext(), SearchVacanciesService.class);
        getContext().bindService(intentSearchService, mSearchConnection, Context.BIND_AUTO_CREATE);
    }

   private void stopSearchVacanciesService() {
       mSearchVacanciesService.cancelDownloading();
    }

    @Override
    public void updateData(ArrayList<RequestModel> data) {
        Timber.d(" downloadingFinished(ArrayList<RequestModel> data)");

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
    }

    @Override
    public void updateDownloadingDialog(int count) {
        // restoring DownloadingDialog if needed
        mDialogDownloading = (DialogDownloading) getFragmentManager().findFragmentByTag(TAG_DIALOG_DOWNLOADING);
        if (mDialogDownloading != null) {
            mDialogDownloading.setCallback(this);
            if (SearchVacanciesService.sIsDownloadingFinished) {
                mDialogDownloading.downloadingFinished(SearchVacanciesService.sVacanciesCount);
            }
        }
    }

    @Override
    public void updateLastSearchTime(String updated) {
        mLastUpdateTimeTextView.setText(getString(R.string.refreshed) + ": " + updated);
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getContext(), getString(R.string.errors_db_request_already_exists), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideNoConnection() {
        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out_long);
        mInternetStatusTextView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
        mInternetStatusTextView.setText(R.string.msg_connection_established);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { /* NOP */ }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (isAdded()) {
                    mInternetStatusTextView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) { /* NOP */ }
        });
        mInternetStatusTextView.startAnimation(fadeOut);
    }

    @Override
    public void showNoConnection() {
        mInternetStatusTextView.setAlpha(1f);
        mInternetStatusTextView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.pink));
        mInternetStatusTextView.setText(R.string.msg_no_inet_connection_long);
        mInternetStatusTextView.setVisibility(View.VISIBLE);
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
    public void onItemClicked(RequestModel itemRequest) {
        Timber.d("SearchAdapter.AdapterCallback.onItemClicked. Item = " + itemRequest);
        sItemClickedRequest = itemRequest.request();
        if (itemRequest.vacanciesCount() == 0) {
            Toast.makeText(getContext(), R.string.no_vacancies_found, Toast.LENGTH_LONG)
                    .show();
        } else {
            Intent vacancyActivityIntent = new Intent(getContext(), VacancyListActivity.class);
            vacancyActivityIntent.setAction(itemRequest.request());
            startActivityForResult(vacancyActivityIntent, REQUEST_CODE_VACANCY_ACTIVITY);
            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }
    }

    @Override
    public void onOkClickedInDownloadingDialog() {
        unbindSearchService();
        resetDialogDownloading();
        // close NavigationActivity's menu in case
        // downloading was started from NavigationActivity
        closeMainMenuCallback();
    }

    @Override
    public void onCancelClickedDownloadingDialog() {
        stopSearchVacanciesService();
        unbindSearchService();
        resetDialogDownloading();
    }

    // CallbackListener add item
    @Override
    public void onTakeRequest(String request, @DialogRequest.MODE int mode) {
        switch (mode) {
            case DialogRequest.MODE_ADD_REQUEST:
                presenter.addRequest(request);
                break;
            case DialogRequest.MODE_EDIT_REQUEST:
                presenter.editRequest(sItemClickedRequest, request);
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

        switch (removeCode) {
            case DialogDelete.REMOVE_ALL_REQUESTS:
                presenter.clearAllRequest();
                break;
            case DialogDelete.REMOVE_ONE_REQUEST:
                presenter.removeRequest(sItemClickedRequest);
                break;
        }
    }

    private boolean mBound;
    private SearchVacanciesService mSearchVacanciesService;
    private ServiceConnection mSearchConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SearchVacanciesService.SearchServiceBinder binder =
                    (SearchVacanciesService.SearchServiceBinder) iBinder;
            mBound = true;
            mSearchVacanciesService = binder.getService();
            Log.e("!!!", "service=" + mSearchVacanciesService);

            Intent searchService = new Intent(
                    getContext().getApplicationContext(), SearchVacanciesService.class);

            searchService.putExtra(SearchVacanciesService.EXTRA_MODE, SearchVacanciesService.MODE_SEARCH);
            binder.startSearching(searchService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    private void unbindSearchService() {
        if (mBound) {
            getContext().unbindService(mSearchConnection);
        }

        mBound = false;
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

    public interface OnFragmentInteractionListener {
        // for updating vacancies number count
        void setVacanciesCount(int vacancies);
    }
}
