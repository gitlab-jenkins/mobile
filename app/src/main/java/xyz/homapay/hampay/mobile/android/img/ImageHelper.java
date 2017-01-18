package xyz.homapay.hampay.mobile.android.img;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.FileInputStream;

import okhttp3.OkHttpClient;
import xyz.homapay.hampay.mobile.android.m.common.TrustedOkHttpClient;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;

/**
 * Created by mohammad on 12/30/16.
 */

public class ImageHelper {

    private static ImageHelper instance;
    private Context ctx;
    private OkHttpClient client;

    private ImageHelper(final Context ctx) {
        try {
            this.ctx = ctx;
            picassoMaker();
        } catch (Exception m) {
            m.printStackTrace();
        }
    }

    public static ImageHelper getInstance(final Context ctx) {
        if (instance == null)
            instance = new ImageHelper(ctx);
        return instance;
    }

    private static Bitmap decodeFile(@NonNull File f) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void imageLoader(final String imgId, @NonNull final ImageView img, @NonNull final int placeHolderResId) {
        if (imgId == null || imgId.equals(""))
            return;
        try {
            Picasso.with(ctx)
                    .load(urlMaker(imgId))
                    .placeholder(placeHolderResId)
                    .into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void circularImageLoader(final String imgId, @NonNull final ImageView img, @NonNull final int placeHolderResId) {
        if (imgId == null || imgId.equals(""))
            return;
        try {
            //since the target is weak refrence piccaso would GC the target when this method is done. in order to stop this we just set the target to view tag so it remains alive as long as view is alive
            img.setTag(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(ctx.getResources(), bitmap);
                    circularBitmapDrawable.setCircular(true);
                    img.post(() -> img.setImageDrawable(circularBitmapDrawable));
                    img.postInvalidate();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    img.post(() -> img.setImageResource(placeHolderResId));
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    img.post(() -> img.setImageResource(placeHolderResId));
                }
            });
            Picasso.with(ctx)
                    .load(urlMaker(imgId))
                    .placeholder(placeHolderResId)
                    .into((Target) img.getTag());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void imageLoader(final String imgId, @NonNull final ImageView img, @NonNull final int placeHolderResId, final Transformation transformation) {
        if (imgId == null || imgId.equals(""))
            return;
        try {
            Picasso.with(ctx)
                    .load(urlMaker(imgId))
                    .placeholder(placeHolderResId)
                    .transform(transformation)
                    .into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(final String imgId, final ImageView img) {
        if (imgId == null || imgId.equals(""))
            return;
        Picasso.with(ctx)
                .load(urlMaker(imgId))
                .into(img);
    }

    private void picassoMaker() {
        client = TrustedOkHttpClient.getTrustedOkHttpClient(new ModelLayerImpl(ctx), new PicassoInterceptor(ctx));
        Picasso picasso = new Picasso.Builder(ctx)
                .downloader(new OkHttp3Downloader(client))
                .build();
        Picasso.setSingletonInstance(picasso);
    }

    private String urlMaker(String imageId) {
        return Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + "retrieve/" + imageId;
    }

    public void invalidateImage(String imageId) {
        Picasso.with(ctx).invalidate(urlMaker(imageId));
    }

}
