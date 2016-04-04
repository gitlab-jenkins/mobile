package xyz.homapay.hampay.mobile.android.component;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import xyz.homapay.hampay.mobile.android.R;

import java.util.HashMap;
import java.util.Map;



public class FacedTextView extends TextView {


    private static Map<String, Typeface> mTypefaces;

    public FacedTextView(final Context context) {
        this(context, null);
    }

    public FacedTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FacedTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        if (mTypefaces == null) {
            mTypefaces = new HashMap<String, Typeface>();
        }

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TypefaceTextView);
        if (array != null) {
            final String typefaceAssetPath = array.getString(
                    R.styleable.TypefaceTextView_customTypeface);

            if (typefaceAssetPath != null) {
                Typeface typeface = null;

                if (mTypefaces.containsKey(typefaceAssetPath)) {
                    typeface = mTypefaces.get(typefaceAssetPath);
                } else {
                    AssetManager assets = context.getAssets();
                    typeface = Typeface.createFromAsset(assets, typefaceAssetPath);
                    mTypefaces.put(typefaceAssetPath, typeface);
                }

                setTypeface(typeface);
            }
            array.recycle();
        }
    }

    @TargetApi(14)
    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }


}