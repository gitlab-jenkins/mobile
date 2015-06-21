package com.hampay.mobile.android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.response.TransactionListResponse;
import com.hampay.common.core.model.response.dto.TransactionDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;

import java.util.List;


/**
 * Created by amir on 6/10/15.
 */
public class TransactionAdapter extends BaseAdapter  {

    private Context context;
    private ResponseMessage<TransactionListResponse> transactionListResponse;

    public TransactionAdapter(Context c, ResponseMessage<TransactionListResponse> transactions)
    {
        // TODO Auto-generated method stub
        context = c;
        this.transactionListResponse = transactions;

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return transactionListResponse.getService().getTransactions().size();
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
            convertView = inflater.inflate(R.layout.transaction_item, null);

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

        TransactionDTO transaction = transactionListResponse.getService().getTransactions().get(position);

        if (transaction.getTransactionStatus().ordinal() == 0){

            if (transaction.getTransactionType().ordinal() == 0){
                viewHolder.status_text.setText(context.getString(R.string.credit));
                viewHolder.status_text.setTextColor(convertView.getResources().getColor(R.color.register_btn_color));
                viewHolder.status_icon.setImageResource(R.drawable.arrow_r);
            }
            else if (transaction.getTransactionType().ordinal() == 1){
                viewHolder.status_text.setText(context.getString(R.string.debit));
                viewHolder.status_text.setTextColor(convertView.getResources().getColor(R.color.user_change_status));
                viewHolder.status_icon.setImageResource(R.drawable.arrow_p);
            }

        }else {
            viewHolder.status_text.setText(context.getString(R.string.fail));
            viewHolder.status_text.setTextColor(convertView.getResources().getColor(R.color.colorPrimary));
            viewHolder.status_icon.setImageResource(R.drawable.arrow_f);
        }


        viewHolder.user_name.setText(transaction.getPersonName());
        viewHolder.date_time.setText(transaction.getTransactionDate().getTime() + "");
        viewHolder.message.setText(transaction.getMessage());
        viewHolder.price_pay.setText(transaction.getAmount() + "");

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
