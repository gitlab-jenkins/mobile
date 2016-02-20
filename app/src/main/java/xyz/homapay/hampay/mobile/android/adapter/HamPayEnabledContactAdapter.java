package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.circleimageview.CircleImageView;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 2/20/16.
 */
public class HamPayEnabledContactAdapter extends BaseAdapter {

    private Context context;
    List<ContactDTO> contacts;
    private PersianEnglishDigit persianEnglishDigit;
    private String loginTokenId;


    public HamPayEnabledContactAdapter(Context c, List<ContactDTO> contacts, String loginTokenId)
    {
        // TODO Auto-generated method stub
        context = c;
        this.contacts = contacts;
        persianEnglishDigit = new PersianEnglishDigit();
        this.loginTokenId = loginTokenId;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return contacts.size();
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
            viewHolder.image_profile = (CircleImageView)convertView.findViewById(R.id.image_profile);
            viewHolder.contact_name = (FacedTextView)convertView.findViewById(R.id.contact_name);
            viewHolder.contact_phone_no = (FacedTextView)convertView.findViewById(R.id.contact_phone_no);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }


        ContactDTO contact = contacts.get(position);
        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(viewHolder.image_profile)).execute(Constants.HTTPS_SERVER_IP + "/users/" + loginTokenId + "/" + contact.getContactImageId());
        viewHolder.contact_name.setText(persianEnglishDigit.E2P(/*contact.getDisplayName()*/"علی امیری آخوندیان"));
        viewHolder.contact_phone_no.setText(persianEnglishDigit.E2P(contact.getCellNumber()));

        return convertView;

    }


    private class ViewHolder{
        ViewHolder(){ }
        FacedTextView contact_name;
        FacedTextView contact_phone_no;
        CircleImageView image_profile;
    }


}
