package xyz.homapay.hampay.mobile.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import xyz.homapay.hampay.mobile.android.img.ImageHelper;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.DateUtil;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 2/20/16.
 */
public class PendingFundListAdapter extends BaseAdapter {

    List<FundDTO> fundList;
    NumberFormat timeFormat;
    private Activity activity;
    private PersianEnglishDigit persianEnglishDigit;
    private CurrencyFormatter formatter;
    private String authToken;
    private Date currentDate;
    private DateUtil dateUtil;
    private ViewHolder viewHolder;

    public PendingFundListAdapter(Activity activity, List<FundDTO> fundList, String authToken) {
        currentDate = new Date();
        this.activity = activity;
        this.fundList = fundList;
        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();
        dateUtil = new DateUtil();
        timeFormat = new DecimalFormat("00");
        this.authToken = authToken;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return fundList.size();
    }

    public FundDTO getItem(int position) {
        // TODO Auto-generated method stub
        return fundList.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub


        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pending_fund_list_item, null);
            viewHolder.user_image = (ImageView) convertView.findViewById(R.id.user_image);
            viewHolder.contact_name = (FacedTextView) convertView.findViewById(R.id.contact_name);
            viewHolder.code_digit_1 = (FacedTextView) convertView.findViewById(R.id.code_digit_1);
            viewHolder.code_digit_2 = (FacedTextView) convertView.findViewById(R.id.code_digit_2);
            viewHolder.code_digit_3 = (FacedTextView) convertView.findViewById(R.id.code_digit_3);
            viewHolder.code_digit_4 = (FacedTextView) convertView.findViewById(R.id.code_digit_4);
            viewHolder.code_digit_5 = (FacedTextView) convertView.findViewById(R.id.code_digit_5);
            viewHolder.code_digit_6 = (FacedTextView) convertView.findViewById(R.id.code_digit_6);
            viewHolder.remaining_time = (FacedTextView) convertView.findViewById(R.id.remaining_time);
            viewHolder.amount_value = (FacedTextView) convertView.findViewById(R.id.amount_value);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        FundDTO fund = fundList.get(position);

        if (fund.getImageId() != null) {
            viewHolder.user_image.setTag(fund.getImageId());
            ImageHelper.getInstance(activity).imageLoader(fund.getImageId(), viewHolder.user_image, R.drawable.user_placeholder);
        } else {
            viewHolder.user_image.setImageResource(R.drawable.user_placeholder);
        }

        viewHolder.contact_name.setText(persianEnglishDigit.E2P(fund.getName()));
        if (fund.getPaymentType() == FundDTO.PaymentType.PURCHASE) {
            if (fund.getCode().length() == 6) {
                viewHolder.code_digit_1.setText(String.valueOf(fund.getCode().charAt(0)));
                viewHolder.code_digit_2.setText(String.valueOf(fund.getCode().charAt(1)));
                viewHolder.code_digit_3.setText(String.valueOf(fund.getCode().charAt(2)));
                viewHolder.code_digit_4.setText(String.valueOf(fund.getCode().charAt(3)));
                viewHolder.code_digit_5.setText(String.valueOf(fund.getCode().charAt(4)));
                viewHolder.code_digit_6.setText(String.valueOf(fund.getCode().charAt(5)));
            }
        } else if (fund.getPaymentType() == FundDTO.PaymentType.PAYMENT) {
            viewHolder.code_digit_1.setText("");
            viewHolder.code_digit_2.setText("");
            viewHolder.code_digit_3.setText("");
            viewHolder.code_digit_4.setText("");
            viewHolder.code_digit_5.setText("");
            viewHolder.code_digit_6.setText(fund.getProductCode());
        } else if (fund.getPaymentType() == FundDTO.PaymentType.UTILITY_BILL) {
        } else if (fund.getPaymentType() == FundDTO.PaymentType.UTILITY_BILL) {
            viewHolder.code_digit_1.setText("");
            viewHolder.code_digit_2.setText("");
            viewHolder.code_digit_3.setText("");
            viewHolder.code_digit_4.setText("");
            viewHolder.code_digit_5.setText("");
            viewHolder.code_digit_6.setText(fund.getProductCode());
        } else if (fund.getPaymentType() == FundDTO.PaymentType.TOP_UP) {
            viewHolder.code_digit_6.setText(fund.getProductCode());
        }

        if (fund.getExpirationDate().getTime() < currentDate.getTime()) {
            Log.e("Expired", "Expired");
            fundList.remove(position);
        }

        viewHolder.remaining_time.setText(dateUtil.remainingTime(fund.getExpirationDate(), currentDate));
        viewHolder.amount_value.setText(persianEnglishDigit.E2P(formatter.format(fund.getAmount())));

        return convertView;

    }

    @Override
    public void notifyDataSetChanged() {
        currentDate = new Date();
        super.notifyDataSetChanged();
    }

    private class ViewHolder {
        FacedTextView contact_name;
        FacedTextView code_digit_1;
        FacedTextView code_digit_2;
        FacedTextView code_digit_3;
        FacedTextView code_digit_4;
        FacedTextView code_digit_5;
        FacedTextView code_digit_6;
        FacedTextView remaining_time;
        ImageView user_image;
        FacedTextView amount_value;

        ViewHolder() {
        }
    }


}
