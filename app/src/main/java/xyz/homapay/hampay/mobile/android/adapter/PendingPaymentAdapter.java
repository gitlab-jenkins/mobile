package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;


/**
 * Created by amir on 6/10/15.
 */
public class PendingPaymentAdapter extends BaseAdapter  {

    private Context context;

    List<PurchaseInfoDTO> purchaseInfoDTOs;

    public PendingPaymentAdapter(Context c, List<PurchaseInfoDTO> purchaseInfoDTOs)
    {
        // TODO Auto-generated method stub
        context = c;

        this.purchaseInfoDTOs = purchaseInfoDTOs;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return purchaseInfoDTOs.size();
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
            convertView = inflater.inflate(R.layout.pending_payment_row, null);


            viewHolder.business_name = (FacedTextView)convertView.findViewById(R.id.business_name);
            viewHolder.business_logo = (ImageView)convertView.findViewById(R.id.business_logo);
            viewHolder.date_time = (FacedTextView)convertView.findViewById(R.id.date_time);
            viewHolder.price_pay = (FacedTextView)convertView.findViewById(R.id.price_pay);
            viewHolder.expire_pay = (FacedTextView)convertView.findViewById(R.id.expire_pay);


            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.business_name.setText(purchaseInfoDTOs.get(position).getMerchantName());
        String LogoUrl = Constants.HTTPS_SERVER_IP + "/merchant-logo/" + purchaseInfoDTOs.get(position).getMerchantLogoName();
        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(viewHolder.business_logo)).execute(Constants.HTTPS_SERVER_IP + "/merchant-logo/" + purchaseInfoDTOs.get(position).getMerchantLogoName());
        viewHolder.date_time.setText(new PersianEnglishDigit().E2P(new JalaliConvert().GregorianToPersian(purchaseInfoDTOs.get(position).getCreatedBy())));
        viewHolder.price_pay.setText(new PersianEnglishDigit().E2P(purchaseInfoDTOs.get(position).getAmount().toString()) + " ریال");
        viewHolder.expire_pay.setVisibility(View.GONE);
//        viewHolder.expire_pay.setText(purchaseInfoDTOs.get(position).get);



        return convertView;

    }


    private class ViewHolder{

        ViewHolder(){ }

        FacedTextView business_name;
        ImageView business_logo;
        FacedTextView date_time;
        FacedTextView price_pay;
        FacedTextView expire_pay;
    }

}
