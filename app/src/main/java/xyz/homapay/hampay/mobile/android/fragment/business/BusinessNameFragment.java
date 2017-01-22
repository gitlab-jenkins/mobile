package xyz.homapay.hampay.mobile.android.fragment.business;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.TnxSortFactor;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.enums.BizSortFactor;
import xyz.homapay.hampay.common.core.model.request.BusinessListRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessSearchRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.BusinessDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.BusinessesListActivity;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.adapter.HamPayBusinessesAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestHamPayBusiness;
import xyz.homapay.hampay.mobile.android.async.RequestSearchHamPayBusiness;
import xyz.homapay.hampay.mobile.android.common.messages.MessageOnBackPressedOnPendingAct;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSheetStateChanged;
import xyz.homapay.hampay.mobile.android.component.CustomTextView;
import xyz.homapay.hampay.mobile.android.component.doblist.DobList;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoEmptyViewException;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoListviewException;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.fragment.pending.FrgPendingRequests;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.font.FontFace;

/**
 * Created by mohammad on 1/22/17.
 */

public class BusinessNameFragment extends Fragment {

    private View rootView;
    private ListView businessListView;
    private SwipeRefreshLayout pullToRefresh;
    private HamPayDialog hamPayDialog;
    private List<BusinessDTO> businessDTOs;
    private RelativeLayout rlSearchLayout;
    private boolean FINISHED_SCROLLING = false;
    private int requestPageNumber = 0;
    private boolean onLoadMore = false;
    private HamPayBusinessesAdapter hamPayBusinessesAdapter;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private BusinessListRequest businessListRequest;
    private BusinessSearchRequest businessSearchRequest;
    private RequestSearchHamPayBusiness requestSearchHamPayBusiness;
    private RequestHamPayBusiness requestHamPayBusiness;
    private DobList dobList;
    private boolean searchEnabled = false;
    private BizSortFactor bizSortFactor = BizSortFactor.NAME;
    private FacedEditText etSearchPhraseText;
    private InputMethodManager inputMethodManager;
    private ImageView imgSearchImage;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.fab_sheet)
    View fab_sheet;

    @BindView(R.id.overlay)
    View overlay;

    @BindView(R.id.fab_sheet_item_all)
    CustomTextView fabAll;

    @BindView(R.id.fab_sheet_item_popular)
    CustomTextView fabPoular;

    @BindView(R.id.fab_sheet_item_recent)
    CustomTextView fabRecent;

    private MaterialSheetFab materialSheetFab;

    public static BusinessNameFragment newInstance() {
        BusinessNameFragment fragment = new BusinessNameFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_business_name, null);

        ButterKnife.bind(this, rootView);

        prefs = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE).edit();


        int sheetColor = ContextCompat.getColor(getActivity(), R.color.app_origin);
        int fabColor = ContextCompat.getColor(getActivity(), R.color.white);

        materialSheetFab = new MaterialSheetFab(fab, fab_sheet, overlay, sheetColor, fabColor);
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                super.onShowSheet();
                EventBus.getDefault().post(new MessageSheetStateChanged(true));
                switch (bizSortFactor) {
                    case DATE:
                        fabAll.setTypeface(FontFace.getInstance(getActivity()).getVAZIR_BOLD());
                        fabPoular.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        fabRecent.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        //
                        fabAll.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_origin));
                        fabPoular.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        fabRecent.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        break;
                    case NAME:
                        fabAll.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        fabPoular.setTypeface(FontFace.getInstance(getActivity()).getVAZIR_BOLD());
                        fabRecent.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        //
                        fabAll.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        fabPoular.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_origin));
                        fabRecent.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        break;
                    default:
                        fabAll.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        fabPoular.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        fabRecent.setTypeface(FontFace.getInstance(getActivity()).getVAZIR_BOLD());
                        //
                        fabAll.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        fabPoular.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        fabRecent.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_origin));
                        break;
                }
            }

            @Override
            public void onHideSheet() {
                super.onHideSheet();
                EventBus.getDefault().post(new MessageSheetStateChanged(false));
            }
        });

        fabAll.setOnClickListener(view -> {
            materialSheetFab.hideSheet();
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
            businessListRequest.setSortFactor(BizSortFactor.DATE);
            requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
            requestHamPayBusiness.execute(businessListRequest);
        });
        fabPoular.setOnClickListener(view -> {
            bizSortFactor = BizSortFactor.DATE;
            materialSheetFab.hideSheet();
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
            businessListRequest.setSortFactor(BizSortFactor.DATE);
            requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
            requestHamPayBusiness.execute(businessListRequest);
        });
        fabRecent.setOnClickListener(view -> {
            bizSortFactor = BizSortFactor.NAME;
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
            businessListRequest.setSortFactor(BizSortFactor.DATE);
            requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
            requestHamPayBusiness.execute(businessListRequest);
        });

        imgSearchImage = (ImageView) rootView.findViewById(R.id.imgSearchImage);
        imgSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etSearchPhraseText.getText().toString().trim().length() > 0) {
                    performBusinessSearch(etSearchPhraseText.getText().toString());
                }
            }
        });
        etSearchPhraseText = (FacedEditText) rootView.findViewById(R.id.etSearchPhraseText);
        businessListView = (ListView) rootView.findViewById(R.id.businessListView);
        pullToRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.pullToRefresh);
        hamPayDialog = new HamPayDialog(getActivity());
        businessDTOs = new ArrayList<>();
        rlSearchLayout = (RelativeLayout) rootView.findViewById(R.id.rlSearchLayout);
        hamPayBusinessesAdapter = new HamPayBusinessesAdapter(getActivity(), prefs.getString(Constants.LOGIN_TOKEN_ID, ""));


        hamPayDialog = new HamPayDialog(getActivity());
        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
        editor.commit();
        businessListRequest = new BusinessListRequest();
        businessListRequest.setPageNumber(requestPageNumber);
        businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        businessListRequest.setSortFactor(BizSortFactor.NAME);
        requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
        requestHamPayBusiness.execute(businessListRequest);

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
            requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
            requestHamPayBusiness.execute(businessListRequest);
        });

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

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
                    requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
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


        return rootView;
    }

    public class RequestBusinessListTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BusinessListResponse>> {

        List<BusinessDTO> newBusinessDTOs;
        boolean searchEnabled;
        ServiceEvent serviceName = ServiceEvent.BUSINESS_LIST_FAILURE;
        LogEvent logEvent = new LogEvent(getActivity());

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
                        Toast.makeText(getActivity(), getString(R.string.no_search_result), Toast.LENGTH_SHORT).show();
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
                                initDobList(getActivity().getWindow().getDecorView().getRootView(), businessListView);
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
                        requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(false));
                        new HamPayDialog(getActivity()).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest,
                                businessListResponseMessage.getService().getResultStatus().getCode(),
                                businessListResponseMessage.getService().getResultStatus().getDescription());
                    } else {
                        businessSearchRequest.setPageNumber(requestPageNumber);
                        businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                        businessListRequest.setSortFactor(BizSortFactor.NAME);
                        requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(true));
                        new HamPayDialog(getActivity()).showFailBusinessSearchListDialog(requestSearchHamPayBusiness, businessSearchRequest,
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
                    requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(false));
                    new HamPayDialog(getActivity()).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest,
                            Constants.LOCAL_ERROR_CODE,
                            getString(R.string.msg_fail_business_list));
                } else {
                    businessSearchRequest.setPageNumber(requestPageNumber);
                    businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    businessListRequest.setSortFactor(BizSortFactor.NAME);
                    requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(true));
                    new HamPayDialog(getActivity()).showFailBusinessSearchListDialog(requestSearchHamPayBusiness, businessSearchRequest,
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
                            requestHamPayBusiness = new RequestHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(false));
                            requestHamPayBusiness.execute(businessListRequest);
                        } else {
                            businessSearchRequest.setPageNumber(requestPageNumber);
                            businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                            businessListRequest.setSortFactor(BizSortFactor.NAME);
                            requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
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

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(getActivity(), HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (getActivity() != null) {
            getActivity().finish();
            startActivity(intent);
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
        requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(getActivity(), new RequestBusinessListTaskCompleteListener(searchEnabled));
        requestSearchHamPayBusiness.execute(businessSearchRequest);
        inputMethodManager.hideSoftInputFromWindow(etSearchPhraseText.getWindowToken(), 0);

        hamPayBusinessesAdapter.clear();
        businessDTOs.clear();

        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

    }

    @Subscribe
    public void onBackPressed(MessageOnBackPressedOnPendingAct onBackPressedOnPendingAct) {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        }
    }

}
