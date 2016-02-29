package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import xyz.homapay.hampay.common.core.model.response.dto.BusinessDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 6/26/15.
 */
public class HamPayBusinessesAdapter extends HamPayBusinessesGenericAdapter<BusinessDTO> {


    Context context;
    BusinessDTO businessDTO;

    PersianEnglishDigit persianEnglishDigit;

    public HamPayBusinessesAdapter(Context context) {
        super(context);
        this.context = context;

        persianEnglishDigit = new PersianEnglishDigit();
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
//            viewHolder.pay_to_business_button = (ButtonRectangle)convertView.findViewById(R.id.pay_to_business_button);


            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        businessDTO = getItem(position);


        viewHolder.business_name.setText(businessDTO.getTitle());
        viewHolder.business_description.setText(businessDTO.getCategory());
        viewHolder.business_phone_no.setText(persianEnglishDigit.E2P("تلفن: " + businessDTO.getDefaultPhoneNumber()));
        viewHolder.business_hampay_id.setText(persianEnglishDigit.E2P("شناسه: " + businessDTO.getCode()));

//        viewHolder.pay_to_business_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(context, BusinessPaymentInfoActivity.class);
//                intent.putExtra("business_name", getItem(position).getTitle());
//                intent.putExtra("business_code", getItem(position).getCode());
//                context.startActivity(intent);
//            }
//        });

        return convertView;
    }


    private class ViewHolder{


        ViewHolder(){ }

        FacedTextView business_name;
        FacedTextView business_description;
        FacedTextView business_phone_no;
        FacedTextView business_hampay_id;
//        ButtonRectangle pay_to_business_button;
    }

}
