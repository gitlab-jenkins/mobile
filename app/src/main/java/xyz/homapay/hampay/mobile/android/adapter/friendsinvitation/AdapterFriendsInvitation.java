package xyz.homapay.hampay.mobile.android.adapter.friendsinvitation;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.common.friendsinvitation.FriendsObject;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;

/**
 * Created by mohammad on 1/30/17.
 */

public class AdapterFriendsInvitation extends RecyclerView.Adapter<AdapterFriendsInvitation.ViewHolder> {

    private Context ctx;
    private LayoutInflater li;
    private List<FriendsObject> items;

    public AdapterFriendsInvitation(Context ctx, List<FriendsObject> items) {
        this.ctx = ctx;
        this.li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = li.inflate(R.layout.list_item_friends_invitaion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvNumber.setText(items.get(position).getNormalizedNumber());
        holder.tvName.setText(items.get(position).getContact().getDisplayName());
        holder.chkSelected.setChecked(items.get(position).isSelected());
        Log.i("XXXX-item", items.get(position).isSelected() + "");
        try {
            if (items.get(position).getContact().getPhotoUri() != null)
                Picasso.with(ctx)
                        .load(Uri.parse(items.get(position).getContact().getPhotoUri()))
                        .placeholder(R.drawable.user_placeholder)
                        .into(holder.imgAvatar);
            else
                holder.imgAvatar.setImageResource(R.drawable.user_placeholder);
            holder.rlMain.setOnClickListener(view -> {
                items.get(position).setSelected(!items.get(position).isSelected());
                holder.chkSelected.setChecked(!holder.chkSelected.isChecked());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<FriendsObject> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public List<FriendsObject> getItems() {
        return items;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlMain;
        ImageView imgAvatar;
        FacedTextView tvName;
        CheckBox chkSelected;
        FacedTextView tvNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            rlMain = (RelativeLayout) itemView.findViewById(R.id.rlMain);
            imgAvatar = (ImageView) itemView.findViewById(R.id.imgAvatar);
            tvName = (FacedTextView) itemView.findViewById(R.id.tvName);
            chkSelected = (CheckBox) itemView.findViewById(R.id.chkSelected);
            tvNumber = (FacedTextView) itemView.findViewById(R.id.tvNumber);
        }
    }
}
