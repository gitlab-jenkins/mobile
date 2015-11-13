package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import xyz.homapay.hampay.common.core.model.response.dto.BankDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;

import java.util.List;


/**
 * Created by amir on 6/10/15.
 */
public class BankListAdapter extends BaseAdapter  {

    private Context context;

    List<BankDTO> bankDTOs;

    public BankListAdapter(Context c, List<BankDTO> bankDTOs)
    {
        // TODO Auto-generated method stub
        context = c;

        this.bankDTOs = bankDTOs;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return bankDTOs.size();
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
            convertView = inflater.inflate(R.layout.bank_item_row, null);


            viewHolder.bank_name = (FacedTextView)convertView.findViewById(R.id.bank_name);


            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.bank_name.setText(bankDTOs.get(position).getTitle());


        return convertView;

    }


    private class ViewHolder{

        ViewHolder(){ }

        FacedTextView bank_name;
    }

}
