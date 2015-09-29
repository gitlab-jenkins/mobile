package xyz.homapay.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.TransactionListRequest;
import com.hampay.common.core.model.response.TransactionListResponse;
import com.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.activity.TransactionDetailActivity;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class UserTransactionFragment extends Fragment {



    View rootView;

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

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Tracker hamPayGaTracker;

    public UserTransactionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE).edit();

        transactionDTOs = new ArrayList<TransactionDTO>();
        userTransactionAdapter = new UserTransactionAdapter(getActivity());

        hamPayGaTracker = ((HamPayApplication) getActivity().getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_transaction, container, false);

        hamPayDialog = new HamPayDialog(getActivity());


        no_transaction = (FacedTextView)rootView.findViewById(R.id.no_transaction);

        transationListView = (ListView)rootView.findViewById(R.id.transationListView);


        transationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), TransactionDetailActivity.class);
                intent.putExtra(Constants.USER_TRANSACTION_DTO, transactionDTOs.get(position));
                startActivity(intent);
            }
        });

        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().finish();
            startActivity(intent);
        }else {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            requestPageNumber = 0;
            transactionListRequest = new TransactionListRequest();
            transactionListRequest.setPageNumber(requestPageNumber);
            transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
            requestUserTransaction = new RequestUserTransaction(getActivity(), new RequestUserTransactionsTaskCompleteListener());
            requestUserTransaction.execute(transactionListRequest);
        }

        return rootView;
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                                initDobList(rootView, transationListView);
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
                    requestUserTransaction = new RequestUserTransaction(getActivity(), new RequestUserTransactionsTaskCompleteListener());
                    new HamPayDialog(getActivity()).showFailUserTransactionDialog(requestUserTransaction, transactionListRequest,
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
                requestUserTransaction = new RequestUserTransaction(getActivity(), new RequestUserTransactionsTaskCompleteListener());
                new HamPayDialog(getActivity()).showFailUserTransactionDialog(requestUserTransaction, transactionListRequest,
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
                        requestUserTransaction = new RequestUserTransaction(getActivity(), new RequestUserTransactionsTaskCompleteListener());
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
