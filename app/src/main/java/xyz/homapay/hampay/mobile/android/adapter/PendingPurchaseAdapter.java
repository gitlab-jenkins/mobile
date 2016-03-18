package xyz.homapay.hampay.mobile.android.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.CancelPurchasePaymentRequest;
import xyz.homapay.hampay.common.core.model.response.CancelPurchasePaymentResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCancelPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;


/**
 * Created by amir on 6/10/15.
 */
public class PendingPurchaseAdapter extends BaseAdapter  {

    private Context context;

    List<PurchaseInfoDTO> purchaseInfoDTOs;
    Dialog dialog;
    HamPayDialog hamPayDialog;
    RequestCancelPurchase requestCancelPurchase;
    CancelPurchasePaymentRequest cancelPurchasePaymentRequest;
    Activity activity;
    private String authToken;

    public PendingPurchaseAdapter(Context c, List<PurchaseInfoDTO> purchaseInfoDTOs, String authToken)
    {
        // TODO Auto-generated method stub
        context = c;
        this.purchaseInfoDTOs = purchaseInfoDTOs;
        activity = (Activity) context;
        hamPayDialog = new HamPayDialog(activity);
        this.authToken = authToken;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return purchaseInfoDTOs.size();
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
            convertView = inflater.inflate(R.layout.pending_purchase_row, null);


            viewHolder.business_name = (FacedTextView)convertView.findViewById(R.id.business_name);
            viewHolder.business_image = (ImageView)convertView.findViewById(R.id.business_image);
            viewHolder.price_pay = (FacedTextView)convertView.findViewById(R.id.price_pay);
            viewHolder.createDateTime = (FacedTextView)convertView.findViewById(R.id.createDateTime);
            viewHolder.expire_pay = (FacedTextView)convertView.findViewById(R.id.expire_pay);
            viewHolder.delete = (FacedTextView)convertView.findViewById(R.id.delete);


            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final PurchaseInfoDTO purchaseInfoDTO = purchaseInfoDTOs.get(position);

        viewHolder.business_name.setText(purchaseInfoDTO.getMerchantName());

        if (purchaseInfoDTO.getMerchantImageId() != null) {
            new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(viewHolder.business_image)).execute("/logo/" + authToken + "/" + purchaseInfoDTO.getMerchantImageId());
        }else {
            viewHolder.business_image.setBackgroundColor(context.getResources().getColor(R.color.user_change_status));
        }

        viewHolder.createDateTime.setText(new PersianEnglishDigit().E2P(new JalaliConvert().GregorianToPersian(purchaseInfoDTO.getCreatedBy())));
        viewHolder.expire_pay.setText(new PersianEnglishDigit().E2P(new JalaliConvert().GregorianToPersian(purchaseInfoDTO.getExpirationDate())));
        viewHolder.price_pay.setText(new PersianEnglishDigit().E2P(purchaseInfoDTO.getAmount().toString()) + " ریال");

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Rect displayRectangle = new Rect();

                Window window = activity.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                View view = activity.getLayoutInflater().inflate(R.layout.dialog_delete_pending_payment, null);
                FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
                FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);

                confirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        requestCancelPurchase = new RequestCancelPurchase(activity, new RequestCancelPurchasePaymentTaskCompleteListener(position));
                        cancelPurchasePaymentRequest = new CancelPurchasePaymentRequest();
                        cancelPurchasePaymentRequest.setProductCode(purchaseInfoDTO.getProductCode());
                        requestCancelPurchase.execute(cancelPurchasePaymentRequest);
                    }
                });

                dis_confirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
                dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(view);
                dialog.setTitle(null);
                dialog.setCanceledOnTouchOutside(true);

                dialog.show();
            }
        });

        return convertView;

    }


    private class ViewHolder{

        ViewHolder(){ }

        FacedTextView business_name;
        ImageView business_image;
        FacedTextView date_time;
        FacedTextView price_pay;
        FacedTextView createDateTime;
        FacedTextView expire_pay;
        FacedTextView delete;

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
                    purchaseInfoDTOs.remove(position);
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
