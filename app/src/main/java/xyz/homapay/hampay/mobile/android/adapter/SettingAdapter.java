package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.model.setting.HamPaySetting;


/**
 * Created by amir on 6/10/15.
 */
public class SettingAdapter extends BaseAdapter  {

    private Context context;

    List<HamPaySetting> hamPaySettings;

    public SettingAdapter(Context context, List<HamPaySetting> hamPaySettings)
    {
        // TODO Auto-generated method stub
        this.context = context;

        this.hamPaySettings = hamPaySettings;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return hamPaySettings.size();
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
            convertView = inflater.inflate(R.layout.setting_item_row, null);
            viewHolder.setting_item = (FacedTextView)convertView.findViewById(R.id.setting_item);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.setting_item.setText(hamPaySettings.get(position).getTitle());
        return convertView;
    }


    private class ViewHolder{
        ViewHolder(){ }
        FacedTextView setting_item;
    }

}
