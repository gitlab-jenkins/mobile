package xyz.homapay.hampay.mobile.android.component.cropper.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageViewUtil {


    public static Rect getBitmapRect(Bitmap bitmap, View view, ImageView.ScaleType scaleType) {

        final int bitmapWidth = bitmap.getWidth();
        final int bitmapHeight = bitmap.getHeight();
        final int viewWidth = view.getWidth();
        final int viewHeight = view.getHeight();

        switch (scaleType) {
            default:
            case CENTER_INSIDE:
                return getBitmapRectCenterInsideHelper(bitmapWidth, bitmapHeight, viewWidth, viewHeight);
            case FIT_CENTER:
                return getBitmapRectFitCenterHelper(bitmapWidth, bitmapHeight, viewWidth, viewHeight);
        }
    }


    public static Rect getBitmapRect(int bitmapWidth,
                                     int bitmapHeight,
                                     int viewWidth,
                                     int viewHeight, ImageView.ScaleType scaleType) {
        switch (scaleType) {
            default:
            case CENTER_INSIDE:
                return getBitmapRectCenterInsideHelper(bitmapWidth, bitmapHeight, viewWidth, viewHeight);
            case FIT_CENTER:
                return getBitmapRectFitCenterHelper(bitmapWidth, bitmapHeight, viewWidth, viewHeight);
        }
    }


    public static RotateBitmapResult rotateBitmapByExif(Context context, Bitmap bitmap, Uri uri) {
        try {
            File file = getFileFromUri(context, uri);
            if (file.exists()) {
                ExifInterface ei = new ExifInterface(file.getAbsolutePath());
                return rotateBitmapByExif(bitmap, ei);
            }
        } catch (Exception ignored) {
        }
        return new RotateBitmapResult(bitmap, 0);
    }


    public static RotateBitmapResult rotateBitmapByExif(Bitmap bitmap, ExifInterface exif) {
        int degrees = 0;
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degrees = 270;
                break;
        }
        if (degrees > 0) {
            bitmap = rotateBitmap(bitmap, degrees);
        }
        return new RotateBitmapResult(bitmap, degrees);
    }

    public static DecodeBitmapResult decodeSampledBitmap(Context context, Uri uri, int reqWidth, int reqHeight) {

        InputStream stream = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            stream = resolver.openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(stream, new Rect(0, 0, 0, 0), options);
            options.inJustDecodeBounds = false;

            options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);


            closeSafe(stream);
            stream = resolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream, new Rect(0, 0, 0, 0), options);

            return new DecodeBitmapResult(bitmap, options.inSampleSize);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load sampled bitmap", e);
        } finally {
            closeSafe(stream);
        }
    }

    public static DecodeBitmapResult decodeSampledBitmapRegion(Context context, Uri uri, Rect rect, int reqWidth, int reqHeight) {
        InputStream stream = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            stream = resolver.openInputStream(uri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = calculateInSampleSize(rect.width(), rect.height(), reqWidth, reqHeight);

            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(stream, false);
            Bitmap bitmap = decoder.decodeRegion(rect, options);

            return new DecodeBitmapResult(bitmap, options.inSampleSize);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load sampled bitmap", e);
        } finally {
            closeSafe(stream);
        }
    }


    public static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    public static File getFileFromUri(Context context, Uri uri) {

        File file = new File(uri.getPath());
        if (file.exists()) {
            return file;
        }

        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String realPath = cursor.getString(column_index);
            file = new File(realPath);
        } catch (Exception ignored) {
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return file;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        bitmap.recycle();
        return newBitmap;
    }


    public static void closeSafe(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static Rect getBitmapRectCenterInsideHelper(int bitmapWidth,
                                                        int bitmapHeight,
                                                        int viewWidth,
                                                        int viewHeight) {
        double resultWidth;
        double resultHeight;
        int resultX;
        int resultY;

        double viewToBitmapWidthRatio = Double.POSITIVE_INFINITY;
        double viewToBitmapHeightRatio = Double.POSITIVE_INFINITY;

        if (viewWidth < bitmapWidth) {
            viewToBitmapWidthRatio = (double) viewWidth / (double) bitmapWidth;
        }
        if (viewHeight < bitmapHeight) {
            viewToBitmapHeightRatio = (double) viewHeight / (double) bitmapHeight;
        }

        if (viewToBitmapWidthRatio != Double.POSITIVE_INFINITY || viewToBitmapHeightRatio != Double.POSITIVE_INFINITY) {
            if (viewToBitmapWidthRatio <= viewToBitmapHeightRatio) {
                resultWidth = viewWidth;
                resultHeight = (bitmapHeight * resultWidth / bitmapWidth);
            } else {
                resultHeight = viewHeight;
                resultWidth = (bitmapWidth * resultHeight / bitmapHeight);
            }
        }

        else {
            resultHeight = bitmapHeight;
            resultWidth = bitmapWidth;
        }

        if (resultWidth == viewWidth) {
            resultX = 0;
            resultY = (int) Math.round((viewHeight - resultHeight) / 2);
        } else if (resultHeight == viewHeight) {
            resultX = (int) Math.round((viewWidth - resultWidth) / 2);
            resultY = 0;
        } else {
            resultX = (int) Math.round((viewWidth - resultWidth) / 2);
            resultY = (int) Math.round((viewHeight - resultHeight) / 2);
        }

        final Rect result = new Rect(resultX,
                resultY,
                resultX + (int) Math.ceil(resultWidth),
                resultY + (int) Math.ceil(resultHeight));

        return result;
    }


    private static Rect getBitmapRectFitCenterHelper(int bitmapWidth, int bitmapHeight, int viewWidth, int viewHeight) {
        double resultWidth;
        double resultHeight;
        int resultX;
        int resultY;

        double viewToBitmapWidthRatio = (double) viewWidth / bitmapWidth;
        double viewToBitmapHeightRatio = (double) viewHeight / bitmapHeight;

        if (viewToBitmapWidthRatio <= viewToBitmapHeightRatio) {
            resultWidth = viewWidth;
            resultHeight = (bitmapHeight * resultWidth / bitmapWidth);
        } else {
            resultHeight = viewHeight;
            resultWidth = (bitmapWidth * resultHeight / bitmapHeight);
        }


        if (resultWidth == viewWidth) {
            resultX = 0;
            resultY = (int) Math.round((viewHeight - resultHeight) / 2);
        } else if (resultHeight == viewHeight) {
            resultX = (int) Math.round((viewWidth - resultWidth) / 2);
            resultY = 0;
        } else {
            resultX = (int) Math.round((viewWidth - resultWidth) / 2);
            resultY = (int) Math.round((viewHeight - resultHeight) / 2);
        }

        final Rect result = new Rect(resultX,
                resultY,
                resultX + (int) Math.ceil(resultWidth),
                resultY + (int) Math.ceil(resultHeight));

        return result;
    }


    public static final class DecodeBitmapResult {


        public final Bitmap bitmap;

        public final int sampleSize;

        DecodeBitmapResult(Bitmap bitmap, int sampleSize) {
            this.sampleSize = sampleSize;
            this.bitmap = bitmap;
        }
    }

    public static final class RotateBitmapResult {


        public final Bitmap bitmap;

        public final int degrees;

        RotateBitmapResult(Bitmap bitmap, int degrees) {
            this.bitmap = bitmap;
            this.degrees = degrees;
        }
    }
}