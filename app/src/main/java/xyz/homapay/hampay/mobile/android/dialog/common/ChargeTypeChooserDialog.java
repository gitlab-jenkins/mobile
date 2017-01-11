package xyz.homapay.hampay.mobile.android.dialog.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.greenrobot.eventbus.EventBus;

import java.util.Collection;

import xyz.homapay.hampay.mobile.android.common.messages.MessageSelectChargeType;
import xyz.homapay.hampay.mobile.android.util.font.FontFace;

/**
 * Created by mohammad on 8/3/16.
 */

public class ChargeTypeChooserDialog {

    protected static MaterialDialog dlg;

    public static void show(Context ctx, Collection<String> items) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            try {
                dlg = new MaterialDialog.Builder(ctx)
                        .theme(Theme.LIGHT)
                        .items(items)
                        .autoDismiss(true)
                        .title("نوع شارژ خود را انتخاب کنید")
                        .typeface(FontFace.getInstance(ctx).getIRANSANS(), FontFace.getInstance(ctx).getIRANSANS())
                        .itemsCallbackSingleChoice(0, (dialog, itemView, which, text) -> {
                            EventBus.getDefault().post(new MessageSelectChargeType(which, text.toString()));
                            return true;
                        })
                        .build();
                dlg.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public static void cancel() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            try {
                if (dlg != null && dlg.isShowing())
                    dlg.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}
