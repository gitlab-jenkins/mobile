package xyz.homapay.hampay.mobile.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.response.CancelPurchasePaymentResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.DateUtil;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;


/**
 * Created by amir on 6/10/15.
 */
public class PendingPurchaseAdapter extends BaseAdapter  {

    private Context context;
    List<PurchaseInfoDTO> purchaseInfoDTOs;
    HamPayDialog hamPayDialog;
    Activity activity;
    private String authToken;
    private Date currentDate;
    private PersianEnglishDigit persianEnglishDigit;
    NumberFormat timeFormat;
    private CurrencyFormatter currencyFormatter;
    private DateUtil dateUtil;
    private ImageManager imageManager;

    public PendingPurchaseAdapter(Context context, List<PurchaseInfoDTO> purchaseInfoDTOs, String authToken)
    {
        currentDate = new Date();
        this.context = context;
        this.purchaseInfoDTOs = purchaseInfoDTOs;
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
        return purchaseInfoDTOs.size();
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
            convertView = inflater.inflate(R.layout.pending_purchase_row, null);

            viewHolder.business_name = (FacedTextView)convertView.findViewById(R.id.business_name);
            viewHolder.purchase_code = (FacedTextView)convertView.findViewById(R.id.purchase_code);
            viewHolder.business_image = (ImageView)convertView.findViewById(R.id.business_image);
            viewHolder.price_pay = (FacedTextView)convertView.findViewById(R.id.price_pay);
            viewHolder.expire_pay = (FacedTextView)convertView.findViewById(R.id.expire_pay);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final PurchaseInfoDTO purchaseInfoDTO = purchaseInfoDTOs.get(position);

        viewHolder.purchase_code.setText("کد فاکتور " + persianEnglishDigit.E2P(purchaseInfoDTO.getPurchaseCode()));
        viewHolder.business_name.setText(purchaseInfoDTO.getMerchantName());

        if (purchaseInfoDTO.getMerchantImageId() != null) {
            viewHolder.business_image.setTag(purchaseInfoDTO.getMerchantImageId());
            imageManager.displayImage(purchaseInfoDTO.getMerchantImageId(), viewHolder.business_image, R.drawable.user_placeholder);
        }else {
            viewHolder.business_image.setImageResource(R.drawable.user_placeholder);
        }

        viewHolder.expire_pay.setText(dateUtil.remainingTime(purchaseInfoDTO.getExpirationDate(), currentDate));

        viewHolder.price_pay.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getAmount())));

        return convertView;

    }


    @Override
    public void notifyDataSetChanged() {
        currentDate = new Date();
        super.notifyDataSetChanged();
    }

    private class ViewHolder{

        ViewHolder(){ }
        FacedTextView business_name;
        FacedTextView purchase_code;
        ImageView business_image;
        FacedTextView price_pay;
        FacedTextView expire_pay;

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
                    purchaseInfoDTOs.remove(position);
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
