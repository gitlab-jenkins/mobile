package xyz.homapay.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.BusinessListRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessSearchRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.BusinessDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.adapter.HamPayBusinessesAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestHamPayBusiness;
import xyz.homapay.hampay.mobile.android.async.RequestSearchHamPayBusiness;
import xyz.homapay.hampay.mobile.android.component.doblist.DobList;
import xyz.homapay.hampay.mobile.android.component.doblist.events.OnLoadMoreListener;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoEmptyViewException;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoListviewException;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class PayToBusinessFragment extends Fragment {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private ListView businessListView;

    HamPayDialog hamPayDialog;

    private boolean onLoadMore = false;
    DobList dobList;


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

    Tracker hamPayGaTracker;

    public PayToBusinessFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE).edit();

        hamPayBusinessesAdapter = new HamPayBusinessesAdapter(getActivity());

        hamPayGaTracker = ((HamPayApplication) getActivity().getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);
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

                    hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

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

        businessDTOs = new ArrayList<BusinessDTO>();

        hamPayDialog = new HamPayDialog(getActivity());
        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));


        businessListView = (ListView) rootView.findViewById(R.id.businessListView);


        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().finish();
            startActivity(intent);
        }else {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            businessListRequest = new BusinessListRequest();
            businessListRequest.setPageNumber(requestPageNumber);
            businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);

            requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
            requestHamPayBusiness.execute(businessListRequest);
        }


        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (requestHamPayBusiness != null){
            if (!requestHamPayBusiness.isCancelled())
                requestHamPayBusiness.cancel(true);
        }

        if (requestSearchHamPayBusiness != null){
            if (!requestSearchHamPayBusiness.isCancelled())
                requestSearchHamPayBusiness.cancel(true);
        }

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

        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().finish();
            startActivity(intent);
        }else {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            businessSearchRequest = new BusinessSearchRequest();
            businessSearchRequest.setPageNumber(requestPageNumber);
            businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
            businessSearchRequest.setTerm(searchTerm);
            requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
            requestSearchHamPayBusiness.execute(businessSearchRequest);
        }


        inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);

        hamPayBusinessesAdapter.clear();
        businessDTOs.clear();

        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

    }


    public class RequestBusinessListTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BusinessListResponse>> {

        List<BusinessDTO> newBusinessDTOs;
        boolean searchEnabled;

        public RequestBusinessListTaskCompleteListener(boolean searchEnabled){
            this.searchEnabled = searchEnabled;
        }

        @Override
        public void onTaskComplete(ResponseMessage<BusinessListResponse> businessListResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

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

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Business List")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());

                }else {
                    if (!searchEnabled) {
                        businessListRequest.setPageNumber(requestPageNumber);
                        businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                        requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(false));
                        new HamPayDialog(getActivity()).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest,
                                businessListResponseMessage.getService().getResultStatus().getCode(),
                                businessListResponseMessage.getService().getResultStatus().getDescription());
                    }else {
                        businessSearchRequest.setPageNumber(requestPageNumber);
                        businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                        requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(true));
                        new HamPayDialog(getActivity()).showFailBusinessSearchListDialog(requestSearchHamPayBusiness, businessSearchRequest,
                                businessListResponseMessage.getService().getResultStatus().getCode(),
                                businessListResponseMessage.getService().getResultStatus().getDescription());
//                        requestSearchHamPayBusiness.execute(businessSearchRequest);
                    }

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Business List")
                            .setAction("Fetch")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                if (!searchEnabled) {
                    businessListRequest.setPageNumber(requestPageNumber);
                    businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(false));
                    new HamPayDialog(getActivity()).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest,
                            Constants.LOCAL_ERROR_CODE,
                            getString(R.string.msg_fail_business_list));
                }else {
                    businessSearchRequest.setPageNumber(requestPageNumber);
                    businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(true));
                    new HamPayDialog(getActivity()).showFailBusinessSearchListDialog(requestSearchHamPayBusiness, businessSearchRequest,
                            Constants.LOCAL_ERROR_CODE,
                            getString(R.string.msg_fail_business_search_list));
//                    requestSearchHamPayBusiness.execute(businessSearchRequest);
                }

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Business List")
                        .setAction("Fetch")
                        .setLabel("Fail(Mobile)")
                        .build());
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
