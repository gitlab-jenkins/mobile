package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.enums.BizSortFactor;
import xyz.homapay.hampay.common.core.model.request.BusinessListRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessSearchRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.BusinessDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
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
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class BusinessesListActivity extends AppCompatActivity implements View.OnClickListener {



    private Context context;
    private Activity activity;
    private SwipeRefreshLayout pullToRefresh;
    private ListView businessListView;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    HamPayDialog hamPayDialog;

    private boolean onLoadMore = false;
    DobList dobList;


    private List<BusinessDTO> businessDTOs;

    private HamPayBusinessesAdapter hamPayBusinessesAdapter;

    private boolean FINISHED_SCROLLING = false;


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

    private RelativeLayout full_business;
    private RelativeLayout popular_business;
    private RelativeLayout recent_business;
    private ImageView full_triangle;
    private ImageView popular_triangle;
    private ImageView recent_triangle;
    private BizSortFactor bizSortFactor = BizSortFactor.NAME;

    public void backActionBar(View view){
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
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
    protected void onResume() {
        super.onResume();
        HamPayApplication.setAppSate(AppState.Resumed);
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_businesses_list);

        context = this;
        activity = BusinessesListActivity.this;

        full_business = (RelativeLayout)findViewById(R.id.full_business);
        full_business.setOnClickListener(this);
        popular_business = (RelativeLayout)findViewById(R.id.popular_business);
        popular_business.setOnClickListener(this);
        recent_business = (RelativeLayout)findViewById(R.id.recent_business);
        recent_business.setOnClickListener(this);
        full_triangle = (ImageView)findViewById(R.id.full_triangle);
        popular_triangle = (ImageView)findViewById(R.id.popular_triangle);
        recent_triangle = (ImageView)findViewById(R.id.recent_triangle);
        pullToRefresh = (SwipeRefreshLayout)findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchEnabled = false;
                businessListRequest = new BusinessListRequest();
                businessListRequest.setPageNumber(requestPageNumber);
                businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                businessListRequest.setSortFactor(bizSortFactor);
                requestHamPayBusiness = new RequestHamPayBusiness(context, new RequestBusinessListTaskCompleteListener(searchEnabled));
                requestHamPayBusiness.execute(businessListRequest);
            }
        });
        businessListView = (ListView)findViewById(R.id.businessListView);
        businessListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(context, BusinessPaymentInfoActivity.class);
                intent.putExtra(Constants.BUSINESS_INFO, businessDTOs.get(position));
                context.startActivity(intent);
            }
        });

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        hamPayBusinessesAdapter = new HamPayBusinessesAdapter(activity, prefs.getString(Constants.LOGIN_TOKEN_ID, ""));

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);


        inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

        searchImage = (ImageView) findViewById(R.id.searchImage);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchPhraseText.getText().toString().length() > 0) {
                    performBusinessSearch(searchPhraseText.getText().toString());
                }
            }
        });

        searchPhraseText = (FacedEditText) findViewById(R.id.searchPhraseText);

        searchPhraseText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().length() == 0) {
                    requestPageNumber = 0;
                    searchEnabled = false;
                    FINISHED_SCROLLING = false;
                    onLoadMore = false;
                    businessListRequest.setPageNumber(requestPageNumber);
                    businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    businessListRequest.setSortFactor(BizSortFactor.NAME);
                    requestHamPayBusiness = new RequestHamPayBusiness(context, new RequestBusinessListTaskCompleteListener(searchEnabled));
                    requestHamPayBusiness.execute(businessListRequest);

                    inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);

                    hamPayBusinessesAdapter.clear();
                    businessDTOs.clear();

                    hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

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

        hamPayDialog = new HamPayDialog(activity);
        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
        editor.commit();
        businessListRequest = new BusinessListRequest();
        businessListRequest.setPageNumber(requestPageNumber);
        businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        businessListRequest.setSortFactor(BizSortFactor.NAME);
        requestHamPayBusiness = new RequestHamPayBusiness(this, new RequestBusinessListTaskCompleteListener(searchEnabled));
        requestHamPayBusiness.execute(businessListRequest);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.full_business:
                changeTab(1);
                break;

            case R.id.popular_business:
                changeTab(2);
                break;

            case R.id.recent_business:
                changeTab(3);
                break;
        }

    }

    private void changeTab(int index){
        switch (index){
            case 1:
                businessListRequest = new BusinessListRequest();
                requestPageNumber = 0;
                bizSortFactor = BizSortFactor.NAME;
                businessListRequest.setPageNumber(requestPageNumber);
                businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                businessListRequest.setSortFactor(bizSortFactor);
                requestHamPayBusiness = new RequestHamPayBusiness(this, new RequestBusinessListTaskCompleteListener(searchEnabled));
                requestHamPayBusiness.execute(businessListRequest);
                full_business.setBackgroundColor(getResources().getColor(R.color.app_origin));
                popular_business.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                recent_business.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                full_triangle.setVisibility(View.VISIBLE);
                popular_triangle.setVisibility(View.GONE);
                recent_triangle.setVisibility(View.GONE);
                break;
            case 2:
                businessListRequest = new BusinessListRequest();
                requestPageNumber = 0;
                bizSortFactor = BizSortFactor.DATE;
                businessListRequest.setPageNumber(requestPageNumber);
                businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                businessListRequest.setSortFactor(bizSortFactor);
                requestHamPayBusiness = new RequestHamPayBusiness(this, new RequestBusinessListTaskCompleteListener(searchEnabled));
                requestHamPayBusiness.execute(businessListRequest);
                full_business.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                popular_business.setBackgroundColor(getResources().getColor(R.color.app_origin));
                recent_business.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                full_triangle.setVisibility(View.GONE);
                popular_triangle.setVisibility(View.VISIBLE);
                recent_triangle.setVisibility(View.GONE);
                break;

            case 3:
                businessListRequest = new BusinessListRequest();
                requestPageNumber = 0;
                bizSortFactor = BizSortFactor.DATE;
                businessListRequest.setPageNumber(requestPageNumber);
                businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                businessListRequest.setSortFactor(bizSortFactor);
                requestHamPayBusiness = new RequestHamPayBusiness(this, new RequestBusinessListTaskCompleteListener(searchEnabled));
                requestHamPayBusiness.execute(businessListRequest);
                full_business.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                popular_business.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                recent_business.setBackgroundColor(getResources().getColor(R.color.app_origin));
                full_triangle.setVisibility(View.GONE);
                popular_triangle.setVisibility(View.GONE);
                recent_triangle.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void performBusinessSearch(String searchTerm) {
        requestPageNumber = 0;
        searchEnabled = true;
        FINISHED_SCROLLING = false;
        onLoadMore = false;
        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
        editor.commit();
        businessSearchRequest = new BusinessSearchRequest();
        businessSearchRequest.setPageNumber(requestPageNumber);
        businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        businessListRequest.setSortFactor(BizSortFactor.NAME);
        businessSearchRequest.setTerm(searchTerm);
        requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(searchEnabled));
        requestSearchHamPayBusiness.execute(businessSearchRequest);
        inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);

        hamPayBusinessesAdapter.clear();
        businessDTOs.clear();

        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

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
            pullToRefresh.setRefreshing(false);

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
                                initDobList(getWindow().getDecorView().getRootView(), businessListView);
                                businessListView.setAdapter(hamPayBusinessesAdapter);
                            }
                        }
                    }

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Business List")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());

                }else if (businessListResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }else {
                    if (!searchEnabled) {
                        businessListRequest.setPageNumber(requestPageNumber);
                        businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                        businessListRequest.setSortFactor(BizSortFactor.NAME);
                        requestHamPayBusiness = new RequestHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(false));
                        new HamPayDialog(activity).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest,
                                businessListResponseMessage.getService().getResultStatus().getCode(),
                                businessListResponseMessage.getService().getResultStatus().getDescription());
                    }else {
                        businessSearchRequest.setPageNumber(requestPageNumber);
                        businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                        businessListRequest.setSortFactor(BizSortFactor.NAME);
                        requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(true));
                        new HamPayDialog(activity).showFailBusinessSearchListDialog(requestSearchHamPayBusiness, businessSearchRequest,
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
                    businessListRequest.setSortFactor(BizSortFactor.NAME);
                    requestHamPayBusiness = new RequestHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(false));
                    new HamPayDialog(activity).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest,
                            Constants.LOCAL_ERROR_CODE,
                            getString(R.string.msg_fail_business_list));
                }else {
                    businessSearchRequest.setPageNumber(requestPageNumber);
                    businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    businessListRequest.setSortFactor(BizSortFactor.NAME);
                    requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(true));
                    new HamPayDialog(activity).showFailBusinessSearchListDialog(requestSearchHamPayBusiness, businessSearchRequest,
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
                                businessListRequest.setSortFactor(BizSortFactor.NAME);
                                requestHamPayBusiness = new RequestHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(false));
                                requestHamPayBusiness.execute(businessListRequest);
                            } else {
                                businessSearchRequest.setPageNumber(requestPageNumber);
                                businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                                businessListRequest.setSortFactor(BizSortFactor.NAME);
                                requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(searchEnabled));
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

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(context, HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (activity != null) {
            finish();
            startActivity(intent);
        }
    }

}
