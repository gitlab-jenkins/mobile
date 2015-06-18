package com.hampay.mobile.android.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hampay.common.core.model.dto.ContactDTO;
import com.hampay.common.core.model.response.dto.BusinessDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;

import java.util.List;


/**
 * Created by amir on 6/10/15.
 */
public class HamPayBusinessAdapter extends BaseAdapter  {

    private Context context;
    private List<BusinessDTO> businessDTOs;

    public HamPayBusinessAdapter(Context c, List<BusinessDTO> businessDTOs)
    {
        // TODO Auto-generated method stub
        context = c;
        this.businessDTOs = businessDTOs;

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return businessDTOs.size();
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
            convertView = inflater.inflate(R.layout.contact_pay_business_item, null);



            viewHolder.business_name = (FacedTextView)convertView.findViewById(R.id.business_name);
            viewHolder.business_description = (FacedTextView)convertView.findViewById(R.id.business_description);
            viewHolder.business_phone_no = (FacedTextView)convertView.findViewById(R.id.business_phone_no);
            viewHolder.business_hampay_id = (FacedTextView)convertView.findViewById(R.id.business_hampay_id);
            viewHolder.pay_to_business = (CardView)convertView.findViewById(R.id.pay_to_business);

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        BusinessDTO businessDTO = businessDTOs.get(position);


        viewHolder.business_name.setText(businessDTO.getTitle());
        viewHolder.business_description.setText(businessDTO.getCategory());
        viewHolder.business_phone_no.setText("تلفن: " + businessDTO.getDefaultPhoneNumber());
        viewHolder.business_hampay_id.setText(businessDTO.getCode());
//        viewHolder.pay_to_business

        return convertView;

    }


    private class ViewHolder{

        ViewHolder(){ }

        FacedTextView business_name;
        FacedTextView business_description;
        FacedTextView business_phone_no;
        FacedTextView business_hampay_id;
        CardView pay_to_business;
    }

}
