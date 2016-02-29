package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseLongArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.model.NavDrawerItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.icon.setImageResource(current.getIcon());
        if (data.get(position).getSelected() == 1){
            holder.nav_draw_bg.setSelected(true);
        }else {
            holder.nav_draw_bg.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        FacedTextView title;
        ImageView icon;
        LinearLayout nav_draw_bg;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (FacedTextView) itemView.findViewById(R.id.title);
            icon = (ImageView)itemView.findViewById(R.id.icon);
            nav_draw_bg = (LinearLayout)itemView.findViewById(R.id.nav_draw_bg);
//            nav_draw_bg.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//
//            Log.e("POS", getAdapterPosition() + "");
//            if (selectedItems.get(getAdapterPosition(), false)) {
//                selectedItems.delete(getAdapterPosition());
//                nav_draw_bg.setSelected(false);
//            }
//            else {
//                selectedItems.put(getAdapterPosition(), true);
//                nav_draw_bg.setSelected(true);
//            }
//        }
    }
}
