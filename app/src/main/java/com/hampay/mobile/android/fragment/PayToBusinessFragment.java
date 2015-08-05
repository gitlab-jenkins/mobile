package com.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.BusinessListRequest;
import com.hampay.common.core.model.request.BusinessSearchRequest;
import com.hampay.common.core.model.response.BusinessListResponse;
import com.hampay.common.core.model.response.dto.BusinessDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.adapter.HamPayBusinessesAdapter;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestHamPayBusiness;
import com.hampay.mobile.android.async.RequestSearchHamPayBusiness;
import com.hampay.mobile.android.component.doblist.DobList;
import com.hampay.mobile.android.component.doblist.events.OnLoadMoreListener;
import com.hampay.mobile.android.component.doblist.exceptions.NoEmptyViewException;
import com.hampay.mobile.android.component.doblist.exceptions.NoListviewException;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class PayToBusinessFragment extends Fragment {


    private ListView businessListView;

    RelativeLayout loading_rl;

    private boolean onLoadMore = false;
    DobList dobList;

    private ResponseMessage<BusinessListResponse> businessListResponse;

    private List<BusinessDTO> businessDTOs;

    private HamPayBusinessesAdapter hamPayBusinessesAdapter;

    private boolean FINISHED_SCROLLING = false;

    View rootView;

    ImageView searchImage;
    FacedEditText searchPhraseText;

    InputMethodManager inputMethodManager;


    BusinessListRequest businessListRequest;
    BusinessSearchRequest businessSearchRequest;

    RequestSearchHamPayBusiness requestSearchHamPayBusiness;
    RequestHamPayBusiness requestHamPayBusiness;

    int requestPageNumber = 0;

    boolean searchEnabled = false;

    public PayToBusinessFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hamPayBusinessesAdapter = new HamPayBusinessesAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_pay_to_business, container, false);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);

        searchImage = (ImageView) rootView.findViewById(R.id.searchImage);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchPhraseText.getText().toString().length() > 0) {
                    performBusinessSearch(searchPhraseText.getText().toString());
                }
            }
        });

        searchPhraseText = (FacedEditText) rootView.findViewById(R.id.searchPhraseText);

        searchPhraseText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count == 0) {
                    requestPageNumber = 0;
                    searchEnabled = false;
                    FINISHED_SCROLLING = false;
                    onLoadMore = false;
                    businessListRequest.setPageNumber(requestPageNumber);
                    businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
                    requestHamPayBusiness.execute(businessListRequest);

                    inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);

                    hamPayBusinessesAdapter.clear();
                    businessDTOs.clear();
                    loading_rl.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchPhraseText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performBusinessSearch(searchPhraseText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        businessListResponse = new ResponseMessage<BusinessListResponse>();
        businessDTOs = new ArrayList<BusinessDTO>();

        loading_rl = (RelativeLayout) rootView.findViewById(R.id.loading_rl);

        businessListView = (ListView) rootView.findViewById(R.id.businessListView);

        businessListRequest = new BusinessListRequest();
        businessListRequest.setPageNumber(requestPageNumber);
        businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);

        requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
        requestHamPayBusiness.execute(businessListRequest);


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void performBusinessSearch(String searchTerm) {
        requestPageNumber = 0;
        searchEnabled = true;
        FINISHED_SCROLLING = false;
        onLoadMore = false;


        businessSearchRequest = new BusinessSearchRequest();
        businessSearchRequest.setPageNumber(requestPageNumber);
        businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        businessSearchRequest.setTerm(searchTerm);

        requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
        requestSearchHamPayBusiness.execute(businessSearchRequest);


        inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);

        hamPayBusinessesAdapter.clear();
        businessDTOs.clear();
        loading_rl.setVisibility(View.VISIBLE);

    }


    public class RequestBusinessListTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BusinessListResponse>> {

        List<BusinessDTO> newBusinessDTOs;
        boolean searchEnabled;

        public RequestBusinessListTaskCompleteListener(boolean searchEnabled){
            this.searchEnabled = searchEnabled;
        }

        @Override
        public void onTaskComplete(ResponseMessage<BusinessListResponse> businessListResponseMessage) {

            loading_rl.setVisibility(View.GONE);

            if (businessListResponseMessage != null) {
                if (businessListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    newBusinessDTOs = businessListResponseMessage.getService().getBusinesses();
                    businessDTOs.addAll(newBusinessDTOs);
                    if (businessDTOs != null) {
                        if (newBusinessDTOs.size() == 0 || newBusinessDTOs.size() < Constants.DEFAULT_PAGE_SIZE) {
                            FINISHED_SCROLLING = true;
                        }
                        if (businessDTOs.size() > 0) {
                            requestPageNumber++;
                            if (onLoadMore) {
                                if (newBusinessDTOs != null)
                                    addDummyData(newBusinessDTOs.size());
                            } else {
                                initDobList(rootView, businessListView);
                                businessListView.setAdapter(hamPayBusinessesAdapter);
                            }
                        }
                    }
                }else {
                    if (!searchEnabled) {
                        businessListRequest.setPageNumber(requestPageNumber);
                        businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                        requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(false));
                        new HamPayDialog(getActivity()).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest);
                    }else {
                        businessSearchRequest.setPageNumber(requestPageNumber);
                        businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                        requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(true));
                        requestSearchHamPayBusiness.execute(businessSearchRequest);
                    }
                }
            }else {
                if (!searchEnabled) {
                    businessListRequest.setPageNumber(requestPageNumber);
                    businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(false));
                    new HamPayDialog(getActivity()).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest);
                }else {
                    businessSearchRequest.setPageNumber(requestPageNumber);
                    businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(true));
                    requestSearchHamPayBusiness.execute(businessSearchRequest);
                }
            }
        }

        @Override
        public void onTaskPreRun() {
        }

        private void initDobList(View rootView, ListView listView) {

            dobList = new DobList();
            try {

                dobList.register(listView);

                dobList.addDefaultLoadingFooterView();

                View noItems = rootView.findViewById(R.id.noItems);
                dobList.setEmptyView(noItems);

                dobList.setOnLoadMoreListener(new OnLoadMoreListener() {

                    @Override
                    public void onLoadMore(final int totalItemCount) {

                        onLoadMore = true;

                        if (!FINISHED_SCROLLING) {
                            if (!searchEnabled) {
                                businessListRequest.setPageNumber(requestPageNumber);
                                businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                                requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(false));
                                requestHamPayBusiness.execute(businessListRequest);
                            } else {
                                businessSearchRequest.setPageNumber(requestPageNumber);
                                businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                                requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
                                requestSearchHamPayBusiness.execute(businessSearchRequest);
                            }
                        } else
                            dobList.finishLoading();

                    }
                });

            } catch (NoListviewException e) {
                e.printStackTrace();
            }

            try {

                dobList.startCentralLoading();

            } catch (NoEmptyViewException e) {
                e.printStackTrace();
            }

            addDummyData(businessDTOs.size());
        }

        protected void addDummyData(int itemsCount) {
            addItems(hamPayBusinessesAdapter.getCount(), hamPayBusinessesAdapter.getCount() + itemsCount);
            dobList.finishLoading();
        }

        protected void addItems(int from, int to) {
            for (int i = from; i < to; i++) {
                hamPayBusinessesAdapter.addItem(businessDTOs.get(i));
            }
        }
    }
}
