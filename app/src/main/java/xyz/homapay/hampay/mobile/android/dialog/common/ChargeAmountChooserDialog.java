package xyz.homapay.hampay.mobile.android.dialog.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.greenrobot.eventbus.EventBus;

import java.util.Collection;

import xyz.homapay.hampay.mobile.android.common.messages.MessageSelectChargeAmount;
import xyz.homapay.hampay.mobile.android.util.font.FontFace;

/**
 * Created by mohammad on 8/3/16.
 */

public class ChargeAmountChooserDialog {

    protected static MaterialDialog dlg;

    public static void show(Context ctx, Collection<String> items) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            dlg = new MaterialDialog.Builder(ctx)
                    .theme(Theme.LIGHT)
                    .items(items)
                    .autoDismiss(true)
                    .title("مبلغ شارژ خود را انتخاب کنید")
                    .typeface(FontFace.getInstance(ctx).getIRANSANS(), FontFace.getInstance(ctx).getIRANSANS())
                    .itemsCallbackSingleChoice(0, (dialog, itemView, which, text) -> {
                        EventBus.getDefault().post(new MessageSelectChargeAmount(text.toString(), which));
                        return true;
                    })
                    .build();
            dlg.show();
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
