package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

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
import xyz.homapay.hampay.mobile.android.util.Constants;

public class TransactionsListActivity extends AppCompatActivity {

    ListView transationListView;
    UserTransactionAdapter userTransactionAdapter;
    private boolean FINISHED_SCROLLING = false;

    HamPayDialog hamPayDialog;

    FacedTextView no_transaction;
    private boolean onLoadMore = false;

    private List<TransactionDTO> transactionDTOs;

    RequestUserTransaction requestUserTransaction;
    TransactionListRequest transactionListRequest;
    int requestPageNumber = 0;
    TransactionListRequest.SortFactor sortFactor = TransactionListRequest.SortFactor.DEFAULT;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Tracker hamPayGaTracker;

    private Context context;
    private Activity activity;


    private Dialog dialog;

    public void sortView(View v) {

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_transaction_sort, null);

        final FacedTextView sort_default = (FacedTextView) view.findViewById(R.id.sort_default);
        final FacedTextView sort_individual = (FacedTextView) view.findViewById(R.id.sort_individual);
        final FacedTextView sort_business = (FacedTextView) view.findViewById(R.id.sort_business);
        final FacedTextView sort_success = (FacedTextView) view.findViewById(R.id.sort_success);
        final FacedTextView sort_failure = (FacedTextView) view.findViewById(R.id.sort_failure);
        final FacedTextView sort_paid = (FacedTextView) view.findViewById(R.id.sort_paid);
        final FacedTextView sort_received = (FacedTextView) view.findViewById(R.id.sort_received);


        sort_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTransactionAdapter.clear();
                transactionDTOs.clear();
                requestPageNumber = 0;
                sortFactor = TransactionListRequest.SortFactor.DEFAULT;
                transactionListRequest = new TransactionListRequest();
                transactionListRequest.setPageNumber(requestPageNumber);
                transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                transactionListRequest.setSortFactor(sortFactor);
                requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                requestUserTransaction.execute(transactionListRequest);
                dialog.dismiss();
            }
        });

        sort_individual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTransactionAdapter.clear();
                transactionDTOs.clear();
                requestPageNumber = 0;
                sortFactor = TransactionListRequest.SortFactor.INDIVIDUAL;
                transactionListRequest = new TransactionListRequest();
                transactionListRequest.setPageNumber(requestPageNumber);
                transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                transactionListRequest.setSortFactor(sortFactor);
                requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                requestUserTransaction.execute(transactionListRequest);
                dialog.dismiss();
            }
        });

        sort_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTransactionAdapter.clear();
                transactionDTOs.clear();
                requestPageNumber = 0;
                sortFactor = TransactionListRequest.SortFactor.BUSINESS;
                transactionListRequest = new TransactionListRequest();
                transactionListRequest.setPageNumber(requestPageNumber);
                transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                transactionListRequest.setSortFactor(sortFactor);
                requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                requestUserTransaction.execute(transactionListRequest);
                dialog.dismiss();
            }
        });

        sort_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTransactionAdapter.clear();
                transactionDTOs.clear();
                requestPageNumber = 0;
                sortFactor = TransactionListRequest.SortFactor.SUCCESS;
                transactionListRequest = new TransactionListRequest();
                transactionListRequest.setPageNumber(requestPageNumber);
                transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                transactionListRequest.setSortFactor(sortFactor);
                requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                requestUserTransaction.execute(transactionListRequest);
                dialog.dismiss();
            }
        });

        sort_failure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTransactionAdapter.clear();
                transactionDTOs.clear();
                requestPageNumber = 0;
                sortFactor = TransactionListRequest.SortFactor.FAILURE;
                transactionListRequest = new TransactionListRequest();
                transactionListRequest.setPageNumber(requestPageNumber);
                transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                transactionListRequest.setSortFactor(sortFactor);
                requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                requestUserTransaction.execute(transactionListRequest);
                dialog.dismiss();
            }
        });

        sort_paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTransactionAdapter.clear();
                transactionDTOs.clear();
                requestPageNumber = 0;
                sortFactor = TransactionListRequest.SortFactor.PAID;
                transactionListRequest = new TransactionListRequest();
                transactionListRequest.setPageNumber(requestPageNumber);
                transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                transactionListRequest.setSortFactor(sortFactor);
                requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                requestUserTransaction.execute(transactionListRequest);
                dialog.dismiss();
            }
        });

        sort_received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTransactionAdapter.clear();
                transactionDTOs.clear();
                requestPageNumber = 0;
                sortFactor = TransactionListRequest.SortFactor.RECEIVED;
                transactionListRequest = new TransactionListRequest();
                transactionListRequest.setPageNumber(requestPageNumber);
                transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                transactionListRequest.setSortFactor(sortFactor);
                requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
                requestUserTransaction.execute(transactionListRequest);
                dialog.dismiss();
            }
        });

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0);
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = 25;
        layoutParams.y = 20;

        dialog.show();
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

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        transactionDTOs = new ArrayList<TransactionDTO>();
        userTransactionAdapter = new UserTransactionAdapter(context, prefs.getString(Constants.LOGIN_TOKEN_ID, ""));

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);


        no_transaction = (FacedTextView)findViewById(R.id.no_transaction);

        transationListView = (ListView)findViewById(R.id.transationListView);


        transationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(activity, TransactionDetailActivity.class);
                intent.putExtra(Constants.USER_TRANSACTION_DTO, transactionDTOs.get(position));
                startActivity(intent);
            }
        });

        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(activity, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }else {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            requestPageNumber = 0;
            transactionListRequest = new TransactionListRequest();
            transactionListRequest.setPageNumber(requestPageNumber);
            transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
            transactionListRequest.setSortFactor(sortFactor);
            requestUserTransaction = new RequestUserTransaction(activity, new RequestUserTransactionsTaskCompleteListener());
            requestUserTransaction.execute(transactionListRequest);
        }

    }

    public class RequestUserTransactionsTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<TransactionListResponse>> {

        List<TransactionDTO> newTransactionDTOs;

        @Override
        public void onTaskComplete(ResponseMessage<TransactionListResponse> transactionListResponseMessage) {


            hamPayDialog.dismisWaitingDialog();

            if (transactionListResponseMessage != null) {

                if (transactionListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    newTransactionDTOs = transactionListResponseMessage.getService().getTransactions();
                    transactionDTOs.addAll(newTransactionDTOs);

                    if (transactionDTOs.size() == 0){
                        no_transaction.setVisibility(View.VISIBLE);
                        transationListView.setVisibility(View.GONE);
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
                                initDobList(getWindow().getDecorView().getRootView(), transationListView);
                                transationListView.setAdapter(userTransactionAdapter);
                            }
                        }

                    }

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Transaction List")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());

                } else {
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
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
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
            userTransactionAdapter.addItem(transactionDTOs.get(i));
        }
    }

}
