package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.common.common.TnxSortFactor;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.TransactionListRequest;
import xyz.homapay.hampay.common.core.model.response.TransactionListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.UserTransactionAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestUserTransaction;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.doblist.DobList;
import xyz.homapay.hampay.mobile.android.component.doblist.events.OnLoadMoreListener;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoEmptyViewException;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoListviewException;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class TransactionsListActivity extends AppCompatActivity implements View.OnClickListener {

    private SwipeRefreshLayout pullToRefresh;
    private ListView transactionListView;
    UserTransactionAdapter userTransactionAdapter;
    private boolean FINISHED_SCROLLING = false;

    HamPayDialog hamPayDialog;

    FacedTextView no_transaction;
    private boolean onLoadMore = false;

    private List<TransactionDTO> transactionDTOs;

    RequestUserTransaction requestUserTransaction;
    TransactionListRequest transactionListRequest;
    int requestPageNumber = 0;
    TnxSortFactor sortFactor = TnxSortFactor.DEFAULT;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Tracker hamPayGaTracker;

    private Context context;
    private Activity activity;

    private RelativeLayout full_transaction;
    private RelativeLayout business_transaction;
    private RelativeLayout invoice_transaction;
    private ImageView full_triangle;
    private ImageView business_triangle;
    private ImageView invoice_triangle;

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

    public void backActionBar(View view){
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (requestUserTransaction != null){
            if (!requestUserTransaction.isCancelled())
                requestUserTransaction.cancel(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_list);

        context = this;
        activity = TransactionsListActivity.this;

        PugNotification.with(context).cancel(Constants.TRANSACTIONS_NOTIFICATION_IDENTIFIER);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        hamPayDialog = new HamPayDialog(activity);

        full_transaction = (RelativeLayout)findViewById(R.id.full_transaction);
        full_transaction.setOnClickListener(this);
        business_transaction = (RelativeLayout)findViewById(R.id.business_transaction);
        business_transaction.setOnClickListener(this);
        invoice_transaction = (RelativeLayout)findViewById(R.id.invoice_transaction);
        invoice_transaction.setOnClickListener(this);
        full_triangle = (ImageView)findViewById(R.id.full_triangle);
        business_triangle = (ImageView)findViewById(R.id.business_triangle);
        invoice_triangle = (ImageView)findViewById(R.id.invoice_triangle);


        transactionDTOs = new ArrayList<>();
        userTransactionAdapter = new UserTransactionAdapter(activity);

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        no_transaction = (FacedTextView)findViewById(R.id.no_transaction);

        transactionListView = (ListView)findViewById(R.id.transactionListView);
        pullToRefresh = (SwipeRefreshLayout)findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (requestUserTransaction.getStatus() == AsyncTask.Status.RUNNING){
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
                requestUserTransaction.execute(transactionListRequest);
            }
        });
        transactionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(activity, TransactionDetailActivity.class);
                intent.putExtra(Constants.USER_TRANSACTION_DTO, transactionDTOs.get(position));
                startActivity(intent);
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
        requestUserTransaction.execute(transactionListRequest);
        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.full_transaction:
                if (requestUserTransaction.getStatus() == AsyncTask.Status.RUNNING){
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
                requestUserTransaction.execute(transactionListRequest);
                hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                changeTab(1);
                break;

            case R.id.business_transaction:
                if (requestUserTransaction.getStatus() == AsyncTask.Status.RUNNING){
                    requestUserTransaction.cancel(true);
                }
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                FINISHED_SCROLLING = false;
                onLoadMore = false;
                userTransactionAdapter.clear();
                transactionDTOs.clear();
                requestPageNumber = 0;
                sortFactor = TnxSortFactor.BUSINESS_AND_PURCHASE;
                transactionListRequest = new TransactionListRequest();
                transactionListRequest.setPageNumber(requestPageNumber);
                transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                transactionListRequest.setSortFactor(sortFactor);
                requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                requestUserTransaction.execute(transactionListRequest);
                hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                changeTab(2);
                break;

            case R.id.invoice_transaction:
                if (requestUserTransaction.getStatus() == AsyncTask.Status.RUNNING){
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
                requestUserTransaction.execute(transactionListRequest);
                hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                changeTab(3);
                break;
        }
    }

    private void changeTab(int index){
        switch (index){
            case 1:
                full_transaction.setBackgroundColor(getResources().getColor(R.color.app_origin));
                business_transaction.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                invoice_transaction.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                full_triangle.setVisibility(View.VISIBLE);
                business_triangle.setVisibility(View.GONE);
                invoice_triangle.setVisibility(View.GONE);
                break;
            case 2:
                full_transaction.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                business_transaction.setBackgroundColor(getResources().getColor(R.color.app_origin));
                invoice_transaction.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                full_triangle.setVisibility(View.GONE);
                business_triangle.setVisibility(View.VISIBLE);
                invoice_triangle.setVisibility(View.GONE);
                break;

            case 3:
                full_transaction.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                business_transaction.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                invoice_transaction.setBackgroundColor(getResources().getColor(R.color.app_origin));
                full_triangle.setVisibility(View.GONE);
                business_triangle.setVisibility(View.GONE);
                invoice_triangle.setVisibility(View.VISIBLE);
                break;
        }
    }

    public class RequestUserTransactionsTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<TransactionListResponse>> {

        List<TransactionDTO> newTransactionDTOs;

        @Override
        public void onTaskComplete(ResponseMessage<TransactionListResponse> transactionListResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            pullToRefresh.setRefreshing(false);

            PugNotification.with(context).cancel(Constants.TRANSACTIONS_NOTIFICATION_IDENTIFIER);

            if (transactionListResponseMessage != null) {

                if (transactionListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    newTransactionDTOs = transactionListResponseMessage.getService().getTransactions();
                    transactionDTOs.addAll(newTransactionDTOs);

                    if (transactionDTOs.size() == 0){
                        no_transaction.setVisibility(View.VISIBLE);
                        transactionListView.setVisibility(View.GONE);
                    }else {
                        no_transaction.setVisibility(View.GONE);
                    }

                    if (transactionDTOs != null) {

                        if (newTransactionDTOs.size() == 0 || newTransactionDTOs.size() < Constants.DEFAULT_PAGE_SIZE){
                            FINISHED_SCROLLING = true;
                        }

                        if (transactionDTOs.size() > 0) {

                            requestPageNumber++;

                            if (onLoadMore) {
                                if (newTransactionDTOs != null)
                                    addDummyData(newTransactionDTOs.size());
                            } else {
                                initDobList(getWindow().getDecorView().getRootView(), transactionListView);
                                transactionListView.setAdapter(userTransactionAdapter);
                            }
                        }

                    }

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Transaction List")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());

                } else if (transactionListResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
                else {
                    transactionListRequest.setPageNumber(requestPageNumber);
                    requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                    new HamPayDialog(activity).showFailUserTransactionDialog(requestUserTransaction, transactionListRequest,
                            transactionListResponseMessage.getService().getResultStatus().getCode(),
                            transactionListResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Transaction List")
                            .setAction("Fetch")
                            .setLabel("Fail(Server)")
                            .build());
                }
            } else {
                transactionListRequest.setPageNumber(requestPageNumber);
                requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                new HamPayDialog(activity).showFailUserTransactionDialog(requestUserTransaction, transactionListRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_user_transation));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Transaction List")
                        .setAction("Fetch")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }


        @Override
        public void onTaskPreRun() {
        }
    }


    DobList dobList;

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
                        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                        editor.commit();
                        transactionListRequest.setPageNumber(requestPageNumber);
                        requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                        requestUserTransaction.execute(transactionListRequest);

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

}
