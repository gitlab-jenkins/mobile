package xyz.homapay.hampay.mobile.android.dialog.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.charge.ChargeAdapter;
import xyz.homapay.hampay.mobile.android.common.charge.ChargeAdapterModel;
import xyz.homapay.hampay.mobile.android.common.charge.RecyclerItemClickListener;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSelectChargeAmount;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.font.FontFace;

/**
 * Created by mohammad on 8/3/16.
 */

public class ChargeAmountChooserDialog {

    protected static MaterialDialog dlg;

    public static void show(Context ctx, List<String> items, int selectedIndex) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            try {
                ArrayList<ChargeAdapterModel> itemsAdapter = new ArrayList<>();
                for (int i = 0; i < items.size(); i++) {
                    ChargeAdapterModel model = new ChargeAdapterModel(i, "", items.get(i), i == selectedIndex);
                    itemsAdapter.add(model);
                }
                ChargeAdapter adapter = new ChargeAdapter(ctx, itemsAdapter);
                LinearLayoutManager manager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);

                dlg = new MaterialDialog.Builder(ctx)
                        .theme(Theme.LIGHT)
                        .customView(R.layout.dlg_charge_chooser, false)
                        .autoDismiss(true)
                        .typeface(FontFace.getInstance(ctx).getIRANSANS(), FontFace.getInstance(ctx).getIRANSANS())
                        .build();
                RecyclerView recyclerView = (RecyclerView) dlg.findViewById(R.id.main_view);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(adapter);
                recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(ctx, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        adapter.setSelected(position);
                        cancel();
                        EventBus.getDefault().post(new MessageSelectChargeAmount(((FacedTextView) view).getText().toString(), position));
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));
                dlg.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void cancel() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (dlg != null && dlg.isShowing())
                dlg.dismiss();
        });

    }

}
