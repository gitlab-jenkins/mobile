package xyz.homapay.hampay.mobile.android.adapter.charge;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.common.charge.ChargeAdapterModel;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.AppManager;

/**
 * Created by mohammad on 1/11/17.
 */

public class ChargeAdapter extends RecyclerView.Adapter<ChargeAdapter.Holder> {

    private LayoutInflater li;
    private List<ChargeAdapterModel> items;
    private Context ctx;

    public ChargeAdapter(Context ctx, List<ChargeAdapterModel> items) {
        this.items = items;
        this.ctx = ctx;
        li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int position) {
        return new Holder(li.inflate(R.layout.list_item_charge_select, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        long amount = -1;
        try {
            amount = Long.parseLong(items.get(position).getDesc());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (amount == -1)
            holder.tvText.setText(items.get(position).getDesc());
        else
            holder.tvText.setText(AppManager.amountFixer(amount));
        holder.tvText.setTag(items.get(position).getIndex());
        if (!items.get(position).isSelected()) {
            holder.tvText.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white));
            holder.tvText.setTextColor(ContextCompat.getColor(ctx, R.color.colorAccent));
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setSelected(int index) {
        for (ChargeAdapterModel item : items) {
            if (item.getIndex() == index) {
                item.setSelected(true);
            } else {
                item.setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    public long getAmount(int position) {
        long amount = -1;
        try {
            amount = Long.parseLong(items.get(position).getDesc());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amount;
    }

    protected class Holder extends RecyclerView.ViewHolder {

        protected FacedTextView tvText;

        public Holder(View itemView) {
            super(itemView);
            tvText = (FacedTextView) itemView.findViewById(R.id.text);
        }
    }
}
