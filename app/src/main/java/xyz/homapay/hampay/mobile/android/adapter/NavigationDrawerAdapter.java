package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Collections;
import java.util.List;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.model.NavDrawerItem;

/**
 * Created by amir on 6/5/15.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.icon.setImageResource(current.getIcon());

        if (position == 0){
            holder.action_divider.setVisibility(View.VISIBLE);
        }else if (position == 2){
            holder.action_divider.setVisibility(View.VISIBLE);
        }else {
            holder.action_divider.setVisibility(View.GONE);
        }
        if (data.get(position).getSelected() == 1){
        }else {
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        FacedTextView title;
        ImageView icon;
        LinearLayout nav_draw_bg;
        View action_divider;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (FacedTextView) itemView.findViewById(R.id.title);
            icon = (ImageView)itemView.findViewById(R.id.icon);
            nav_draw_bg = (LinearLayout)itemView.findViewById(R.id.nav_draw_bg);
            action_divider = itemView.findViewById(R.id.action_divider);
        }
    }
}
