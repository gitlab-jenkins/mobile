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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.CancelUserPaymentRequest;
import xyz.homapay.hampay.common.core.model.response.CancelUserPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCancelPayment;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.circleimageview.CircleImageView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.DateUtil;
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
    RequestCancelPayment requestCancelPayment;
    CancelUserPaymentRequest cancelUserPaymentRequest;
    Activity activity;
    private String authToken;
    private Date currentDate;
    private PersianEnglishDigit persianEnglishDigit;
    NumberFormat timeFormat;
    private CurrencyFormatter currencyFormatter;
    private DateUtil dateUtil;

    public PendingPaymentAdapter(Context context, List<PaymentInfoDTO> paymentInfoDTOs, String authToken)
    {
        currentDate = new Date();
        this.context = context;
        this.paymentInfoDTOs = paymentInfoDTOs;
        activity = (Activity) context;
        hamPayDialog = new HamPayDialog(activity);
        this.authToken = authToken;
        persianEnglishDigit = new PersianEnglishDigit();
        timeFormat = new DecimalFormat("00");
        currencyFormatter = new CurrencyFormatter();
        dateUtil = new DateUtil();
    }

    public int getCount() {
        return paymentInfoDTOs.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    private ViewHolder viewHolder;


    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pending_payment_row, null);

            viewHolder.callerName = (FacedTextView)convertView.findViewById(R.id.callerName);
            viewHolder.callerPhoneNo = (FacedTextView)convertView.findViewById(R.id.callerPhoneNo);
            viewHolder.user_image = (CircleImageView)convertView.findViewById(R.id.user_image);
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

        final PaymentInfoDTO paymentInfoDTO = paymentInfoDTOs.get(position);

        viewHolder.callerName.setText(paymentInfoDTO.getCallerName());
        viewHolder.callerPhoneNo.setText(persianEnglishDigit.E2P(paymentInfoDTO.getCallerPhoneNumber()));
        viewHolder.date_time.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(paymentInfoDTO.getCreatedBy())));
        viewHolder.price_pay.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfoDTO.getAmount()) + " ریال"));
        viewHolder.message.setText(paymentInfoDTO.getMessage());

        viewHolder.expire_pay.setText(dateUtil.remainingTime(paymentInfoDTO.getExpirationDate(), currentDate));

        if (paymentInfoDTO.getImageId() != null) {
            String userImageUrl = "/users/" + authToken + "/" + paymentInfoDTO.getImageId();

            new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(viewHolder.user_image)).execute(userImageUrl);
        }

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
                        requestCancelPayment = new RequestCancelPayment(activity, new RequestCancelPaymentTaskCompleteListener(position));
                        cancelUserPaymentRequest = new CancelUserPaymentRequest();
                        cancelUserPaymentRequest.setProductCode(paymentInfoDTO.getProductCode());
                        requestCancelPayment.execute(cancelUserPaymentRequest);
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

    @Override
    public void notifyDataSetChanged() {
        currentDate = new Date();
        super.notifyDataSetChanged();
    }


    private class ViewHolder{

        ViewHolder(){ }

        FacedTextView callerName;
        FacedTextView callerPhoneNo;
        CircleImageView user_image;
        FacedTextView date_time;
        FacedTextView price_pay;
        FacedTextView expire_pay;
        FacedTextView delete;
        FacedTextView message;
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
                if (cancelUserPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
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
