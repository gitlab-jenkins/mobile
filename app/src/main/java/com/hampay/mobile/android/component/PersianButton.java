package com.hampay.mobile.android.component;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.hampay.mobile.android.component.TextUtils;


public class PersianButton extends Button {

    public PersianButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(TextUtils.getDefaultTypeface(context));
    }
}

