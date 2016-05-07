package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.circleimageview.CircleImageView;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO.TransactionStatus;
import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO.TransactionType;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 7/16/15.
 */
public class UserTransactionAdapter extends UserTransactionGenericAdapter<TransactionDTO> {

    private Context context;
    private ViewHolder viewHolder;
    private TransactionDTO transactionDTO;
    private PersianEnglishDigit persianEnglishDigit;
    private CurrencyFormatter currencyFormatter;
    private String authToken;

    public UserTransactionAdapter(Context context, String authToken) {
        super(context);
        this.context = context;
        persianEnglishDigit = new PersianEnglishDigit();
        currencyFormatter = new CurrencyFormatter();
        this.authToken = authToken;
    }



    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.transaction_item, null);

            viewHolder.status_icon = (ImageView)convertView.findViewById(R.id.status_icon);
            viewHolder.status_text = (FacedTextView)convertView.findViewById(R.id.status_text);
            viewHolder.image = (CircleImageView)convertView.findViewById(R.id.image);
            viewHolder.user_name = (FacedTextView)convertView.findViewById(R.id.user_name);
            viewHolder.date_time = (FacedTextView)convertView.findViewById(R.id.date_time);
            viewHolder.price_pay = (FacedTextView)convertView.findViewById(R.id.price_pay);
            viewHolder.reject_message = (FacedTextView)convertView.findViewById(R.id.reject_message);
            viewHolder.user_fee_value = (FacedTextView)convertView.findViewById(R.id.user_fee_value);
            viewHolder.user_fee_ll = (LinearLayout)convertView.findViewById(R.id.user_fee_ll);

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        transactionDTO = getItem(position);


        if (transactionDTO.getTransactionStatus() == TransactionStatus.SUCCESS){

            viewHolder.reject_message.setVisibility(View.GONE);

            if (transactionDTO.getTransactionType() == TransactionType.CREDIT){
                viewHolder.status_text.setText(context.getString(R.string.credit));
                viewHolder.status_text.setTextColor(ContextCompat.getColor(context, R.color.register_btn_color));
                viewHolder.status_icon.setImageResource(R.drawable.arrow_r);
                viewHolder.user_fee_ll.setVisibility(View.GONE);
            }
            else if (transactionDTO.getTransactionType() == TransactionType.DEBIT){
                viewHolder.status_text.setText(context.getString(R.string.debit));
                viewHolder.status_text.setTextColor(ContextCompat.getColor(context, R.color.user_change_status));
                viewHolder.status_icon.setImageResource(R.drawable.arrow_p);
                viewHolder.user_fee_ll.setVisibility(View.VISIBLE);
            }

        }else if (transactionDTO.getTransactionStatus() == TransactionStatus.PENDING) {
            viewHolder.reject_message.setVisibility(View.GONE);
            viewHolder.status_text.setText(context.getString(R.string.pending));
            viewHolder.status_text.setTextColor(ContextCompat.getColor(context, R.color.pending_transaction));
            viewHolder.status_icon.setImageResource(R.drawable.pending);
            viewHolder.user_fee_ll.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.reject_message.setVisibility(View.VISIBLE);
            viewHolder.status_text.setText(context.getString(R.string.fail));
            viewHolder.status_text.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            viewHolder.status_icon.setImageResource(R.drawable.arrow_f);
            viewHolder.user_fee_ll.setVisibility(View.GONE);
        }


        viewHolder.user_name.setText(transactionDTO.getPersonName());
        viewHolder.date_time.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(transactionDTO.getTransactionDate())));
//        viewHolder.reject_message.setText(transactionDTO.getRejectReasonMessage());
//        viewHolder.user_fee_value.setText(persianEnglishDigit.E2P(currencyFormatter.format(transactionDTO.getFeeCharge())) + context.getString(R.string.currency_rials));
        viewHolder.price_pay.setText(persianEnglishDigit.E2P(currencyFormatter.format(transactionDTO.getAmount())) + "\n" + context.getString(R.string.currency_rials));

        if (transactionDTO.getImageId() != null) {
            String userImageUrl = Constants.IMAGE_PREFIX + authToken + "/" + transactionDTO.getImageId();
            new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(viewHolder.image)).execute(userImageUrl);
        }else {
            viewHolder.image.setImageResource(R.drawable.user_icon_blue);
        }

        return convertView;
    }


    private class ViewHolder{

        ViewHolder(){ }

        ImageView status_icon;
        CircleImageView image;
        FacedTextView status_text;
        FacedTextView user_name;
        FacedTextView date_time;
        FacedTextView price_pay;
        FacedTextView reject_message;
        FacedTextView user_fee_value;
        LinearLayout user_fee_ll;
    }

}
