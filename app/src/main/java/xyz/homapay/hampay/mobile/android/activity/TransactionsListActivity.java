package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;
import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.TnxSortFactor;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.TransactionListRequest;
import xyz.homapay.hampay.common.core.model.response.TransactionListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.UserTransactionAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestUserTransaction;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSheetStateChanged;
import xyz.homapay.hampay.mobile.android.component.CustomTextView;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.doblist.DobList;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoEmptyViewException;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoListviewException;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.fragment.pending.FrgPendingRequests;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.font.FontFace;

public class TransactionsListActivity extends AppCompatActivity {

    @BindView(R.id.no_transaction)
    FacedTextView no_transaction;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.transactionListView)
    ListView transactionListView;
    @BindView(R.id.loading)
    ProgressBar loading;
    private TnxSortFactor sortFactor = TnxSortFactor.DEFAULT;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private DobList dobList;
    private UserTransactionAdapter userTransactionAdapter;
    private HamPayDialog hamPayDialog;
    private RequestUserTransaction requestUserTransaction;
    private TransactionListRequest transactionListRequest;
    private int requestPageNumber = 0;
    private boolean FINISHED_SCROLLING = false;
    private boolean onLoadMore = false;
    private List<TransactionDTO> transactionDTOs;
    private Context context;
    private Activity activity;
    private UserProfileDTO userProfile;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.fab_sheet)
    View fab_sheet;

    @BindView(R.id.overlay)
    View overlay;

    @BindView(R.id.fab_sheet_item_all)
    CustomTextView fabAll;

    @BindView(R.id.fab_sheet_item_business)
    CustomTextView fabBusiness;

    @BindView(R.id.fab_sheet_item_individual)
    CustomTextView fabIndividual;

    private MaterialSheetFab materialSheetFab;

    @Override
    protected void onResume() {
        super.onResume();
        PugNotification.with(context).cancel(Constants.TRANSACTIONS_NOTIFICATION_IDENTIFIER);
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
        PugNotification.with(context).cancel(Constants.TRANSACTIONS_NOTIFICATION_IDENTIFIER);
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    public void backActionBar(View view) {
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (requestUserTransaction != null) {
            if (!requestUserTransaction.isCancelled())
                requestUserTransaction.cancel(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_list);
        ButterKnife.bind(this);

        context = this;
        activity = TransactionsListActivity.this;

        int sheetColor = ContextCompat.getColor(activity, R.color.app_origin);
        int fabColor = ContextCompat.getColor(activity, R.color.white);
        materialSheetFab = new MaterialSheetFab(fab, fab_sheet, overlay, sheetColor, fabColor);
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                super.onShowSheet();
                EventBus.getDefault().post(new MessageSheetStateChanged(true));
                switch (sortFactor) {
                    case DEFAULT:
                        fabAll.setTypeface(FontFace.getInstance(activity).getVAZIR_BOLD());
                        fabBusiness.setTypeface(FontFace.getInstance(activity).getVAZIR());
                        fabIndividual.setTypeface(FontFace.getInstance(activity).getVAZIR());
                        //
                        fabAll.setTextColor(ContextCompat.getColor(activity, R.color.app_origin));
                        fabBusiness.setTextColor(ContextCompat.getColor(activity, R.color.black));
                        fabIndividual.setTextColor(ContextCompat.getColor(activity, R.color.black));
                        break;
                    case COMMERCIAL:
                        fabAll.setTypeface(FontFace.getInstance(activity).getVAZIR());
                        fabBusiness.setTypeface(FontFace.getInstance(activity).getVAZIR_BOLD());
                        fabIndividual.setTypeface(FontFace.getInstance(activity).getVAZIR());
                        //
                        fabAll.setTextColor(ContextCompat.getColor(activity, R.color.black));
                        fabBusiness.setTextColor(ContextCompat.getColor(activity, R.color.app_origin));
                        fabIndividual.setTextColor(ContextCompat.getColor(activity, R.color.black));
                        break;
                    case INDIVIDUAL:
                        fabAll.setTypeface(FontFace.getInstance(activity).getVAZIR());
                        fabBusiness.setTypeface(FontFace.getInstance(activity).getVAZIR());
                        fabIndividual.setTypeface(FontFace.getInstance(activity).getVAZIR_BOLD());
                        //
                        fabAll.setTextColor(ContextCompat.getColor(activity, R.color.black));
                        fabBusiness.setTextColor(ContextCompat.getColor(activity, R.color.black));
                        fabIndividual.setTextColor(ContextCompat.getColor(activity, R.color.app_origin));
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
            if (requestUserTransaction.getStatus() == AsyncTask.Status.RUNNING) {
                requestUserTransaction.cancel(true);
            }
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            FINISHED_SCROLLING = false;
            onLoadMore = false;
            userTransactionAdapter.clear();
            transactionDTOs.clear();
            requestPageNumber = 0;
            sortFactor = TnxSortFactor.DEFAULT;
            transactionListRequest = new TransactionListRequest();
            transactionListRequest.setPageNumber(requestPageNumber);
            transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
            transactionListRequest.setSortFactor(sortFactor);
            requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
            requestUserTransaction.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, transactionListRequest);
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        });
        fabBusiness.setOnClickListener(view -> {
            materialSheetFab.hideSheet();
            if (requestUserTransaction.getStatus() == AsyncTask.Status.RUNNING) {
                requestUserTransaction.cancel(true);
            }
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            FINISHED_SCROLLING = false;
            onLoadMore = false;
            userTransactionAdapter.clear();
            transactionDTOs.clear();
            requestPageNumber = 0;
            sortFactor = TnxSortFactor.COMMERCIAL;
            transactionListRequest = new TransactionListRequest();
            transactionListRequest.setPageNumber(requestPageNumber);
            transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
            transactionListRequest.setSortFactor(sortFactor);
            requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
            requestUserTransaction.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, transactionListRequest);
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        });
        fabIndividual.setOnClickListener(view -> {
            materialSheetFab.hideSheet();
            if (requestUserTransaction.getStatus() == AsyncTask.Status.RUNNING) {
                requestUserTransaction.cancel(true);
            }
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            FINISHED_SCROLLING = false;
            onLoadMore = false;
            userTransactionAdapter.clear();
            transactionDTOs.clear();
            requestPageNumber = 0;
            sortFactor = TnxSortFactor.INDIVIDUAL;
            transactionListRequest = new TransactionListRequest();
            transactionListRequest.setPageNumber(requestPageNumber);
            transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
            transactionListRequest.setSortFactor(sortFactor);
            requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
            requestUserTransaction.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, transactionListRequest);
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        });

        PugNotification.with(context).cancel(Constants.TRANSACTIONS_NOTIFICATION_IDENTIFIER);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        userProfile = (UserProfileDTO) getIntent().getSerializableExtra(Constants.USER_PROFILE);
        hamPayDialog = new HamPayDialog(activity);

        transactionDTOs = new ArrayList<>();
        userTransactionAdapter = new UserTransactionAdapter(activity);
        pullToRefresh.setOnRefreshListener(() -> {
            if (requestUserTransaction.getStatus() == AsyncTask.Status.RUNNING) {
                requestUserTransaction.cancel(true);
            }
            FINISHED_SCROLLING = false;
            onLoadMore = false;
            userTransactionAdapter.clear();
            transactionDTOs.clear();
            requestPageNumber = 0;
            transactionListRequest = new TransactionListRequest();
            transactionListRequest.setPageNumber(requestPageNumber);
            transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
            transactionListRequest.setSortFactor(sortFactor);
            requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
            requestUserTransaction.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, transactionListRequest);
        });
        transactionListView.setOnItemClickListener((parent, view, position, id) -> {
            if (transactionDTOs != null) {
                if (transactionDTOs.size() > 0) {
                    Intent intent = new Intent();
                    intent.setClass(activity, TransactionDetailActivity.class);
                    intent.putExtra(Constants.USER_TRANSACTION_DTO, transactionDTOs.get(position));
                    intent.putExtra(Constants.USER_PROFILE, userProfile);
                    startActivity(intent);
                }
            }
        });

        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
        editor.commit();
        requestPageNumber = 0;
        transactionListRequest = new TransactionListRequest();
        transactionListRequest.setPageNumber(requestPageNumber);
        transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        transactionListRequest.setSortFactor(sortFactor);
        requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
        requestUserTransaction.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, transactionListRequest);
        try {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initDobList(ListView listView) {

        dobList = new DobList();
        try {

            dobList.register(listView);

            dobList.addDefaultLoadingFooterView();
            dobList.setOnLoadMoreListener(totalItemCount -> {
                onLoadMore = true;
                if (!FINISHED_SCROLLING) {
                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();
                    transactionListRequest.setPageNumber(requestPageNumber);
                    requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                    requestUserTransaction.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, transactionListRequest);
                    loading.setVisibility(View.VISIBLE);

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

        addDummyData(transactionDTOs.size());
    }

    protected void addDummyData(int itemsCount) {

        addItems(userTransactionAdapter.getCount(), userTransactionAdapter.getCount() + itemsCount);

        dobList.finishLoading();

    }

    protected void addItems(int from, int to) {
        for (int i = from; i < to; i++) {
            if (i < transactionDTOs.size())
                userTransactionAdapter.addItem(transactionDTOs.get(i));
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

    public class RequestUserTransactionsTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<TransactionListResponse>> {

        List<TransactionDTO> newTransactionDTOs;
        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);

        @Override
        public void onTaskComplete(ResponseMessage<TransactionListResponse> transactionListResponseMessage) {
            hamPayDialog.dismisWaitingDialog();
            pullToRefresh.setRefreshing(false);
            loading.setVisibility(View.INVISIBLE);

            PugNotification.with(context).cancel(Constants.TRANSACTIONS_NOTIFICATION_IDENTIFIER);

            if (transactionListResponseMessage != null) {

                if (transactionListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.TRANSACTION_LIST_SUCCESS;
                    newTransactionDTOs = transactionListResponseMessage.getService().getTransactions();
                    transactionDTOs.addAll(newTransactionDTOs);

                    if (transactionDTOs.size() == 0) {
                        no_transaction.setVisibility(View.VISIBLE);
                        transactionListView.setVisibility(View.GONE);
                    } else {
                        no_transaction.setVisibility(View.GONE);
                        transactionListView.setVisibility(View.VISIBLE);
                    }

                    if (transactionDTOs != null) {

                        if (newTransactionDTOs.size() == 0 || newTransactionDTOs.size() < Constants.DEFAULT_PAGE_SIZE) {
                            FINISHED_SCROLLING = true;
                        }

                        if (transactionDTOs.size() > 0) {

                            requestPageNumber++;

                            if (onLoadMore) {
                                if (newTransactionDTOs != null)
                                    addDummyData(newTransactionDTOs.size());
                            } else {
                                initDobList(transactionListView);
                                transactionListView.setAdapter(userTransactionAdapter);
                            }
                        }

                    }

                } else if (transactionListResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.TRANSACTION_LIST_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.TRANSACTION_LIST_FAILURE;
                    transactionListRequest.setPageNumber(requestPageNumber);
                    requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                    new HamPayDialog(activity).showFailUserTransactionDialog(requestUserTransaction, transactionListRequest,
                            transactionListResponseMessage.getService().getResultStatus().getCode(),
                            transactionListResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                serviceName = ServiceEvent.TRANSACTION_LIST_FAILURE;
                transactionListRequest.setPageNumber(requestPageNumber);
                requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                new HamPayDialog(activity).showFailUserTransactionDialog(requestUserTransaction, transactionListRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_user_transation));
            }
            logEvent.log(serviceName);
        }

        @Override
        public void onTaskPreRun() {
        }
    }

}
