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
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.enums.FundType;
import xyz.homapay.hampay.common.core.model.request.CancelPurchasePaymentRequest;
import xyz.homapay.hampay.common.core.model.request.CancelUserPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.PendingFundListRequest;
import xyz.homapay.hampay.common.core.model.response.CancelPurchasePaymentResponse;
import xyz.homapay.hampay.common.core.model.response.CancelUserPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.PendingFundListResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPaymentListResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPurchaseListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.FundDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.PendingFundListAdapter;
import xyz.homapay.hampay.mobile.android.adapter.PendingPaymentAdapter;
import xyz.homapay.hampay.mobile.android.adapter.PendingPurchaseAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCancelPayment;
import xyz.homapay.hampay.mobile.android.async.RequestCancelPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPendingFundList;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.cancelPending.ActionPending;
import xyz.homapay.hampay.mobile.android.dialog.cancelPending.CancelPendingDialog;
import xyz.homapay.hampay.mobile.android.impl.comparator.PaymentAmountComparator;
import xyz.homapay.hampay.mobile.android.impl.comparator.PaymentDateComparator;
import xyz.homapay.hampay.mobile.android.impl.comparator.PaymentExpireComparator;
import xyz.homapay.hampay.mobile.android.impl.comparator.PurchaseAmountComparator;
import xyz.homapay.hampay.mobile.android.impl.comparator.PurchaseDateComparator;
import xyz.homapay.hampay.mobile.android.impl.comparator.PurchaseExpireComparator;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PendingPurchasePaymentListActivity extends AppCompatActivity implements View.OnClickListener, CancelPendingDialog.CancelPendingDialogListener {

    private Activity activity;

    private FacedTextView nullPendingText;

    private CoordinatorLayout coordinatorLayout;

    RequestPendingFundList requestPendingFundList;
    PendingFundListRequest pendingFundListRequest;

//    RequestPendingPurchase requestPendingPurchase;
//    PendingPurchaseListRequest pendingPurchaseListRequest;

//    RequestPendingPayment requestPendingPayment;
//    PendingPaymentListRequest pendingPaymentListRequest;

    PendingPurchaseAdapter pendingPurchaseAdapter;
    PendingPaymentAdapter pendingPaymentAdapter;
    PendingFundListAdapter pendingFundListAdapter;

    ListView pendingListView;

    HamPayDialog hamPayDialog;

    private List<PurchaseInfoDTO> purchaseInfoDTOs;
    private List<PaymentInfoDTO> paymentInfoDTOs;
    private List<FundDTO> fundDTOList;
    PspInfoDTO pspInfoDTOs;
    private int pos = -1;


    private Dialog dialog;

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

                        if (paymentInfoDTOs != null && pendingPaymentAdapter != null) {
                            pendingPaymentAdapter.notifyDataSetChanged();
                        }else if (purchaseInfoDTOs != null && pendingPurchaseAdapter != null){
                            pendingPurchaseAdapter.notifyDataSetChanged();
                        }else if (fundDTOList != null && pendingFundListAdapter != null){
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



    public void sortView(View v){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pending_sort, null);

        final FacedTextView sort_date = (FacedTextView) view.findViewById(R.id.sort_date);
        final FacedTextView sort_expire = (FacedTextView) view.findViewById(R.id.sort_expire);
        final FacedTextView sort_amount = (FacedTextView) view.findViewById(R.id.sort_amount);

        sort_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (purchaseInfoDTOs != null && pendingPurchaseAdapter != null){
                    Collections.sort(purchaseInfoDTOs, new PurchaseDateComparator());
                    pendingPurchaseAdapter.notifyDataSetChanged();
                }else if (paymentInfoDTOs != null && pendingPaymentAdapter != null){
                    Collections.sort(paymentInfoDTOs, new PaymentDateComparator());
                    pendingPaymentAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });

        sort_expire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (purchaseInfoDTOs != null && pendingPurchaseAdapter != null){
                    Collections.sort(purchaseInfoDTOs, new PurchaseExpireComparator());
                    pendingPurchaseAdapter.notifyDataSetChanged();
                }else if (paymentInfoDTOs != null && pendingPaymentAdapter != null){
                    Collections.sort(paymentInfoDTOs, new PaymentExpireComparator());
                    pendingPaymentAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });

        sort_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (purchaseInfoDTOs != null && pendingPurchaseAdapter != null){
                    Collections.sort(purchaseInfoDTOs, new PurchaseAmountComparator());
                    pendingPurchaseAdapter.notifyDataSetChanged();
                }else if (paymentInfoDTOs != null && pendingPaymentAdapter != null){
                    Collections.sort(paymentInfoDTOs, new PaymentAmountComparator());
                    pendingPaymentAdapter.notifyDataSetChanged();
                }
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

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        pendingListView = (ListView)findViewById(R.id.pendingListView);


        requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
        pendingFundListRequest = new PendingFundListRequest();
        pendingFundListRequest.setType(FundType.ALL);
        requestPendingFundList.execute(pendingFundListRequest);


