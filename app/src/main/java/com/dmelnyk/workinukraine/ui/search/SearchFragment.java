package com.dmelnyk.workinukraine.ui.search;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.business.search.model.SearchRequestModel;
import com.dmelnyk.workinukraine.mvp.dialog_request.DialogRequest;
import com.dmelnyk.workinukraine.ui.dialogs.downloading.DialogDownloading;
import com.dmelnyk.workinukraine.ui.search.Contract.ISearchPresenter;
import com.dmelnyk.workinukraine.ui.search.di.SearchModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SearchFragment extends Fragment implements
        Contract.ISearchView, SearchAdapter.AdapterCallback {

    @BindView(R.id.backImageView)
    ImageView backImageView;
    @BindView(R.id.addImageView)
    ImageView addImageView;
    @BindView((R.id.recyclerView))
    RecyclerView recyclerView;
    @BindView(R.id.buttonAdd)
    ImageView buttonAdd;
    @BindView(R.id.buttonSearch)
    ImageView buttonSearch;
    Unbinder unbinder;

    @Inject
    ISearchPresenter presenter;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WorkInUaApplication.get(getContext()).getAppComponent()
                .add(new SearchModule()).inject(this);
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
        DialogRequest dialog =
                DialogRequest.getInstance(new Handler());

        DialogDownloading dialogDownloading =
                DialogDownloading.getInstance(new Handler());
        switch (view.getId()) {
            case R.id.backImageView:
                mListener.onFragmentInteraction();
                break;
            case R.id.addImageView:
                dialog.show(getFragmentManager(), "key");
                break;
            case R.id.buttonAdd:
                dialog.show(getFragmentManager(), "key");
                break;
            case R.id.buttonSearch:
                dialogDownloading.show(getFragmentManager(), "downloading");
                Toast.makeText(getContext(), "search", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void updateData(SearchRequestModel[] data) {
        SearchAdapter adapter = new SearchAdapter(data);
        adapter.setAdapterListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void restoreSavedState(String time) {
        // Todo:
    }

    @Override
    public void onViewClicked(int item) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
