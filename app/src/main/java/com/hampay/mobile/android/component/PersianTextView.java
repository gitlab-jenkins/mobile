package com.hampay.mobile.android.component;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class PersianTextView extends TextView {
    public PersianTextView(Context context) {
        super(context);
        setTypeface(context);
    }

    public PersianTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(context);
    }

    private void setTypeface(Context context) {
        this.setTypeface(TextUtils.getDefaultTypeface(context));
    }
}
