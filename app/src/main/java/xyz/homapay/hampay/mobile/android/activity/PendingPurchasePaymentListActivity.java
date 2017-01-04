package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.enums.FundType;
import xyz.homapay.hampay.common.core.model.request.CancelPurchasePaymentRequest;
import xyz.homapay.hampay.common.core.model.request.CancelUserPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.PendingCountRequest;
import xyz.homapay.hampay.common.core.model.request.PendingFundListRequest;
import xyz.homapay.hampay.common.core.model.response.CancelPurchasePaymentResponse;
import xyz.homapay.hampay.common.core.model.response.CancelUserPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.PendingFundListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.FundDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.PendingFundListAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCancelPayment;
import xyz.homapay.hampay.mobile.android.async.RequestCancelPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPendingCount;
import xyz.homapay.hampay.mobile.android.async.RequestPendingFundList;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.cancelPending.ActionPending;
import xyz.homapay.hampay.mobile.android.dialog.cancelPending.CancelPendingDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PendingPurchasePaymentListActivity extends AppCompatActivity implements View.OnClickListener, CancelPendingDialog.CancelPendingDialogListener {

    private Activity activity;

    private FacedTextView nullPendingText;

    RequestPendingFundList requestPendingFundList;
    PendingFundListRequest pendingFundListRequest;

    PendingFundListAdapter pendingFundListAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private ListView pendingListView;
    HamPayDialog hamPayDialog;
    private List<FundDTO> fundDTOList;
    private int pos = -1;
    private FundType fundType = FundType.ALL;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private int itemPosition;
    private String authToken;
    RequestCancelPurchase requestCancelPurchase;
    CancelPurchasePaymentRequest cancelPurchasePaymentRequest;
    RequestCancelPayment requestCancelPayment;
    CancelUserPaymentRequest cancelUserPaymentRequest;
    RelativeLayout full_pending;
    RelativeLayout invoice_pending;
    RelativeLayout purchase_pending;
    ImageView full_triangle;
    ImageView business_triangle;
    ImageView invoice_triangle;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    private PersianEnglishDigit persianEnglishDigit;
    private Context context;
    private IntentFilter intentFilter;
    private BroadcastReceiver mIntentReceiver;

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000);
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {

            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if (fundDTOList != null && pendingFundListAdapter != null) {
                            pendingFundListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        };
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        PugNotification.with(context).cancel(Constants.PAYMENT_NOTIFICATION_IDENTIFIER);
        HamPayApplication.setAppSate(AppState.Resumed);
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }

        intentFilter = new IntentFilter("notification.intent.MAIN");
        mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra("get_update", false)){
                    requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
                    pendingFundListRequest = new PendingFundListRequest();
                    pendingFundListRequest.setType(fundType);
                    requestPendingFundList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pendingFundListRequest);
                }
            }
        };
        registerReceiver(mIntentReceiver, intentFilter);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        PugNotification.with(context).cancel(Constants.PAYMENT_NOTIFICATION_IDENTIFIER);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_purchase_payment_list);

        activity = PendingPurchasePaymentListActivity.this;
        context = this;
        PugNotification.with(context).cancel(Constants.PAYMENT_NOTIFICATION_IDENTIFIER);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        Intent intent = getIntent();

        persianEnglishDigit = new PersianEnglishDigit();

        nullPendingText = (FacedTextView)findViewById(R.id.nullPendingText);

        hamPayDialog = new HamPayDialog(activity);

        pullToRefresh = (SwipeRefreshLayout)findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
                pendingFundListRequest = new PendingFundListRequest();
                pendingFundListRequest.setType(fundType);
                requestPendingFundList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pendingFundListRequest);
            }
        });
        pendingListView = (ListView)findViewById(R.id.pendingListView);

        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
        pendingFundListRequest = new PendingFundListRequest();
        pendingFundListRequest.setType(fundType);
        requestPendingFundList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pendingFundListRequest);

        full_pending = (RelativeLayout)findViewById(R.id.full_pending);
        full_pending.setOnClickListener(this);
        invoice_pending = (RelativeLayout)findViewById(R.id.invoice_pending);
        invoice_pending.setOnClickListener(this);
        purchase_pending = (RelativeLayout)findViewById(R.id.purchase_pending);
        purchase_pending.setOnClickListener(this);
        full_triangle = (ImageView)findViewById(R.id.full_triangle);
        business_triangle = (ImageView)findViewById(R.id.business_triangle);
        invoice_triangle = (ImageView)findViewById(R.id.invoice_triangle);

        pendingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                if (fundDTOList.get(position).getPaymentType() == FundDTO.PaymentType.PURCHASE) {
                    intent.setClass(activity, RequestBusinessPayDetailActivity.class);
                    intent.putExtra(Constants.PROVIDER_ID, fundDTOList.get(position).getProviderId());
                    itemPosition = position;
                    startActivityForResult(intent, 45);
                } else if (fundDTOList.get(position).getPaymentType() == FundDTO.PaymentType.PAYMENT) {
                    intent.setClass(activity, InvoicePendingConfirmationActivity.class);
                    intent.putExtra(Constants.PROVIDER_ID, fundDTOList.get(position).getProviderId());
                    itemPosition = position;
                    startActivityForResult(intent, 46);
                }else if (fundDTOList.get(position).getPaymentType() == FundDTO.PaymentType.BILL_UTILITY) {
                    intent.setClass(activity, ServiceBillsDetailActivity.class);
                    intent.putExtra(Constants.PROVIDER_ID, fundDTOList.get(position).getProviderId());
                    itemPosition = position;
                    startActivityForResult(intent, 47);
                }
            }
        });

        pendingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                pos = position;
                String productCode = "";
                if (fundDTOList != null){
                    productCode = fundDTOList.get(position).getCode();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                CancelPendingDialog cancelPendingDialog = new CancelPendingDialog();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PENDING_CODE, persianEnglishDigit.E2P(productCode));
                cancelPendingDialog.setArguments(bundle);
                cancelPendingDialog.show(fragmentManager, "fragment_edit_name");

                return true;
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mIntentReceiver);
    }

    @Override
    protected void onStop() {
        stoptimertask();
        super.onStop();
    }

    @Override
    protected void onStart() {
        startTimer();
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 46) {
            if(resultCode == Activity.RESULT_OK){
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, -1);
                if (result == 0){
                    fundDTOList.remove(itemPosition);
                    pendingFundListAdapter.notifyDataSetChanged();
                    if (fundDTOList.size() == 0) {
                        nullPendingText.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }

        if (requestCode == 45) {
            if(resultCode == Activity.RESULT_OK){
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, -1);
                if (result == 0){
                    fundDTOList.remove(itemPosition);
                    pendingFundListAdapter.notifyDataSetChanged();
                    if (fundDTOList.size() == 0){
                        nullPendingText.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.full_pending:
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                fundType = FundType.ALL;
                requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
                pendingFundListRequest = new PendingFundListRequest();
                pendingFundListRequest.setType(fundType);
                requestPendingFundList.execute(pendingFundListRequest);
                changeTab(1);
                break;

            case R.id.invoice_pending:
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                fundType = FundType.INDIVIDUAL;
                requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
                pendingFundListRequest = new PendingFundListRequest();
                pendingFundListRequest.setType(fundType);
                requestPendingFundList.execute(pendingFundListRequest);
                changeTab(2);
                break;

            case R.id.purchase_pending:
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                fundType = FundType.BUSINESS_AND_PURCHASE;
                requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
                pendingFundListRequest = new PendingFundListRequest();
                pendingFundListRequest.setType(fundType);
                requestPendingFundList.execute(pendingFundListRequest);
                changeTab(3);
                break;
        }

    }

    private void changeTab(int index){
        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        switch (index){
            case 1:
                full_pending.setBackgroundColor(getResources().getColor(R.color.app_origin));
                purchase_pending.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                invoice_pending.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                full_triangle.setVisibility(View.VISIBLE);
                business_triangle.setVisibility(View.GONE);
                invoice_triangle.setVisibility(View.GONE);
                break;

            case 2:
                full_pending.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                purchase_pending.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                invoice_pending.setBackgroundColor(getResources().getColor(R.color.app_origin));
                full_triangle.setVisibility(View.GONE);
                business_triangle.setVisibility(View.GONE);
                invoice_triangle.setVisibility(View.VISIBLE);
                break;

            case 3:
                full_pending.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                purchase_pending.setBackgroundColor(getResources().getColor(R.color.app_origin));
                invoice_pending.setBackgroundColor(getResources().getColor(R.color.transaction_unselected_tab));
                full_triangle.setVisibility(View.GONE);
                business_triangle.setVisibility(View.VISIBLE);
                invoice_triangle.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onFinishEditDialog(ActionPending actionPending) {
        switch (actionPending){
            case CANCEL:
                break;
            case REMOVE:
                if (fundDTOList != null){
                    if (fundDTOList.get(pos).getPaymentType() == FundDTO.PaymentType.PURCHASE){
                        requestCancelPurchase = new RequestCancelPurchase(activity, new RequestCancelPurchasePaymentTaskCompleteListener(pos));
                        cancelPurchasePaymentRequest = new CancelPurchasePaymentRequest();
                        cancelPurchasePaymentRequest.setProductCode(fundDTOList.get(pos).getProductCode());
                        requestCancelPurchase.execute(cancelPurchasePaymentRequest);
                    }else if (fundDTOList.get(pos).getPaymentType() == FundDTO.PaymentType.PAYMENT){
                        requestCancelPayment = new RequestCancelPayment(activity, new RequestCancelPaymentTaskCompleteListener(pos));
                        cancelUserPaymentRequest = new CancelUserPaymentRequest();
                        cancelUserPaymentRequest.setProductCode(fundDTOList.get(pos).getCode());
                        requestCancelPayment.execute(cancelUserPaymentRequest);
                    }
                }
                break;
        }
    }

    public class RequestPendingFundTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<PendingFundListResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<PendingFundListResponse> pendingFundListResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            pullToRefresh.setRefreshing(false);
            PugNotification.with(context).cancel(Constants.PAYMENT_NOTIFICATION_IDENTIFIER);

            if (pendingFundListResponseMessage != null) {
                if (pendingFundListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    fundDTOList = new ArrayList<>();
                    for (FundDTO fund: pendingFundListResponseMessage.getService().getFundDTOList()) {
                        if (fund.getExpirationDate().getTime() > System.currentTimeMillis()){
                            fundDTOList.add(fund);
                        }
                    }
                    if (fundDTOList.size() == 0){
                        nullPendingText.setVisibility(View.VISIBLE);
                        pendingListView.setAdapter(null);
                    }else {
                        pendingFundListAdapter = new PendingFundListAdapter(activity, fundDTOList, authToken);
                        pendingListView.setAdapter(pendingFundListAdapter);
                        nullPendingText.setVisibility(View.GONE);
                    }

                }
            }
        }

        @Override
        public void onTaskPreRun() {
        }
    }

    public class RequestCancelPurchasePaymentTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<CancelPurchasePaymentResponse>> {

        int position;

        RequestCancelPurchasePaymentTaskCompleteListener(int position){
            this.position = position;
        }


        @Override
        public void onTaskComplete(ResponseMessage<CancelPurchasePaymentResponse> cancelPurchasePaymentResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (cancelPurchasePaymentResponseMessage != null) {
                if (cancelPurchasePaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS
                        || cancelPurchasePaymentResponseMessage.getService().getResultStatus() == ResultStatus.PURCHASE_NOT_ELIGIBLE_TO_CANCEL) {
                    cancelPurchasePaymentResponseMessage.getService().getRequestUUID();
                    if (fundDTOList != null){
                        fundDTOList.remove(pos);
                        pendingFundListAdapter.notifyDataSetChanged();
                        PugNotification.with(context).cancel(Constants.INVOICE_NOTIFICATION_IDENTIFIER);
                        if (fundDTOList.size() == 0){
                            nullPendingText.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    public class RequestCancelPaymentTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<CancelUserPaymentResponse>> {

        int position;

        RequestCancelPaymentTaskCompleteListener(int position){
            this.position = position;
        }


        @Override
        public void onTaskComplete(ResponseMessage<CancelUserPaymentResponse> cancelUserPaymentResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (cancelUserPaymentResponseMessage != null) {
                if (cancelUserPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS
                        || cancelUserPaymentResponseMessage.getService().getResultStatus() == ResultStatus.PURCHASE_NOT_ELIGIBLE_TO_CANCEL) {
                    if (fundDTOList != null){
                        fundDTOList.remove(pos);
                        pendingFundListAdapter.notifyDataSetChanged();
                        PugNotification.with(context).cancel(Constants.INVOICE_NOTIFICATION_IDENTIFIER);
                        if (fundDTOList.size() == 0){
                            nullPendingText.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }
}
