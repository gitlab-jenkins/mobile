package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import xyz.homapay.hampay.common.core.model.response.dto.CardDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;


/**
 * Created by amir on 9/13/16.
 */
public class CardAdapter extends BaseAdapter {

    private Context context;
    private List<CardDTO> cardList;
    private ViewHolder viewHolder;
    private PersianEnglishDigit persian;

    public CardAdapter(Context context, List<CardDTO> cardList){
        this.context = context;
        this.cardList = cardList;
        persian = new PersianEnglishDigit();
    }

    @Override
    public int getCount() {
        return cardList.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.card_item, null);
            viewHolder.bankName = (FacedTextView) convertView.findViewById(R.id.bankName);
            viewHolder.cardNumber = (FacedTextView) convertView.findViewById(R.id.cardNumber);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.bankName.setText(cardList.get(position).getBankName());
        viewHolder.cardNumber.setText(persian.E2P(cardList.get(position).getLast4Digits()));
        return convertView;
    }

    private class ViewHolder{
        FacedTextView cardNumber;
        FacedTextView bankName;
    }
}
