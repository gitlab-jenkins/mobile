package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.CancelPurchasePaymentRequest;
import xyz.homapay.hampay.common.core.model.request.PendingPaymentListRequest;
import xyz.homapay.hampay.common.core.model.request.PendingPurchaseListRequest;
import xyz.homapay.hampay.common.core.model.response.CancelPurchasePaymentResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPaymentListResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPurchaseListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.PendingPaymentAdapter;
import xyz.homapay.hampay.mobile.android.adapter.PendingPurchaseAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCancelPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPendingPayment;
import xyz.homapay.hampay.mobile.android.async.RequestPendingPurchase;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class PendingPurchasePaymentActivity extends AppCompatActivity implements View.OnClickListener {


    private Context context;
    private Activity activity;

    private RelativeLayout purchase_rl;
    private FacedTextView purchase_title;
    private View purchase_sep;
    private RelativeLayout payment_rl;
    private FacedTextView payment_title;
    private View payment_sep;
    private int selectedType = 1;


    CoordinatorLayout coordinatorLayout;

    RequestPendingPurchase requestPendingPurchase;
    PendingPurchaseListRequest pendingPurchaseListRequest;

    RequestPendingPayment requestPendingPayment;
    PendingPaymentListRequest pendingPaymentListRequest;

    PendingPurchaseAdapter pendingPurchaseAdapter;
    PendingPaymentAdapter pendingCreditRequestAdapter;

    ListView pendingListView;

    HamPayDialog hamPayDialog;

    PendingPurchaseListResponse pendingPurchaseListResponse;

    List<PaymentInfoDTO> paymentInfoDTOs;
    PspInfoDTO pspInfoDTOs;


    RequestCancelPurchase requestCancelPurchase;
    CancelPurchasePaymentRequest cancelPurchasePaymentRequest;

    ButtonRectangle request_business_button;
    ButtonRectangle request_individual_button;

    private Dialog dialog;


    public void backActionBar(View view){
        finish();
    }

    public void menu(View v){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pending, null);

        final FacedTextView pending_purchase = (FacedTextView) view.findViewById(R.id.pending_purchase);
        final FacedTextView pending_payment = (FacedTextView) view.findViewById(R.id.pending_payment);

        pending_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestPendingPurchase = new RequestPendingPurchase(activity, new RequestPendingPurchaseTaskCompleteListener());
                pendingPurchaseListRequest = new PendingPurchaseListRequest();
                requestPendingPurchase.execute(pendingPurchaseListRequest);
            }
        });

        pending_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestPendingPayment = new RequestPendingPayment(activity, new RequestPendingPaymentTaskCompleteListener());
                pendingPaymentListRequest = new PendingPaymentListRequest();
                requestPendingPayment.execute(pendingPaymentListRequest);
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
        setContentView(R.layout.activity_pending_purchase_payment);
        context = this;
        activity = PendingPurchasePaymentActivity.this;

        purchase_rl = (RelativeLayout)findViewById(R.id.purchase_rl);
        purchase_rl.setOnClickListener(this);
        purchase_title = (FacedTextView)findViewById(R.id.purchase_title);
        purchase_sep = (View)findViewById(R.id.purchase_sep);
        payment_rl = (RelativeLayout)findViewById(R.id.payment_rl);
        payment_rl.setOnClickListener(this);
        payment_title = (FacedTextView)findViewById(R.id.payment_title);
        payment_sep = (View)findViewById(R.id.payment_sep);

        hamPayDialog = new HamPayDialog(activity);

        pendingListView = (ListView)findViewById(R.id.pendingListView);

        requestPendingPurchase = new RequestPendingPurchase(activity, new RequestPendingPurchaseTaskCompleteListener());
        pendingPurchaseListRequest = new PendingPurchaseListRequest();

        requestPendingPayment = new RequestPendingPayment(activity, new RequestPendingPaymentTaskCompleteListener());
        pendingPaymentListRequest = new PendingPaymentListRequest();

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id
                .coordinatorLayout);

        requestPendingPurchase.execute(pendingPurchaseListRequest);

        pendingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                if (pendingPurchaseListResponse != null) {
                    intent.setClass(activity, RequestBusinessPayDetailActivity.class);
                    intent.putExtra(Constants.PENDING_PAYMENT_REQUEST_LIST, pendingPurchaseListResponse.getPendingList().get(position));
                    intent.putExtra(Constants.PSP_INFO, pendingPurchaseListResponse.getPspInfo());
                    startActivity(intent);
                } else if (paymentInfoDTOs != null) {
                    intent.setClass(activity, InvoicePaymentPendingActivity.class);
                    intent.putExtra(Constants.PAYMENT_INFO, paymentInfoDTOs.get(position));
                    intent.putExtra(Constants.PSP_INFO, pspInfoDTOs);
                    startActivity(intent);
                }
            }
        });

        pendingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "", Snackbar.LENGTH_LONG)
                        .setAction("از پرداخت منصرف شدم", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestCancelPurchase = new RequestCancelPurchase(activity, new RequestCancelPurchasePaymentTaskCompleteListener(position));
                                cancelPurchasePaymentRequest = new CancelPurchasePaymentRequest();
                                cancelPurchasePaymentRequest.setProductCode(pendingPurchaseListResponse.getPendingList().get(position).getProductCode());
                                requestCancelPurchase.execute(cancelPurchasePaymentRequest);
                            }
                        });

                snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);

                snackbar.show();

                return true;
            }
        });

    }

    public class RequestPendingPurchaseTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<PendingPurchaseListResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<PendingPurchaseListResponse> pendingPurchaseListResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pendingPurchaseListResponseMessage != null) {
                if (pendingPurchaseListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    pendingPurchaseListResponse = pendingPurchaseListResponseMessage.getService();
                    pendingPurchaseAdapter = new PendingPurchaseAdapter(activity, pendingPurchaseListResponseMessage.getService().getPendingList());
                    pendingListView.setAdapter(pendingPurchaseAdapter);
                    paymentInfoDTOs = null;
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog("");
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
                    pspInfoDTOs = pendingPaymentListResponseMessage.getService().getPspInfo();
                    pendingCreditRequestAdapter = new PendingPaymentAdapter(activity, paymentInfoDTOs);
                    pendingListView.setAdapter(pendingCreditRequestAdapter);
                    pendingPurchaseListResponse = null;
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog("");
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
                if (cancelPurchasePaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    cancelPurchasePaymentResponseMessage.getService().getRequestUUID();
                    pendingPurchaseListResponse.getPendingList().remove(position);
                    pendingPurchaseAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.purchase_rl:
                selectedType = 1;
                purchase_title.setTextColor(getResources().getColor(R.color.user_change_status));
                purchase_sep.setBackgroundColor(getResources().getColor(R.color.user_change_status));
                payment_title.setTextColor(getResources().getColor(R.color.normal_text));
                payment_sep.setBackgroundColor(getResources().getColor(R.color.normal_text));

                requestPendingPurchase = new RequestPendingPurchase(activity, new RequestPendingPurchaseTaskCompleteListener());
                pendingPurchaseListRequest = new PendingPurchaseListRequest();
                requestPendingPurchase.execute(pendingPurchaseListRequest);

                break;

            case R.id.payment_rl:
                selectedType = 2;
                payment_title.setTextColor(getResources().getColor(R.color.user_change_status));
                payment_sep.setBackgroundColor(getResources().getColor(R.color.user_change_status));
                purchase_title.setTextColor(getResources().getColor(R.color.normal_text));
                purchase_sep.setBackgroundColor(getResources().getColor(R.color.normal_text));

                requestPendingPayment = new RequestPendingPayment(activity, new RequestPendingPaymentTaskCompleteListener());
                pendingPaymentListRequest = new PendingPaymentListRequest();
                requestPendingPayment.execute(pendingPaymentListRequest);
                break;
        }
    }
}
