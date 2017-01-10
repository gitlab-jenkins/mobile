package xyz.homapay.hampay.mobile.android.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import xyz.homapay.hampay.mobile.android.util.font.FontFace;

/**
 * Created by mohammad on 8/3/16.
 */

public class ProgressDialog {

    protected static MaterialDialog dlg;

    public static void show(Context ctx) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            dlg = new MaterialDialog.Builder(ctx)
                    .theme(Theme.LIGHT)
                    .autoDismiss(false)
                    .cancelable(false)
                    .title("در حال بارگذاری")
                    .content("هم پی در حال انجام درخواست شماست لطفا کمی صبر نمایید")
                    .typeface(FontFace.getInstance(ctx).getIRANSANS(), FontFace.getInstance(ctx).getIRANSANS())
                    .progress(true, 0)
                    .positiveText("تایید")
                    .onPositive((dialog, which) -> cancel())
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
