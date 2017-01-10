package com.karinainc.ritmo.authorization.dialogs.progress;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.karinainc.ritmo.authorization.dialogs.common.Dialog;
import com.karinainc.ritmo.utils.font.FontFace;

/**
 * Created by mohammad on 8/3/16.
 */

public class ProgressDialog extends Dialog {

    private static ProgressDialog instance;

    private ProgressDialog(@NonNull final Context ctx) {
        this.ctx = ctx;
        dlg = new MaterialDialog.Builder(ctx)
                .theme(Theme.LIGHT)
                .autoDismiss(false)
                .cancelable(false)
                .title("در حال بارگذاری")
                .content("ریتمو در حال انجام درخواست شماست لطفا کمی صبر نمایید")
                .typeface(FontFace.getInstance(ctx).getIRANSANS(), FontFace.getInstance(ctx).getIRANSANS())
                .progress(true, 0)
                .positiveText("تایید")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        cancel();
                    }
                })
                .build();
    }

    public static ProgressDialog getInstance(@NonNull final Context ctx) {
        if (instance == null)
            instance = new ProgressDialog(ctx);
        return instance;
    }

}
