package com.hampay.mobile.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import com.hampay.common.core.model.response.dto.BusinessDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.PayBusinessActivity;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonRectangle;

/**
 * Created by amir on 6/26/15.
 */
public class HamPayBusinessesAdapter extends HamPayBusinessesGenericAdapter<BusinessDTO> {


    Context context;
    BusinessDTO businessDTO;

    public HamPayBusinessesAdapter(Context context) {
        super(context);
        this.context = context;
    }

    private ViewHolder viewHolder;

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.contact_pay_business_item, null);

            viewHolder.business_name = (FacedTextView)convertView.findViewById(R.id.business_name);
            viewHolder.business_description = (FacedTextView)convertView.findViewById(R.id.business_description);
            viewHolder.business_phone_no = (FacedTextView)convertView.findViewById(R.id.business_phone_no);
            viewHolder.business_hampay_id = (FacedTextView)convertView.findViewById(R.id.business_hampay_id);
            viewHolder.pay_to_business_button = (ButtonRectangle)convertView.findViewById(R.id.pay_to_business_button);


            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        businessDTO = getItem(position);


        viewHolder.business_name.setText(businessDTO.getTitle());
        viewHolder.business_description.setText(businessDTO.getCategory());
        viewHolder.business_phone_no.setText("تلفن: " + businessDTO.getDefaultPhoneNumber());
        viewHolder.business_hampay_id.setText(businessDTO.getCode());
        viewHolder.pay_to_business_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, PayBusinessActivity.class);
                intent.putExtra("business_name", businessDTO.getTitle());
                intent.putExtra("business_code", businessDTO.getCode());
                context.startActivity(intent);
            }
        });

        return convertView;
    }


    private class ViewHolder{


        ViewHolder(){ }

        FacedTextView business_name;
        FacedTextView business_description;
        FacedTextView business_phone_no;
        FacedTextView business_hampay_id;
        ButtonRectangle pay_to_business_button;
    }

}
