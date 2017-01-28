package xyz.homapay.hampay.mobile.android.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import xyz.homapay.hampay.mobile.android.util.font.FontFace;

/**
 * Created by mohammad on 1/22/17.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {
    public CustomTextView(Context context) {
        super(context);
        init(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        setTypeface(FontFace.getInstance(ctx).getVAZIR_BOLD());
    }
}