//        requestPendingPurchase = new RequestPendingPurchase(activity, new RequestPendingPurchaseTaskCompleteListener());
//        pendingPurchaseListRequest = new PendingPurchaseListRequest();

//        requestPendingPayment = new RequestPendingPayment(activity, new RequestPendingPaymentTaskCompleteListener());
//        pendingPaymentListRequest = new PendingPaymentListRequest();

        if (intent.getStringExtra(Constants.CONTACT_NAME) != null){
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
//            requestPendingPayment.execute(pendingPaymentListRequest);
        }else {
//            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
//            editor.commit();
//            requestPendingPurchase.execute(pendingPurchaseListRequest);
        }

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
                }
            }
        });

        pendingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                pos = position;

                String code = "";

                if (paymentInfoDTOs != null){
                    code = paymentInfoDTOs.get(position).getProductCode();
                }else if (purchaseInfoDTOs != null){
                    code = purchaseInfoDTOs.get(position).getProductCode();
                }else if (fundDTOList != null){
                    code = fundDTOList.get(position).getCode();
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                CancelPendingDialog cancelPendingDialog = new CancelPendingDialog();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PENDING_CODE, persianEnglishDigit.E2P(code));
                cancelPendingDialog.setArguments(bundle);
                cancelPendingDialog.show(fragmentManager, "fragment_edit_name");

                return true;
            }
        });

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
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, 0);
                if (result == 1){
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
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, 0);
                if (result == 1){
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
                requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
                pendingFundListRequest = new PendingFundListRequest();
                requestPendingFundList.execute(pendingFundListRequest);
                changeTab(1);
                break;

            case R.id.invoice_pending:
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
                pendingFundListRequest = new PendingFundListRequest();
                pendingFundListRequest.setType(FundType.PAYMENT);
                requestPendingFundList.execute(pendingFundListRequest);
//                requestPendingPayment = new RequestPendingPayment(activity, new RequestPendingPaymentTaskCompleteListener());
//                pendingPaymentListRequest = new PendingPaymentListRequest();
//                requestPendingPayment.execute(pendingPaymentListRequest);
                changeTab(2);
                break;

            case R.id.purchase_pending:
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                requestPendingFundList = new RequestPendingFundList(activity, new RequestPendingFundTaskCompleteListener());
                pendingFundListRequest = new PendingFundListRequest();
                pendingFundListRequest.setType(FundType.PURCHASE);
                requestPendingFundList.execute(pendingFundListRequest);
//                requestPendingPurchase = new RequestPendingPurchase(activity, new RequestPendingPurchaseTaskCompleteListener());
//                pendingPurchaseListRequest = new PendingPurchaseListRequest();
//                requestPendingPurchase.execute(pendingPurchaseListRequest);
                changeTab(3);
                break;
        }

    }

    private void changeTab(int index){
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
                if (purchaseInfoDTOs != null) {
                    requestCancelPurchase = new RequestCancelPurchase(activity, new RequestCancelPurchasePaymentTaskCompleteListener(pos));
                    cancelPurchasePaymentRequest = new CancelPurchasePaymentRequest();
                    cancelPurchasePaymentRequest.setProductCode(purchaseInfoDTOs.get(pos).getProductCode());
                    requestCancelPurchase.execute(cancelPurchasePaymentRequest);
                }else if (paymentInfoDTOs != null){
                    requestCancelPayment = new RequestCancelPayment(activity, new RequestCancelPaymentTaskCompleteListener(pos));
                    cancelUserPaymentRequest = new CancelUserPaymentRequest();
                    cancelUserPaymentRequest.setProductCode(paymentInfoDTOs.get(pos).getProductCode());
                    requestCancelPayment.execute(cancelUserPaymentRequest);
                }else if (fundDTOList != null){
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

            if (pendingFundListResponseMessage != null) {
                if (pendingFundListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    fundDTOList = pendingFundListResponseMessage.getService().getFundDTOList();
                    if (fundDTOList.size() == 0){
                        nullPendingText.setVisibility(View.VISIBLE);
                    }else {
                        pendingFundListAdapter = new PendingFundListAdapter(activity, fundDTOList, authToken);
                        pendingListView.setAdapter(pendingFundListAdapter);
                        purchaseInfoDTOs = null;
                        paymentInfoDTOs = null;
                        nullPendingText.setVisibility(View.GONE);
                    }

                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog("");
        }
    }

    public class RequestPendingPurchaseTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<PendingPurchaseListResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<PendingPurchaseListResponse> pendingPurchaseListResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pendingPurchaseListResponseMessage != null) {
                if (pendingPurchaseListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    purchaseInfoDTOs = pendingPurchaseListResponseMessage.getService().getPendingList();
                    if (purchaseInfoDTOs.size() == 0){
                        nullPendingText.setVisibility(View.VISIBLE);
                    }else {
                        Collections.sort(purchaseInfoDTOs, new PurchaseDateComparator());
                        pendingPurchaseAdapter = new PendingPurchaseAdapter(activity, purchaseInfoDTOs, authToken);
                        pendingListView.setAdapter(pendingPurchaseAdapter);
                        paymentInfoDTOs = null;
                        fundDTOList = null;
                        nullPendingText.setVisibility(View.GONE);
                    }
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog("");
        }
    }

    public class RequestPendingPaymentTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<PendingPaymentListResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<PendingPaymentListResponse> pendingPaymentListResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pendingPaymentListResponseMessage != null) {
                if (pendingPaymentListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    paymentInfoDTOs = pendingPaymentListResponseMessage.getService().getPendingList();
                    if (paymentInfoDTOs.size() == 0){
                        nullPendingText.setVisibility(View.VISIBLE);
                    }else {
                        pspInfoDTOs = pendingPaymentListResponseMessage.getService().getPspInfo();
                        Collections.sort(paymentInfoDTOs, new PaymentDateComparator());
                        pendingPaymentAdapter = new PendingPaymentAdapter(activity, paymentInfoDTOs, authToken);
                        pendingListView.setAdapter(pendingPaymentAdapter);
                        purchaseInfoDTOs = null;
                        fundDTOList = null;
                        nullPendingText.setVisibility(View.GONE);
                    }

                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog("");
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
                    if (purchaseInfoDTOs != null) {
                        purchaseInfoDTOs.remove(position);
                        pendingPurchaseAdapter.notifyDataSetChanged();
                    }else if (fundDTOList != null){
                        fundDTOList.remove(pos);
                        pendingFundListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog("");
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
                    if (paymentInfoDTOs != null) {
                        paymentInfoDTOs.remove(position);
                        pendingPaymentAdapter.notifyDataSetChanged();
                    }else if (fundDTOList != null){
                        fundDTOList.remove(pos);
                        pendingFundListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog("");
        }
    }
}
