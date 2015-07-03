package com.hampay.mobile.android.component.edittext;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by amir on 6/18/15.
 */
public class FacedEditText extends EditText {

    public FacedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FacedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FacedEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/iran_sans.ttf");
            setTypeface(tf);
        }
    }
}
