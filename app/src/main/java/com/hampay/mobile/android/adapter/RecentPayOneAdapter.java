package com.hampay.mobile.android.adapter;

import android.content.Context;
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
import com.hampay.mobile.android.model.RecentPay;

import java.util.List;


/**
 * Created by amir on 6/10/15.
 */
public class RecentPayOneAdapter extends BaseAdapter  {

    private Context context;
    private List<RecentPay> recentPays;

    public RecentPayOneAdapter(Context c, List<RecentPay> recentPays)
    {
        // TODO Auto-generated method stub
        context = c;
        this.recentPays = recentPays;

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return recentPays.size();
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
            convertView = inflater.inflate(R.layout.recent_pay_one, null);

            viewHolder.status_icon = (ImageView)convertView.findViewById(R.id.status_icon);
            viewHolder.user_name = (FacedTextView)convertView.findViewById(R.id.user_name);
            viewHolder.message = (FacedTextView)convertView.findViewById(R.id.message);
            viewHolder.user_mobile_no = (FacedTextView)convertView.findViewById(R.id.user_mobile_no);


            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

       RecentPay recentPay = recentPays.get(position);


        //viewHolder.status_icon = (ImageView)convertView.findViewById(R.id.status_icon);
        viewHolder.user_name.setText(recentPay.getName());
        viewHolder.message.setText(recentPay.getMessage());
        viewHolder.user_mobile_no.setText(recentPay.getPhone());

        return convertView;

    }


    private class ViewHolder{

        ViewHolder(){ }

        ImageView status_icon;
        FacedTextView user_name;
        FacedTextView user_mobile_no;
        FacedTextView message;
    }

}
