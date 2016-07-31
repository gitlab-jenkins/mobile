package xyz.homapay.hampay.mobile.android.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.DateUtil;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;


/**
 * Created by amir on 6/10/15.
 */
public class PendingPaymentAdapter extends BaseAdapter  {

    private Context context;
    List<PaymentInfoDTO> paymentInfoDTOs;
    HamPayDialog hamPayDialog;
    Activity activity;
    private String authToken;
    private Date currentDate;
    private PersianEnglishDigit persianEnglishDigit;
    NumberFormat timeFormat;
    private CurrencyFormatter currencyFormatter;
    private DateUtil dateUtil;
    private ImageManager imageManager;

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
        imageManager = new ImageManager(activity, 200000, false);
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
            viewHolder.user_image = (ImageView)convertView.findViewById(R.id.user_image);
            viewHolder.price_pay = (FacedTextView)convertView.findViewById(R.id.price_pay);
            viewHolder.expire_pay = (FacedTextView)convertView.findViewById(R.id.expire_pay);
            viewHolder.paymentCode = (FacedTextView)convertView.findViewById(R.id.paymentCode);

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final PaymentInfoDTO paymentInfoDTO = paymentInfoDTOs.get(position);

        viewHolder.callerName.setText(paymentInfoDTO.getCallerName());
        viewHolder.price_pay.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfoDTO.getAmount())));
        viewHolder.paymentCode.setText(persianEnglishDigit.E2P("کد فاکتور " + paymentInfoDTO.getProductCode()));
        viewHolder.expire_pay.setText(dateUtil.remainingTime(paymentInfoDTO.getExpirationDate(), currentDate));

        if (paymentInfoDTO.getImageId() != null) {
//            String userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + paymentInfoDTO.getImageId();
            viewHolder.user_image.setTag(paymentInfoDTO.getImageId());
            imageManager.displayImage(paymentInfoDTO.getImageId(), viewHolder.user_image, R.drawable.user_placeholder);
        }else {
            viewHolder.user_image.setImageResource(R.drawable.user_placeholder);
        }
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
        ImageView user_image;
        FacedTextView price_pay;
        FacedTextView expire_pay;
        FacedTextView paymentCode;
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
                    paymentInfoDTOs.remove(position);
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog("");
        }
    }

}
