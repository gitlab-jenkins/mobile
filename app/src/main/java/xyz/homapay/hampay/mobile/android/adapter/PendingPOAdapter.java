package xyz.homapay.hampay.mobile.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 2/20/16.
 */
public class PendingPOAdapter extends BaseAdapter {

    private Activity activity;
    List<PaymentInfoDTO> paymentInfoList;
    private PersianEnglishDigit persianEnglishDigit;
    private String authToken;
    private CurrencyFormatter formatter;
    private ImageManager imageManager;


    public PendingPOAdapter(Activity activity, List<PaymentInfoDTO> paymentInfoList, String authToken)
    {
        // TODO Auto-generated method stub
        this.activity = activity;
        this.paymentInfoList = paymentInfoList;
        persianEnglishDigit = new PersianEnglishDigit();
        this.authToken = authToken;
        formatter = new CurrencyFormatter();
        imageManager = new ImageManager(activity, 200000, false);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return paymentInfoList.size();
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


        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pending_po_item, null);
            viewHolder.user_image = (ImageView)convertView.findViewById(R.id.user_image);
            viewHolder.contact_name = (FacedTextView)convertView.findViewById(R.id.contact_name);
            viewHolder.create_date = (FacedTextView)convertView.findViewById(R.id.create_date);
            viewHolder.amount_value = (FacedTextView)convertView.findViewById(R.id.amount_value);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        PaymentInfoDTO paymentInfo = paymentInfoList.get(position);

        if (paymentInfo.getImageId() != null) {
            String userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + paymentInfo.getImageId();
            viewHolder.user_image.setTag(userImageUrl.split("/")[6]);
            imageManager.displayImage(userImageUrl, viewHolder.user_image, R.drawable.user_placeholder);
        }else {
            viewHolder.user_image.setImageResource(R.drawable.user_placeholder);
        }
        viewHolder.contact_name.setText(persianEnglishDigit.E2P(paymentInfo.getCalleeName()));
        viewHolder.create_date.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(paymentInfo.getCreatedBy())));
        viewHolder.amount_value.setText(persianEnglishDigit.E2P(formatter.format(paymentInfo.getAmount() + paymentInfo.getVat())));

        return convertView;

    }


    private class ViewHolder{
        ViewHolder(){ }
        FacedTextView contact_name;
        FacedTextView create_date;
        ImageView user_image;
        FacedTextView amount_value;
    }


}
