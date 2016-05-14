package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

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
public class HamPayContactsAdapter extends BaseAdapter {

    private Context context;
    List<ContactDTO> contacts;
    private PersianEnglishDigit persianEnglishDigit;
    private String authToken;


    public HamPayContactsAdapter(Context c, List<ContactDTO> contacts, String authToken)
    {
        // TODO Auto-generated method stub
        context = c;
        this.contacts = contacts;
        persianEnglishDigit = new PersianEnglishDigit();
        this.authToken = authToken;
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
            convertView = inflater.inflate(R.layout.hampay_contact_item, null);
            viewHolder.user_image = (ImageView)convertView.findViewById(R.id.user_image);
            viewHolder.contact_name = (FacedTextView)convertView.findViewById(R.id.contact_name);
            viewHolder.cell_number = (FacedTextView)convertView.findViewById(R.id.cell_number);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }


        ContactDTO contact = contacts.get(position);
        if (contact.getContactImageId() != null) {
            new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(viewHolder.user_image)).execute(Constants.IMAGE_PREFIX + authToken + "/" + contact.getContactImageId());
        }else {
//            viewHolder.user_image.setImageResource(R.drawable.user_icon_blue);
        }
        viewHolder.contact_name.setText(persianEnglishDigit.E2P(contact.getDisplayName()));
        viewHolder.cell_number.setText(persianEnglishDigit.E2P(contact.getCellNumber()));

        return convertView;

    }


    private class ViewHolder{
        ViewHolder(){ }
        FacedTextView contact_name;
        FacedTextView cell_number;
        ImageView user_image;
    }


}
