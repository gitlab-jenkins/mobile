package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import xyz.homapay.hampay.common.core.model.response.dto.FundDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 2/20/16.
 */
public class PendingFundAdapter extends BaseAdapter {

    private Context context;
    List<FundDTO> fundList;
    private PersianEnglishDigit persianEnglishDigit;
    private CurrencyFormatter formatter;


    public PendingFundAdapter(Context context, List<FundDTO> fundList)
    {
        // TODO Auto-generated method stub
        this.context = context;
        this.fundList = fundList;
        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return fundList.size();
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
            convertView = inflater.inflate(R.layout.pending_fund_item, null);
            viewHolder.user_image = (ImageView)convertView.findViewById(R.id.user_image);
            viewHolder.contact_name = (FacedTextView)convertView.findViewById(R.id.contact_name);
            viewHolder.create_date = (FacedTextView)convertView.findViewById(R.id.create_date);
            viewHolder.amount_value = (FacedTextView)convertView.findViewById(R.id.amount_value);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }


        FundDTO fund = fundList.get(position);
        if (fund.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(fund.getImage() , 0, fund.getImage().length);
            viewHolder.user_image.setImageBitmap(bitmap);
        }else {
            viewHolder.user_image.setImageResource(R.drawable.transaction_placeholder);
        }
        viewHolder.contact_name.setText(persianEnglishDigit.E2P(fund.getName()));
        viewHolder.create_date.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(fund.getCreationDate())));
        viewHolder.amount_value.setText(persianEnglishDigit.E2P(formatter.format(fund.getAmount())));

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
