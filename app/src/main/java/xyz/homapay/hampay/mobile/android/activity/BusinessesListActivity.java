package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoEmptyViewException;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoListviewException;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class BusinessesListActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.rlRecentBusiness)
    RelativeLayout rlRecentBusiness;
    @BindView(R.id.rlFullBusiness)
    RelativeLayout rlFullBusiness;
    @BindView(R.id.rlPopularBusiness)
    RelativeLayout rlPopularBusiness;
    @BindView(R.id.full_triangle)
    ImageView full_triangle;
    @BindView(R.id.popular_triangle)
    ImageView popular_triangle;
    @BindView(R.id.recent_triangle)
    ImageView recent_triangle;

    @BindView(R.id.rlSearchLayout)
    RelativeLayout rlSearchLayout;

    @BindView(R.id.etSearchPhraseText)
    FacedEditText etSearchPhraseText;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.businessListView)
    ListView businessListView;
    private Context context;
    private Activity activity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private HamPayDialog hamPayDialog;
    private boolean onLoadMore = false;
    private DobList dobList;
    private List<BusinessDTO> businessDTOs;
    private HamPayBusinessesAdapter hamPayBusinessesAdapter;
    private boolean FINISHED_SCROLLING = false;
    private InputMethodManager inputMethodManager;
    private BusinessListRequest businessListRequest;
    private BusinessSearchRequest businessSearchRequest;
    private RequestSearchHamPayBusiness requestSearchHamPayBusiness;
    private RequestHamPayBusiness requestHamPayBusiness;
    private int requestPageNumber = 0;
    private boolean searchEnabled = false;
    private BizSortFactor bizSortFactor = BizSortFactor.NAME;

    public void backActionBar(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
        if (requestHamPayBusiness != null) {
            if (!requestHamPayBusiness.isCancelled())
                requestHamPayBusiness.cancel(true);
        }

        if (requestSearchHamPayBusiness != null) {
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
        ButterKnife.bind(this);

        context = this;
        activity = BusinessesListActivity.this;

        pullToRefresh.setOnRefreshListener(() -> {
            if (requestHamPayBusiness.getStatus() == AsyncTask.Status.RUNNING) {
                requestHamPayBusiness.cancel(true);
            }
            FINISHED_SCROLLING = false;
            onLoadMore = false;
            hamPayBusinessesAdapter.clear();
            businessDTOs.clear();
            requestPageNumber = 0;
            searchEnabled = false;
            businessListRequest = new BusinessListRequest();
            businessListRequest.setPageNumber(requestPageNumber);
            businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
            businessListRequest.setSortFactor(bizSortFactor);
            requestHamPayBusiness = new RequestHamPayBusiness(context, new RequestBusinessListTaskCompleteListener(searchEnabled));
            requestHamPayBusiness.execute(businessListRequest);
        });

        businessListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            intent.setClass(context, BusinessPaymentInfoActivity.class);
            intent.putExtra(Constants.BUSINESS_INFO, businessDTOs.get(position));
            context.startActivity(intent);
        });

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        hamPayBusinessesAdapter = new HamPayBusinessesAdapter(activity, prefs.getString(Constants.LOGIN_TOKEN_ID, ""));

        inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

        etSearchPhraseText.addTextChangedListener(new TextWatcher() {
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
                    inputMethodManager.hideSoftInputFromWindow(etSearchPhraseText.getWindowToken(), 0);
                    hamPayBusinessesAdapter.clear();
                    businessDTOs.clear();
                    hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etSearchPhraseText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performBusinessSearch(etSearchPhraseText.getText().toString());
                return true;
            }
            return false;
        });

        businessDTOs = new ArrayList<>();

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

        switch (v.getId()) {
            case R.id.rlFullBusiness:
                changeTab(1);
                break;

            case R.id.rlPopularBusiness:
                changeTab(2);
                break;

            case R.id.rlRecentBusiness:
                changeTab(3);
                break;

            case R.id.imgSearchImage:
                if (etSearchPhraseText.getText().toString().trim().length() > 0) {
                    performBusinessSearch(etSearchPhraseText.getText().toString());
                }
                break;
        }

    }

    private void changeTab(int index) {
        if (requestHamPayBusiness.getStatus() == AsyncTask.Status.RUNNING) {
            requestHamPayBusiness.cancel(true);
        }
        FINISHED_SCROLLING = false;
        onLoadMore = false;
        switch (index) {
            case 1:
                hamPayBusinessesAdapter.clear();
                businessDTOs.clear();
                hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                businessListRequest = new BusinessListRequest();
                requestPageNumber = 0;
                bizSortFactor = BizSortFactor.NAME;
                businessListRequest.setPageNumber(requestPageNumber);
                businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                businessListRequest.setSortFactor(bizSortFactor);
                requestHamPayBusiness = new RequestHamPayBusiness(this, new RequestBusinessListTaskCompleteListener(searchEnabled));
                requestHamPayBusiness.execute(businessListRequest);
                rlFullBusiness.setBackgroundColor(getResources().getColor(R.color.app_origin));
                rlPopularBusiness.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                rlRecentBusiness.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                full_triangle.setVisibility(View.VISIBLE);
                popular_triangle.setVisibility(View.GONE);
                recent_triangle.setVisibility(View.GONE);
                break;
            case 2:
                hamPayBusinessesAdapter.clear();
                businessDTOs.clear();
                hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                businessListRequest = new BusinessListRequest();
                requestPageNumber = 0;
                bizSortFactor = BizSortFactor.DATE;
                businessListRequest.setPageNumber(requestPageNumber);
                businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                businessListRequest.setSortFactor(bizSortFactor);
                requestHamPayBusiness = new RequestHamPayBusiness(this, new RequestBusinessListTaskCompleteListener(searchEnabled));
                requestHamPayBusiness.execute(businessListRequest);
                rlFullBusiness.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                rlPopularBusiness.setBackgroundColor(getResources().getColor(R.color.app_origin));
                rlRecentBusiness.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                full_triangle.setVisibility(View.GONE);
                popular_triangle.setVisibility(View.VISIBLE);
                recent_triangle.setVisibility(View.GONE);
                break;

            case 3:
                hamPayBusinessesAdapter.clear();
                businessDTOs.clear();
                hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                businessListRequest = new BusinessListRequest();
                requestPageNumber = 0;
                bizSortFactor = BizSortFactor.DATE;
                businessListRequest.setPageNumber(requestPageNumber);
                businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                businessListRequest.setSortFactor(bizSortFactor);
                requestHamPayBusiness = new RequestHamPayBusiness(this, new RequestBusinessListTaskCompleteListener(searchEnabled));
                requestHamPayBusiness.execute(businessListRequest);
                rlFullBusiness.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                rlPopularBusiness.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                rlRecentBusiness.setBackgroundColor(getResources().getColor(R.color.app_origin));
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
        inputMethodManager.hideSoftInputFromWindow(etSearchPhraseText.getWindowToken(), 0);

        hamPayBusinessesAdapter.clear();
        businessDTOs.clear();

        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

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

    public class RequestBusinessListTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BusinessListResponse>> {

        List<BusinessDTO> newBusinessDTOs;
        boolean searchEnabled;
        ServiceEvent serviceName = ServiceEvent.BUSINESS_LIST_FAILURE;
        LogEvent logEvent = new LogEvent(context);

        public RequestBusinessListTaskCompleteListener(boolean searchEnabled) {
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
                    if (!searchEnabled && businessDTOs.size() > 0) {
                        rlSearchLayout.setVisibility(View.VISIBLE);
                    } else if (!searchEnabled) {
                        rlSearchLayout.setVisibility(View.GONE);
                    }

                    if (searchEnabled && businessDTOs.size() == 0) {
                        Toast.makeText(activity, getString(R.string.no_search_result), Toast.LENGTH_SHORT).show();
                    }

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
                    serviceName = ServiceEvent.BUSINESS_LIST_SUCCESS;

                } else if (businessListResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.BUSINESS_LIST_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.BUSINESS_LIST_FAILURE;
                    if (!searchEnabled) {
                        businessListRequest.setPageNumber(requestPageNumber);
                        businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                        businessListRequest.setSortFactor(BizSortFactor.NAME);
                        requestHamPayBusiness = new RequestHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(false));
                        new HamPayDialog(activity).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest,
                                businessListResponseMessage.getService().getResultStatus().getCode(),
                                businessListResponseMessage.getService().getResultStatus().getDescription());
                    } else {
                        businessSearchRequest.setPageNumber(requestPageNumber);
                        businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                        businessListRequest.setSortFactor(BizSortFactor.NAME);
                        requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(true));
                        new HamPayDialog(activity).showFailBusinessSearchListDialog(requestSearchHamPayBusiness, businessSearchRequest,
                                businessListResponseMessage.getService().getResultStatus().getCode(),
                                businessListResponseMessage.getService().getResultStatus().getDescription());
                        requestSearchHamPayBusiness.execute(businessSearchRequest);
                    }
                }
            } else {
                if (!searchEnabled) {
                    businessListRequest.setPageNumber(requestPageNumber);
                    businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    businessListRequest.setSortFactor(BizSortFactor.NAME);
                    requestHamPayBusiness = new RequestHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(false));
                    new HamPayDialog(activity).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest,
                            Constants.LOCAL_ERROR_CODE,
                            getString(R.string.msg_fail_business_list));
                } else {
                    businessSearchRequest.setPageNumber(requestPageNumber);
                    businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    businessListRequest.setSortFactor(BizSortFactor.NAME);
                    requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(true));
                    new HamPayDialog(activity).showFailBusinessSearchListDialog(requestSearchHamPayBusiness, businessSearchRequest,
                            Constants.LOCAL_ERROR_CODE,
                            getString(R.string.msg_fail_business_search_list));
                    requestSearchHamPayBusiness.execute(businessSearchRequest);
                }
            }
            logEvent.log(serviceName);
        }

        @Override
        public void onTaskPreRun() {
        }

        private void initDobList(View rootView, ListView listView) {

            dobList = new DobList();
            try {

                dobList.register(listView);

                dobList.addDefaultLoadingFooterView();

                dobList.setOnLoadMoreListener(totalItemCount -> {

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
