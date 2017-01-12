package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import xyz.homapay.hampay.mobile.android.util.font.FontFace;

/**
 * Created by mohammad on 12/2/2016 AD.
 */

public class Dialoger {

    public static final class NETWORK {

        private static MaterialDialog dlg;

        public static final void showErrorNetworkDialog(Context ctx) {
            try {
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        dlg = new MaterialDialog.Builder(ctx)
                                .theme(Theme.LIGHT)
                                .typeface(FontFace.getInstance(ctx).getIRANSANS(), FontFace.getInstance(ctx).getIRANSANS())
                                .title("عدم دسترسی به شبکه")
                                .content("به نظر می رسد دسترسی شما به شبکه قطع می باشد می توانید از طریق تنظیمات موضوع را بررسی کنید")
                                .positiveText("تنظیمات شبکه")
                                .negativeText("مشاهده آفلاین")
                                .onPositive((dialog, which) -> {
                                    try {
                                        ctx.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                })
                                .onNegative((dialog, which) -> {
                                    try {
                                        cancelErrorNetworkDialog();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                })
                                .build();
                        dlg.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static final void cancelErrorNetworkDialog() {
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    if (dlg != null)
                        dlg.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }

    public static final class GENERAL {
        private static MaterialDialog dlg;
        private static MaterialDialog dlgCustom;

        public static void show(final Context ctx) {
            dlg = new MaterialDialog.Builder(ctx)
                    .typeface(FontFace.getInstance(ctx).getIRANSANS(), FontFace.getInstance(ctx).getIRANSANS())
                    .title("هم پی")
                    .titleColor(Color.GRAY)
                    .theme(Theme.LIGHT)
                    .content("درخواست شما با موفقیت به پایان رسید")
                    .positiveText("تایید")
                    .onPositive((dialog, which) -> {
                        if (dlg != null)
                            dlg.cancel();
                    })
                    .cancelable(false)
                    .build();
        }

        public static void show(final Context ctx, final String title, final String text) {
            dlgCustom = new MaterialDialog.Builder(ctx)
                    .typeface(FontFace.getInstance(ctx).getIRANSANS(), FontFace.getInstance(ctx).getIRANSANS())
                    .title(title)
                    .titleColor(Color.GRAY)
                    .theme(Theme.LIGHT)
                    .content(text)
                    .positiveText("تایید")
                    .onPositive((dialog, which) -> {
                        if (dlgCustom != null)
                            dlgCustom.dismiss();
                    })
                    .cancelable(true)
                    .show();
        }
    }

}
