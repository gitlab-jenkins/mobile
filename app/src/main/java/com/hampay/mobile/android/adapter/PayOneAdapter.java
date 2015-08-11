package com.hampay.mobile.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.PayOneActivity;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.component.sectionlist.SectionedBaseAdapter;
import com.hampay.mobile.android.model.EnabledHamPay;
import com.hampay.mobile.android.model.RecentPay;

import java.util.List;

/**
 * Created by amir on 6/21/15.
 */
public class PayOneAdapter extends SectionedBaseAdapter{

    Activity mContext;
    private List<RecentPay> recentPays;
    private List<EnabledHamPay> enabledHamPays;

    public  PayOneAdapter(Activity context, List<RecentPay> recentPays, List<EnabledHamPay> enabledHamPays){
        mContext = context;

        this.recentPays = recentPays;
        this.enabledHamPays = enabledHamPays;

    }

    @Override
    public Object getItem(int section, int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int section, int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSectionCount() {
        return 2;
    }

    @Override
    public int getCountForSection(int section) {
        if(section == 0) {
            return recentPays.size();
        }else if (section == 1){
            return enabledHamPays.size();
        }
        return 0;
    }


    @Override
    public View getItemView(int section, final int position, View convertView, ViewGroup parent) {
        RelativeLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (RelativeLayout) inflator.inflate(R.layout.contact_pay_one_item, null);
        } else {
            layout = (RelativeLayout) convertView;
        }
        //((TextView) layout.findViewById(R.id.textItem)).setText("Section " + section + " Item " + position);

        RelativeLayout contact_rl = (RelativeLayout)layout.findViewById(R.id.contact_rl);

        RelativeLayout recent_rl = (RelativeLayout)layout.findViewById(R.id.recent_rl);

        if (section == 0){
            contact_rl.setVisibility(View.GONE);
            recent_rl.setVisibility(View.VISIBLE);
        }
        else {
            contact_rl.setVisibility(View.VISIBLE);
            recent_rl.setVisibility(View.GONE);
        }


        ImageView status_icon = (ImageView)layout.findViewById(R.id.status_icon);

        ImageView call_icon = (ImageView)layout.findViewById(R.id.call_icon);
        ImageView pay_icon = (ImageView)layout.findViewById(R.id.pay_icon);
        FacedTextView user_name = (FacedTextView)layout.findViewById(R.id.user_name);
        FacedTextView user_phone = (FacedTextView)layout.findViewById(R.id.user_phone);
        FacedTextView message = (FacedTextView)layout.findViewById(R.id.message);


        FacedTextView contact_name = (FacedTextView)layout.findViewById(R.id.contact_name);
        FacedTextView contact_phone_no = (FacedTextView)layout.findViewById(R.id.contact_phone_no);
        ButtonRectangle pay_to_one_button = (ButtonRectangle)layout.findViewById(R.id.pay_to_one_button);




        if(section == 0) {

            final RecentPay recentPay = recentPays.get(position);
            user_name.setText(recentPay.getName());
            user_phone.setText(recentPay.getPhone());
            message.setText(recentPay.getMessage());
            call_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + recentPay.getPhone()));
                    mContext.startActivityForResult(intent, 1024);
                }
            });
            pay_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, PayOneActivity.class);
                    intent.putExtra("contact_name", recentPay.getName());
                    intent.putExtra("contact_phone_no", recentPay.getPhone());
                    mContext.startActivityForResult(intent, 1024);
                }
            });

        }else{

            final EnabledHamPay enabledHamPay = enabledHamPays.get(position);
            contact_name.setText(enabledHamPay.getDisplayName());
            contact_phone_no.setText(enabledHamPay.getCellNumber());

            pay_to_one_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, PayOneActivity.class);
                    intent.putExtra("contact_name", enabledHamPay.getDisplayName());
                    intent.putExtra("contact_phone_no", enabledHamPay.getCellNumber());
                    mContext.startActivityForResult(intent, 1024);
                }
            });


        }

        return layout;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflator.inflate(R.layout.pay_one_header, null);
        } else {
            layout = (LinearLayout) convertView;
        }
        //((TextView) layout.findViewById(R.id.textItem)).setText("Header for section " + section);

        FacedTextView pinned_header_text = (FacedTextView) layout.findViewById(R.id.pinned_header_text);
        //textView.setText("Header for section " + section);
        if(section == 0) {
            pinned_header_text.setBackgroundColor(mContext.getResources().getColor(R.color.confirmation));
            pinned_header_text.setText(mContext.getResources().getString(R.string.recent_pay_one));
        }else{
            pinned_header_text.setBackgroundColor(mContext.getResources().getColor(R.color.confirmation));
            pinned_header_text.setText(mContext.getResources().getString(R.string.hampay_contacts));
        }



        return layout;
    }

}
