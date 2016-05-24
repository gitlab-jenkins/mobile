package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import xyz.homapay.hampay.common.core.model.response.dto.BusinessDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 6/26/15.
 */
public class HamPayBusinessesAdapter extends HamPayBusinessesGenericAdapter<BusinessDTO> {


    private Context context;
    private BusinessDTO businessDTO;
    private PersianEnglishDigit persianEnglishDigit;
    private String authToken;

    public HamPayBusinessesAdapter(Context context, String authToken) {
        super(context);
        this.context = context;
        persianEnglishDigit = new PersianEnglishDigit();
        this.authToken = authToken;
    }

    private ViewHolder viewHolder;

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.business_item, null);

            viewHolder.business_name = (FacedTextView)convertView.findViewById(R.id.business_name);
            viewHolder.business_hampay_id = (FacedTextView)convertView.findViewById(R.id.business_hampay_id);
            viewHolder.business_image = (ImageView)convertView.findViewById(R.id.business_image);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        businessDTO = getItem(position);

        viewHolder.business_name.setText(businessDTO.getTitle());
        viewHolder.business_hampay_id.setText(persianEnglishDigit.E2P("شناسه: " + businessDTO.getCode()));
        if (businessDTO.getBusinessImageId() != null) {
            new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(viewHolder.business_image)).execute(Constants.IMAGE_PREFIX + authToken + "/" + businessDTO.getBusinessImageId());
        }else {
            viewHolder.business_image.setImageResource(R.drawable.user_placeholder);
        }
        return convertView;
    }


    private class ViewHolder{

        ViewHolder(){ }

        FacedTextView business_name;
        FacedTextView business_hampay_id;
        ImageView business_image;
    }

}
