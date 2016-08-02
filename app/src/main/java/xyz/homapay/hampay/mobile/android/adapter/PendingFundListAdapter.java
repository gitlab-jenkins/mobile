package xyz.homapay.hampay.mobile.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import xyz.homapay.hampay.common.core.model.response.dto.FundDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.DateUtil;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 2/20/16.
 */
public class PendingFundListAdapter extends BaseAdapter {

    private Activity activity;
    List<FundDTO> fundList;
    private PersianEnglishDigit persianEnglishDigit;
    private CurrencyFormatter formatter;
    private String authToken;
    private Date currentDate;
    private DateUtil dateUtil;
    NumberFormat timeFormat;
    private ImageManager imageManager;

    public PendingFundListAdapter(Activity activity, List<FundDTO> fundList, String authToken)
    {
        currentDate = new Date();
        this.activity = activity;
        this.fundList = fundList;
        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();
        dateUtil = new DateUtil();
        timeFormat = new DecimalFormat("00");
        this.authToken = authToken;
        imageManager = new ImageManager(activity, 200000, false);
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


        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pending_fund_list_item, null);
            viewHolder.user_image = (ImageView)convertView.findViewById(R.id.user_image);
            viewHolder.contact_name = (FacedTextView)convertView.findViewById(R.id.contact_name);
            viewHolder.code = (FacedTextView)convertView.findViewById(R.id.code);
            viewHolder.remaining_time = (FacedTextView)convertView.findViewById(R.id.remaining_time);
            viewHolder.amount_value = (FacedTextView)convertView.findViewById(R.id.amount_value);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }


        FundDTO fund = fundList.get(position);

        if (fund.getImageId() != null) {
            viewHolder.user_image.setTag(fund.getImageId());
            imageManager.displayImage(fund.getImageId(), viewHolder.user_image, R.drawable.user_placeholder);
        }else {
            viewHolder.user_image.setImageResource(R.drawable.user_placeholder);
        }

        viewHolder.contact_name.setText(persianEnglishDigit.E2P(fund.getName()));
        viewHolder.code.setText("کد فاکتور " + persianEnglishDigit.E2P(fund.getCode()));
        viewHolder.remaining_time.setText(dateUtil.remainingTime(fund.getExpirationDate(), currentDate));
        viewHolder.amount_value.setText(persianEnglishDigit.E2P(formatter.format(fund.getAmount())));

        return convertView;

    }

    @Override
    public void notifyDataSetChanged() {
        currentDate = new Date();
        super.notifyDataSetChanged();
    }


    private class ViewHolder{
        ViewHolder(){ }
        FacedTextView contact_name;
        FacedTextView code;
        FacedTextView remaining_time;
        ImageView user_image;
        FacedTextView amount_value;
    }


}
