package xyz.homapay.hampay.mobile.android.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.CancelPurchasePaymentRequest;
import xyz.homapay.hampay.common.core.model.response.CancelPurchasePaymentResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCancelPurchase;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;


/**
 * Created by amir on 6/10/15.
 */
public class PendingPaymentAdapter extends BaseAdapter  {

    private Context context;

    List<PaymentInfoDTO> paymentInfoDTOs;

    Dialog dialog;

    HamPayDialog hamPayDialog;

    RequestCancelPurchase requestCancelPurchase;
    CancelPurchasePaymentRequest cancelPurchasePaymentRequest;

    Activity activity;

    public PendingPaymentAdapter(Context c, List<PaymentInfoDTO> paymentInfoDTOs)
    {
        // TODO Auto-generated method stub
        context = c;

        this.paymentInfoDTOs = paymentInfoDTOs;

        activity = (Activity) context;

        hamPayDialog = new HamPayDialog(activity);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return paymentInfoDTOs.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private ViewHolder viewHolder;



    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pending_payment_row, null);


            viewHolder.callerPhoneNo = (FacedTextView)convertView.findViewById(R.id.callerPhoneNo);
            viewHolder.callerImage = (ImageView)convertView.findViewById(R.id.callerImage);
            viewHolder.date_time = (FacedTextView)convertView.findViewById(R.id.date_time);
            viewHolder.price_pay = (FacedTextView)convertView.findViewById(R.id.price_pay);
            viewHolder.expire_pay = (FacedTextView)convertView.findViewById(R.id.expire_pay);
            viewHolder.delete = (FacedTextView)convertView.findViewById(R.id.delete);
            viewHolder.message = (FacedTextView)convertView.findViewById(R.id.message);


            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.callerPhoneNo.setText(paymentInfoDTOs.get(position).getCallerPhoneNumber());
//        String LogoUrl = Constants.HTTPS_SERVER_IP + "/merchant-logo/" + purchaseInfoDTOs.get(position).getMerchantLogoName();
//        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(viewHolder.business_logo)).execute(Constants.HTTPS_SERVER_IP + "/merchant-logo/" + purchaseInfoDTOs.get(position).getMerchantLogoName());
        viewHolder.date_time.setText(new PersianEnglishDigit().E2P(new JalaliConvert().GregorianToPersian(paymentInfoDTOs.get(position).getExpirationDate())));
        viewHolder.price_pay.setText(new PersianEnglishDigit().E2P(paymentInfoDTOs.get(position).getAmount().toString()) + " ریال");
        viewHolder.message.setText(paymentInfoDTOs.get(position).getMessage());
        viewHolder.expire_pay.setVisibility(View.GONE);



        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Rect displayRectangle = new Rect();
//
//                Window window = activity.getWindow();
//                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//                View view = activity.getLayoutInflater().inflate(R.layout.dialog_delete_pending_payment, null);
//                FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
//                FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);
//
//                confirmation.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        requestCancelPurchase = new RequestCancelPurchase(activity, new RequestCancelPurchasePaymentTaskCompleteListener(position));
//                        cancelPurchasePaymentRequest = new CancelPurchasePaymentRequest();
//                        cancelPurchasePaymentRequest.setPurchaseCode(purchaseInfoDTOs.get(position).getPurchaseCode());
//                        requestCancelPurchase.execute(cancelPurchasePaymentRequest);
//                    }
//                });
//
//                dis_confirmation.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//
//                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
//                dialog = new Dialog(activity);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.setContentView(view);
//                dialog.setTitle(null);
//                dialog.setCanceledOnTouchOutside(true);
//
//                dialog.show();
            }
        });
//        viewHolder.expire_pay.setText(purchaseInfoDTOs.get(position).get);



        return convertView;

    }


    private class ViewHolder{

        ViewHolder(){ }

        FacedTextView callerPhoneNo;
        ImageView callerImage;
        FacedTextView date_time;
        FacedTextView price_pay;
        FacedTextView expire_pay;
        FacedTextView delete;
        FacedTextView message;
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
                    paymentInfoDTOs.remove(position);
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog("");
        }
    }

}
