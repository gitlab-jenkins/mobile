package xyz.homapay.hampay.mobile.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

/**
 * Created by amir on 3/7/16.
 */
public class HamPayCustomDialog extends Dialog {

    public HamPayCustomDialog(View view, Context context, int themeResId) {
        super(context, themeResId);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(view);
        setTitle(null);
        setCanceledOnTouchOutside(false);


    }



}
