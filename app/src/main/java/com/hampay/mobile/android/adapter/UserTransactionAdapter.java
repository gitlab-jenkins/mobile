package com.hampay.mobile.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hampay.common.core.model.response.dto.TransactionDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.util.JalaliConvert;
import com.hampay.common.core.model.response.dto.TransactionDTO.TransactionStatus;
import com.hampay.common.core.model.response.dto.TransactionDTO.TransactionType;
import com.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 7/16/15.
 */
public class UserTransactionAdapter extends UserTransactionGenericAdapter<TransactionDTO> {


    Context context;
    private ViewHolder viewHolder;
    TransactionDTO transactionDTO;
    PersianEnglishDigit persianEnglishDigit;

    public UserTransactionAdapter(Context context) {
        super(context);
        this.context = context;
        persianEnglishDigit = new PersianEnglishDigit();
    }



    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.transaction_item, null);

            viewHolder.status_icon = (ImageView)convertView.findViewById(R.id.status_icon);
            viewHolder.status_text = (FacedTextView)convertView.findViewById(R.id.status_text);
            viewHolder.user_name = (FacedTextView)convertView.findViewById(R.id.user_name);
            viewHolder.date_time = (FacedTextView)convertView.findViewById(R.id.date_time);
            viewHolder.message = (FacedTextView)convertView.findViewById(R.id.message);
            viewHolder.price_pay = (FacedTextView)convertView.findViewById(R.id.price_pay);


            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        transactionDTO = getItem(position);


        if (transactionDTO.getTransactionStatus() == TransactionStatus.SUCCESS){

            if (transactionDTO.getTransactionType() == TransactionType.CREDIT){
                viewHolder.status_text.setText(context.getString(R.string.credit));
                viewHolder.status_text.setTextColor(convertView.getResources().getColor(R.color.register_btn_color));
                viewHolder.status_icon.setImageResource(R.drawable.arrow_r);
            }
            else if (transactionDTO.getTransactionType() == TransactionType.DEBIT){
                viewHolder.status_text.setText(context.getString(R.string.debit));
                viewHolder.status_text.setTextColor(convertView.getResources().getColor(R.color.user_change_status));
                viewHolder.status_icon.setImageResource(R.drawable.arrow_p);
            }

        }else if (transactionDTO.getTransactionStatus() == TransactionStatus.PENDING) {
            viewHolder.status_text.setText(context.getString(R.string.pending));
            viewHolder.status_text.setTextColor(convertView.getResources().getColor(R.color.pending_transaction));
            viewHolder.status_icon.setImageResource(R.drawable.arrow_p);
        }
        else {
            viewHolder.status_text.setText(context.getString(R.string.fail));
            viewHolder.status_text.setTextColor(convertView.getResources().getColor(R.color.colorPrimary));
            viewHolder.status_icon.setImageResource(R.drawable.arrow_f);
        }


        viewHolder.user_name.setText(transactionDTO.getPersonName());
        viewHolder.date_time.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(transactionDTO.getTransactionDate())));
        viewHolder.message.setText(transactionDTO.getMessage());

        viewHolder.price_pay.setText(persianEnglishDigit.E2P(String.format("%,d", transactionDTO.getAmount()).replace(".", ","))
                        + "\n"
                + context.getString(R.string.currency_rials)
        );

        return convertView;
    }


    private class ViewHolder{

        ViewHolder(){ }

        ImageView status_icon;
        FacedTextView status_text;
        FacedTextView user_name;
        FacedTextView date_time;
        FacedTextView message;
        FacedTextView price_pay;
    }

}
