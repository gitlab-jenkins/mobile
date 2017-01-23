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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.goncalves.pugnotification.notification.PugNotification;
import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.enums.FundType;
import xyz.homapay.hampay.common.core.model.request.CancelFundRequest;
import xyz.homapay.hampay.common.core.model.request.PendingFundListRequest;
import xyz.homapay.hampay.common.core.model.response.CancelFundResponse;
import xyz.homapay.hampay.common.core.model.response.PendingFundListResponse;
import xyz.homapay.hampay.common.core.model.response.TopUpResponse;
import xyz.homapay.hampay.common.core.model.response.dto.FundDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.PendingFundListAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCancelPayment;
import xyz.homapay.hampay.mobile.android.async.RequestCancelPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPendingFundList;
import xyz.homapay.hampay.mobile.android.common.charge.ChargeType;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSetOperator;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.cancelPending.ActionPending;
import xyz.homapay.hampay.mobile.android.dialog.cancelPending.CancelPendingDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpDetailImpl;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpDetailView;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.TelephonyUtils;

public class PendingPurchasePaymentListActivity extends AppCompatActivity implements View.OnClickListener, CancelPendingDialog.CancelPendingDialogListener {

    final Handler handler = new Handler();
    RequestPendingFundList requestPendingFundList;
    PendingFundListRequest pendingFundListRequest;
    PendingFundListAdapter pendingFundListAdapter;
    HamPayDialog hamPayDialog;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    RequestCancelPurchase requestCancelPurchase;
    CancelFundRequest cancelFundRequest;
    RequestCancelPayment requestCancelPayment;
    @BindView(R.id.full_pending)
    RelativeLayout full_pending;
    @BindView(R.id.invoice_pending)
    RelativeLayout invoice_pending;
    @BindView(R.id.purchase_pending)
    RelativeLayout purchase_pending;
    @BindView(R.id.full_triangle)
    ImageView full_triangle;
    @BindView(R.id.business_triangle)
    ImageView business_triangle;
    @BindView(R.id.invoice_triangle)
    ImageView invoice_triangle;
    Timer timer;
    TimerTask timerTask;
    @BindView(R.id.nullPendingText)
    FacedTextView nullPendingText;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.pendingListView)
    ListView pendingListView;
    private Activity activity;
    private List<FundDTO> fundDTOList;
    private int pos = -1;
    private FundType fundType = FundType.ALL;
    private int itemPosition;
    private String authToken;
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
                handler.post(() -> {
                    if (fundDTOList != null && pendingFundListAdapter != null) {
                        pendingFundListAdapter.notifyDataSetChanged();
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
                if (intent.getBooleanExtra("get_update", false)) {
                    requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
                    pendingFundListRequest = new PendingFundListRequest();
                    pendingFundListRequest.setType(fundType);
                    requestPendingFundList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pendingFundListRequest);
                }
            }
        };
        registerReceiver(mIntentReceiver, intentFilter);
        if (pendingListView != null)
            pendingListView.setEnabled(true);
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


    public void backActionBar(View view) {
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_purchase_payment_list);
        ButterKnife.bind(this);

        activity = PendingPurchasePaymentListActivity.this;
        context = this;
        PugNotification.with(context).cancel(Constants.PAYMENT_NOTIFICATION_IDENTIFIER);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        Intent intent = getIntent();

        persianEnglishDigit = new PersianEnglishDigit();

        hamPayDialog = new HamPayDialog(activity);

        pullToRefresh.setOnRefreshListener(() -> {
            requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
            pendingFundListRequest = new PendingFundListRequest();
            pendingFundListRequest.setType(fundType);
            requestPendingFundList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pendingFundListRequest);
        });

        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
        pendingFundListRequest = new PendingFundListRequest();
        pendingFundListRequest.setType(fundType);
        requestPendingFundList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pendingFundListRequest);

        pendingListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent1 = new Intent();
            pendingListView.setEnabled(false);
            if (fundDTOList.get(position).getPaymentType() == FundDTO.PaymentType.PURCHASE) {
                intent1.setClass(activity, RequestBusinessPayDetailActivity.class);
                intent1.putExtra(Constants.PROVIDER_ID, fundDTOList.get(position).getProviderId());
                itemPosition = position;
                startActivityForResult(intent1, 45);
                pendingListView.setEnabled(true);
            } else if (fundDTOList.get(position).getPaymentType() == FundDTO.PaymentType.PAYMENT) {
                intent1.setClass(activity, InvoicePendingConfirmationActivity.class);
                intent1.putExtra(Constants.PROVIDER_ID, fundDTOList.get(position).getProviderId());
                itemPosition = position;
                startActivityForResult(intent1, 46);
                pendingListView.setEnabled(true);
            } else if (fundDTOList.get(position).getPaymentType() == FundDTO.PaymentType.UTILITY_BILL) {
                intent1.setClass(activity, ServiceBillsDetailActivity.class);
                intent1.putExtra(Constants.PROVIDER_ID, fundDTOList.get(position).getProviderId());
                itemPosition = position;
                startActivityForResult(intent1, 47);
                pendingListView.setEnabled(true);
            } else {
                new TopUpDetailImpl(new ModelLayerImpl(context), new TopUpDetailView() {
                    @Override
                    public void showProgress() {
                        hamPayDialog.showWaitingDialog("");
                    }

                    @Override
                    public void cancelProgress() {
                        hamPayDialog.dismisWaitingDialog();
                    }

                    @Override
                    public void onError() {
                        hamPayDialog.showFailPendingPaymentDialog(getString(R.string.err_general), getString(R.string.err_general_text));
                    }

                    @Override
                    public void onDetailLoaded(boolean status, ResponseMessage<TopUpResponse> data, String message) {
                        if (status) {
                            Intent intent = new Intent(context, ServiceTopUpDetailActivity.class);
                            MessageSetOperator messageSetOperator = new MessageSetOperator(new TelephonyUtils().getNumberOperator(data.getService().getTopUpInfoDTO().getCellNumber()));
                            data.getService().getTopUpInfoDTO().setImageId(messageSetOperator.getOperatorName());
                            intent.putExtra(Constants.TOP_UP_INFO, data.getService().getTopUpInfoDTO());
                            intent.putExtra(Constants.CHARGE_TYPE, ChargeType.DIRECT.ordinal());
                            intent.putExtra(Constants.FUND_DTO, pendingFundListAdapter.getItem(position));
                            startActivityForResult(intent, 48);
                        } else {
                            hamPayDialog.showFailPendingPaymentDialog(data.getService().getResultStatus().getCode(), data.getService().getResultStatus().getDescription());
                        }
                    }
                }).getDetail(fundDTOList.get(position).getProviderId());
            }
        });

        pendingListView.setOnItemLongClickListener((parent, view, position, id) -> {

            pos = position;
            String productCode = "";
            if (fundDTOList != null) {
                productCode = fundDTOList.get(position).getCode();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            CancelPendingDialog cancelPendingDialog = new CancelPendingDialog();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.PENDING_CODE, persianEnglishDigit.E2P(productCode));
            cancelPendingDialog.setArguments(bundle);
            cancelPendingDialog.show(fragmentManager, "fragment_edit_name");

            return true;
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
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, -1);
                if (result == 0) {
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
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, -1);
                if (result == 0) {
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

        if (requestCode == 47) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, -1);
                if (result == 0) {
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

        if (requestCode == 48) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, -1);
                if (result == 0) {
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
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.full_pending:
                AppManager.setMobileTimeout(context);
                editor.commit();
                fundType = FundType.ALL;
                requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
                pendingFundListRequest = new PendingFundListRequest();
                pendingFundListRequest.setType(fundType);
                requestPendingFundList.execute(pendingFundListRequest);
                changeTab(1);
                break;

            case R.id.invoice_pending:
                AppManager.setMobileTimeout(context);
                editor.commit();
                fundType = FundType.INDIVIDUAL;
                requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
                pendingFundListRequest = new PendingFundListRequest();
                pendingFundListRequest.setType(fundType);
                requestPendingFundList.execute(pendingFundListRequest);
                changeTab(2);
                break;

            case R.id.purchase_pending:
                AppManager.setMobileTimeout(context);
                editor.commit();
                fundType = FundType.COMMERCIAL;
                requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
                pendingFundListRequest = new PendingFundListRequest();
                pendingFundListRequest.setType(fundType);
                requestPendingFundList.execute(pendingFundListRequest);
                changeTab(3);
                break;
        }

    }

    private void changeTab(int index) {
        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        switch (index) {
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
        switch (actionPending) {
            case CANCEL:
                break;
            case REMOVE:
                if (fundDTOList != null) {
                    if (fundDTOList.get(pos).getPaymentType() == FundDTO.PaymentType.PURCHASE) {
                        requestCancelPurchase = new RequestCancelPurchase(activity, new RequestCancelPurchasePaymentTaskCompleteListener(pos));
                        cancelFundRequest = new CancelFundRequest();
                        cancelFundRequest.setProviderId(fundDTOList.get(pos).getProviderId());
                        cancelFundRequest.setFundType(FundType.PURCHASE);
                        requestCancelPurchase.execute(cancelFundRequest);
                    } else if (fundDTOList.get(pos).getPaymentType() == FundDTO.PaymentType.PAYMENT) {
                        requestCancelPayment = new RequestCancelPayment(activity, new RequestCancelPaymentTaskCompleteListener(pos));
                        cancelFundRequest = new CancelFundRequest();
                        cancelFundRequest.setFundType(FundType.PAYMENT);
                        cancelFundRequest.setProviderId(fundDTOList.get(pos).getProviderId());
                        requestCancelPayment.execute(cancelFundRequest);
                    } else if (fundDTOList.get(pos).getPaymentType() == FundDTO.PaymentType.UTILITY_BILL) {
                        requestCancelPayment = new RequestCancelPayment(activity, new RequestCancelPaymentTaskCompleteListener(pos));
                        cancelFundRequest = new CancelFundRequest();
                        cancelFundRequest.setFundType(FundType.UTILITY_BILL);
                        cancelFundRequest.setProviderId(fundDTOList.get(pos).getProviderId());
                        requestCancelPayment.execute(cancelFundRequest);
                    } else if (fundDTOList.get(pos).getPaymentType() == FundDTO.PaymentType.TOP_UP) {
                        requestCancelPayment = new RequestCancelPayment(activity, new RequestCancelPaymentTaskCompleteListener(pos));
                        cancelFundRequest = new CancelFundRequest();
                        cancelFundRequest.setFundType(FundType.TOP_UP);
                        cancelFundRequest.setProviderId(fundDTOList.get(pos).getProviderId());
                        requestCancelPayment.execute(cancelFundRequest);
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
                    for (FundDTO fund : pendingFundListResponseMessage.getService().getFundDTOList()) {
                        if (fund.getExpirationDate().getTime() > System.currentTimeMillis()) {
                            fundDTOList.add(fund);
                        }
                    }
                    if (fundDTOList.size() == 0) {
                        nullPendingText.setVisibility(View.VISIBLE);
                        pendingListView.setAdapter(null);
                    } else {
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
            AsyncTaskCompleteListener<ResponseMessage<CancelFundResponse>> {

        int position;

        RequestCancelPurchasePaymentTaskCompleteListener(int position) {
            this.position = position;
        }


        @Override
        public void onTaskComplete(ResponseMessage<CancelFundResponse> cancelPurchasePaymentResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (cancelPurchasePaymentResponseMessage != null) {
                if (cancelPurchasePaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS
                        || cancelPurchasePaymentResponseMessage.getService().getResultStatus() == ResultStatus.FUND_NOT_ELIGIBLE_TO_CANCEL) {
                    cancelPurchasePaymentResponseMessage.getService().getRequestUUID();
                    if (fundDTOList != null) {
                        fundDTOList.remove(pos);
                        pendingFundListAdapter.notifyDataSetChanged();
                        PugNotification.with(context).cancel(Constants.INVOICE_NOTIFICATION_IDENTIFIER);
                        if (fundDTOList.size() == 0) {
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
            AsyncTaskCompleteListener<ResponseMessage<CancelFundResponse>> {

        int position;

        RequestCancelPaymentTaskCompleteListener(int position) {
            this.position = position;
        }


        @Override
        public void onTaskComplete(ResponseMessage<CancelFundResponse> cancelUserPaymentResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (cancelUserPaymentResponseMessage != null) {
                if (cancelUserPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS
                        || cancelUserPaymentResponseMessage.getService().getResultStatus() == ResultStatus.FUND_NOT_ELIGIBLE_TO_CANCEL) {
                    if (fundDTOList != null) {
                        fundDTOList.remove(pos);
                        pendingFundListAdapter.notifyDataSetChanged();
                        PugNotification.with(context).cancel(Constants.INVOICE_NOTIFICATION_IDENTIFIER);
                        if (fundDTOList.size() == 0) {
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
