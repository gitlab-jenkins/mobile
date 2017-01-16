package xyz.homapay.hampay.mobile.android.util.font;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by mohammad on 1/22/14.
 */
public class FontFace {

    @Nullable
    private static FontFace instance = null;
    //
    @Nullable
    private Typeface AWESOME = null;
    @Nullable
    private Typeface OPENSANS = null;
    @Nullable
    private Typeface IRANSANS = null;

    @Nullable
    private Typeface VAZIR = null;

    /**
     * Private Constructor of this class
     *
     * @param ctx Context of application
     */
    private FontFace(@NonNull Context ctx) {
        try {
            AWESOME = Typeface.createFromAsset(ctx.getResources().getAssets(),
                    "fonts/awesome.ttf");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            OPENSANS = Typeface.createFromAsset(ctx.getResources().getAssets(),
                    "fonts/opensans.ttf");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            IRANSANS = Typeface.createFromAsset(ctx.getResources().getAssets(),
                    "fonts/iranSans.ttf");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            IRANSANS = Typeface.createFromAsset(ctx.getResources().getAssets(),
                    "fonts/vazir_regular.ttf");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Get instance method to get an static instance of class in singleton design pattern
     *
     * @param _ctx Context of application
     * @return
     */
    @Nullable
    public static FontFace getInstance(@NonNull Context _ctx) {
        if (instance == null) {
            instance = new FontFace(_ctx);
        }
        return instance;
    }

    @Nullable
    public Typeface getIRANSANS() {
        return IRANSANS;
    }

    /**
     * Get awesome font
     *
     * @return
     */
    @Nullable
    public Typeface getAWESOME() {
        return AWESOME;
    }

    /**
     * Get opensans font
     *
     * @return
     */
    @Nullable
    public Typeface getOPENSANS() {
        return OPENSANS;
    }

    @Nullable
    public Typeface getVAZIR() {
        return VAZIR;
    }
}
