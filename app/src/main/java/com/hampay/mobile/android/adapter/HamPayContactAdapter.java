package com.hampay.mobile.android.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hampay.common.core.model.dto.ContactDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.model.RecentPay;

import java.util.List;


/**
 * Created by amir on 6/10/15.
 */
public class HamPayContactAdapter extends BaseAdapter  {

    private Context context;
    private List<ContactDTO> contactDTOs;

    public HamPayContactAdapter(Context c, List<ContactDTO> contactDTOs)
    {
        // TODO Auto-generated method stub
        context = c;
        this.contactDTOs = contactDTOs;

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return contactDTOs.size();
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
            convertView = inflater.inflate(R.layout.contact_pay_one_item, null);


            viewHolder.contact_name = (FacedTextView)convertView.findViewById(R.id.contact_name);
            viewHolder.contact_phone_no = (FacedTextView)convertView.findViewById(R.id.contact_phone_no);
            viewHolder.pay_to_one = (CardView)convertView.findViewById(R.id.pay_to_one);


            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

//       RecentPay recentPay = contactDTOs.get(position);

        ContactDTO contactDTO = contactDTOs.get(position);


        viewHolder.contact_name.setText(contactDTO.getDisplayName());
        viewHolder.contact_phone_no.setText(contactDTO.getCellNumber());


        return convertView;

    }


    private class ViewHolder{

        ViewHolder(){ }

        FacedTextView contact_name;
        FacedTextView contact_phone_no;
        CardView pay_to_one;
    }

}
