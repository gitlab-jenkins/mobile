package xyz.homapay.hampay.mobile.android.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO.TransactionStatus;
import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO.TransactionType;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.img.ImageHelper;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 7/16/15.
 */
public class UserTransactionAdapter extends UserTransactionGenericAdapter<TransactionDTO> {

    private Activity activity;
    private ViewHolder viewHolder;
    private TransactionDTO transactionDTO;
    private PersianEnglishDigit persianEnglishDigit;
    private CurrencyFormatter currencyFormatter;

    public UserTransactionAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        persianEnglishDigit = new PersianEnglishDigit();
        currencyFormatter = new CurrencyFormatter();
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.transaction_item, null);

            viewHolder.status_icon = (ImageView) convertView.findViewById(R.id.status_icon);
            viewHolder.status_text = (FacedTextView) convertView.findViewById(R.id.status_text);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.user_name = (FacedTextView) convertView.findViewById(R.id.user_name);
            viewHolder.date_time = (FacedTextView) convertView.findViewById(R.id.date_time);
            viewHolder.amountValue = (FacedTextView) convertView.findViewById(R.id.amountValue);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        transactionDTO = getItem(position);


        if (transactionDTO.getTransactionStatus() == TransactionStatus.SUCCESS) {


            if (transactionDTO.getTransactionType() == TransactionType.CREDIT) {
                viewHolder.status_text.setText(activity.getString(R.string.credit));
                viewHolder.status_text.setTextColor(ContextCompat.getColor(activity, R.color.register_btn_color));
                viewHolder.status_icon.setImageResource(R.drawable.arrow_r);
            } else if (transactionDTO.getTransactionType() == TransactionType.DEBIT) {
                viewHolder.status_text.setText(activity.getString(R.string.debit));
                viewHolder.status_text.setTextColor(ContextCompat.getColor(activity, R.color.user_change_status));
                viewHolder.status_icon.setImageResource(R.drawable.arrow_p);
            }

        }
//        else if (transactionDTO.getTransactionStatus() == TransactionStatus.PENDING) {
//            viewHolder.status_text.setText(activity.getString(R.string.pending));
//            viewHolder.status_text.setTextColor(ContextCompat.getColor(activity, R.color.pending_transaction));
//            viewHolder.status_icon.setImageResource(R.drawable.pending);
//        }
        else {
            viewHolder.status_text.setText(getItem(position).getTransactionType().equals(TransactionType.CREDIT) ? R.string.fail_credit : R.string.fail_debit);
            viewHolder.status_text.setTextColor(ContextCompat.getColor(activity, R.color.failed_transaction));
            viewHolder.status_icon.setImageResource(R.drawable.arrow_f);
        }

        if (transactionDTO.getImageId() != null) {
            viewHolder.image.setTag(transactionDTO.getImageId());
            ImageHelper.getInstance(activity).imageLoader(transactionDTO.getImageId(), viewHolder.image, R.drawable.user_placeholder);
        } else {
            viewHolder.image.setImageResource(R.drawable.user_placeholder);
        }

        viewHolder.user_name.setText(transactionDTO.getPersonName());
        viewHolder.date_time.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(transactionDTO.getTransactionDate())));
        viewHolder.amountValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(transactionDTO.getAmount())));

        return convertView;
    }


    private class ViewHolder {

        ImageView status_icon;
        ImageView image;
        FacedTextView status_text;
        FacedTextView user_name;
        FacedTextView date_time;
        FacedTextView amountValue;

        ViewHolder() {
        }
    }

}
