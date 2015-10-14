package xyz.homapay.hampay.mobile.android.component.cropper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.cropper.cropwindow.CropOverlayView;
import xyz.homapay.hampay.mobile.android.component.cropper.cropwindow.edge.Edge;
import xyz.homapay.hampay.mobile.android.component.cropper.util.ImageViewUtil;


public class CropImageView extends FrameLayout {


    private static final Rect EMPTY_RECT = new Rect();

    public static final int DEFAULT_GUIDELINES = 1;

    public static final boolean DEFAULT_FIXED_ASPECT_RATIO = false;

    public static final int DEFAULT_ASPECT_RATIO_X = 1;

    public static final int DEFAULT_ASPECT_RATIO_Y = 1;

    public static final int DEFAULT_SCALE_TYPE_INDEX = 0;

    public static final int DEFAULT_CROP_SHAPE_INDEX = 0;

    private static final int DEFAULT_IMAGE_RESOURCE = 0;

    private static final ImageView.ScaleType[] VALID_SCALE_TYPES = new ImageView.ScaleType[]{ImageView.ScaleType.CENTER_INSIDE, ImageView.ScaleType.FIT_CENTER};

    private static final CropShape[] VALID_CROP_SHAPES = new CropShape[]{CropShape.RECTANGLE, CropShape.OVAL};

    private static final String DEGREES_ROTATED = "DEGREES_ROTATED";

    private ImageView mImageView;

    private CropOverlayView mCropOverlayView;

    private Bitmap mBitmap;

    private int mDegreesRotated = 0;

    private int mLayoutWidth;

    private int mLayoutHeight;


    private int mGuidelines = DEFAULT_GUIDELINES;

    private boolean mFixAspectRatio = DEFAULT_FIXED_ASPECT_RATIO;

    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_X;

    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_Y;

    private int mImageResource = DEFAULT_IMAGE_RESOURCE;

    private ImageView.ScaleType mScaleType = VALID_SCALE_TYPES[DEFAULT_SCALE_TYPE_INDEX];

    private CropShape mCropShape;

    private Uri mLoadedImageUri;

    private int mLoadedSampleSize = 1;

    public CropImageView(Context context) {
        super(context);
        init(context);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CropImageView, 0, 0);
        try {
            mGuidelines = ta.getInteger(R.styleable.CropImageView_guidelines, DEFAULT_GUIDELINES);
            mFixAspectRatio = ta.getBoolean(R.styleable.CropImageView_fixAspectRatio, DEFAULT_FIXED_ASPECT_RATIO);
            mAspectRatioX = ta.getInteger(R.styleable.CropImageView_aspectRatioX, DEFAULT_ASPECT_RATIO_X);
            mAspectRatioY = ta.getInteger(R.styleable.CropImageView_aspectRatioY, DEFAULT_ASPECT_RATIO_Y);
            mImageResource = ta.getResourceId(R.styleable.CropImageView_imageResource, DEFAULT_IMAGE_RESOURCE);
            mScaleType = VALID_SCALE_TYPES[ta.getInt(R.styleable.CropImageView_scaleType, DEFAULT_SCALE_TYPE_INDEX)];
            mCropShape = VALID_CROP_SHAPES[ta.getInt(R.styleable.CropImageView_cropShape, DEFAULT_CROP_SHAPE_INDEX)];
        } finally {
            ta.recycle();
        }

        init(context);
    }

    public ImageView.ScaleType getScaleType() {
        return mScaleType;
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        mScaleType = scaleType;
        if (mImageView != null)
            mImageView.setScaleType(mScaleType);
    }

    public CropShape getCropShape() {
        return mCropShape;
    }

    public void setCropShape(CropShape cropShape) {
        if (cropShape != mCropShape) {
            mCropShape = cropShape;
            mCropOverlayView.setCropShape(cropShape);
        }
    }

    public void setFixedAspectRatio(boolean fixAspectRatio) {
        mCropOverlayView.setFixedAspectRatio(fixAspectRatio);
    }

    public void setGuidelines(int guidelines) {
        mCropOverlayView.setGuidelines(guidelines);
    }


    public void setAspectRatio(int aspectRatioX, int aspectRatioY) {
        mAspectRatioX = aspectRatioX;
        mCropOverlayView.setAspectRatioX(mAspectRatioX);

        mAspectRatioY = aspectRatioY;
        mCropOverlayView.setAspectRatioY(mAspectRatioY);
    }

    public int getImageResource() {
        return mImageResource;
    }


    public void setImageBitmap(Bitmap bitmap) {
        if(mBitmap == bitmap) {
            return;
        }

        if (mBitmap != null && (mImageResource > 0 || mLoadedImageUri != null)) {
            mBitmap.recycle();
        }

        mImageResource = 0;
        mLoadedImageUri = null;
        mLoadedSampleSize = 1;
        mDegreesRotated = 0;

        mBitmap = bitmap;
        mImageView.setImageBitmap(mBitmap);
        if (mCropOverlayView != null) {
            mCropOverlayView.resetCropOverlayView();
        }
    }

    public void setImageBitmap(Bitmap bitmap, ExifInterface exif) {
        if (bitmap != null && exif != null) {
            ImageViewUtil.RotateBitmapResult result = ImageViewUtil.rotateBitmapByExif(bitmap, exif);
            bitmap = result.bitmap;
            mDegreesRotated = result.degrees;
        }
        setImageBitmap(bitmap);
    }

    public void setImageResource(int resId) {
        if (resId != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
            setImageBitmap(bitmap);

            mImageResource = resId;
        }
    }

    public void setImageUri(Uri uri) {
        if (uri != null) {

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            double densityAdj = metrics.density > 1 ? 1 / metrics.density : 1;

            int width = (int) (metrics.widthPixels * densityAdj);
            int height = (int) (metrics.heightPixels * densityAdj);
            ImageViewUtil.DecodeBitmapResult decodeResult =
                    ImageViewUtil.decodeSampledBitmap(getContext(), uri, width, height);

            ImageViewUtil.RotateBitmapResult rotateResult =
                    ImageViewUtil.rotateBitmapByExif(getContext(), decodeResult.bitmap, uri);

            setImageBitmap(rotateResult.bitmap);

            mLoadedImageUri = uri;
            mLoadedSampleSize = decodeResult.sampleSize;
            mDegreesRotated = rotateResult.degrees;
        }
    }

    public Rect getActualCropRect() {
        if (mBitmap != null) {
            final Rect displayedImageRect = ImageViewUtil.getBitmapRect(mBitmap, mImageView, mScaleType);

            final float actualImageWidth = mBitmap.getWidth();
            final float displayedImageWidth = displayedImageRect.width();
            final float scaleFactorWidth = actualImageWidth / displayedImageWidth;

            final float actualImageHeight = mBitmap.getHeight();
            final float displayedImageHeight = displayedImageRect.height();
            final float scaleFactorHeight = actualImageHeight / displayedImageHeight;

            final float displayedCropLeft = Edge.LEFT.getCoordinate() - displayedImageRect.left;
            final float displayedCropTop = Edge.TOP.getCoordinate() - displayedImageRect.top;
            final float displayedCropWidth = Edge.getWidth();
            final float displayedCropHeight = Edge.getHeight();

            float actualCropLeft = displayedCropLeft * scaleFactorWidth;
            float actualCropTop = displayedCropTop * scaleFactorHeight;
            float actualCropRight = actualCropLeft + displayedCropWidth * scaleFactorWidth;
            float actualCropBottom = actualCropTop + displayedCropHeight * scaleFactorHeight;

            actualCropLeft = Math.max(0f, actualCropLeft);
            actualCropTop = Math.max(0f, actualCropTop);
            actualCropRight = Math.min(mBitmap.getWidth(), actualCropRight);
            actualCropBottom = Math.min(mBitmap.getHeight(), actualCropBottom);

            return new Rect((int) actualCropLeft, (int) actualCropTop, (int) actualCropRight, (int) actualCropBottom);
        } else {
            return null;
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public Rect getActualCropRectNoRotation() {
        if (mBitmap != null) {
            Rect rect = getActualCropRect();
            int rotateSide = mDegreesRotated / 90;
            if (rotateSide == 1) {
                rect.set(rect.top, mBitmap.getWidth() - rect.right, rect.bottom, mBitmap.getWidth() - rect.left);
            } else if (rotateSide == 2) {
                rect.set(mBitmap.getWidth() - rect.right, mBitmap.getHeight() - rect.bottom, mBitmap.getWidth() - rect.left, mBitmap.getHeight() - rect.top);
            } else if (rotateSide == 3) {
                rect.set(mBitmap.getHeight() - rect.bottom, rect.left, mBitmap.getHeight() - rect.top, rect.right);
            }
            rect.set(rect.left * mLoadedSampleSize, rect.top * mLoadedSampleSize, rect.right * mLoadedSampleSize, rect.bottom * mLoadedSampleSize);
            return rect;
        } else {
            return null;
        }
    }


    public void rotateImage(int degrees) {
        if (mBitmap != null) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degrees);
            Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
            setImageBitmap(bitmap);

            mDegreesRotated += degrees;
            mDegreesRotated = mDegreesRotated % 360;
        }
    }


    public Bitmap getCroppedImage() {
        return getCroppedImage(0, 0);
    }

    public Bitmap getCroppedImage(int reqWidth, int reqHeight) {
        if (mBitmap != null) {
            if (mLoadedImageUri != null && mLoadedSampleSize > 1) {
                Rect rect = getActualCropRectNoRotation();
                reqWidth = reqWidth > 0 ? reqWidth : rect.width();
                reqHeight = reqHeight > 0 ? reqHeight : rect.height();
                ImageViewUtil.DecodeBitmapResult result =
                        ImageViewUtil.decodeSampledBitmapRegion(getContext(), mLoadedImageUri, rect, reqWidth, reqHeight);

                Bitmap bitmap = result.bitmap;
                if (mDegreesRotated > 0) {
                    bitmap = ImageViewUtil.rotateBitmap(bitmap, mDegreesRotated);
                }

                return bitmap;
            } else {
                Rect rect = getActualCropRect();
                return Bitmap.createBitmap(mBitmap, rect.left, rect.top, rect.width(), rect.height());
            }
        } else {
            return null;
        }
    }

    public Bitmap getCroppedOvalImage() {
        if (mBitmap != null) {
            Bitmap cropped = getCroppedImage();

            int width = cropped.getWidth();
            int height = cropped.getHeight();
            Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);

            RectF rect = new RectF(0, 0, width, height);
            canvas.drawOval(rect, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(cropped, 0, 0, paint);

            return output;
        } else {
            return null;
        }
    }


    @Override
    public Parcelable onSaveInstanceState() {

        final Bundle bundle = new Bundle();

        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt(DEGREES_ROTATED, mDegreesRotated);

        return bundle;

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {

            final Bundle bundle = (Bundle) state;

            if (mBitmap != null) {
                mDegreesRotated = bundle.getInt(DEGREES_ROTATED);
                int tempDegrees = mDegreesRotated;
                rotateImage(mDegreesRotated);
                mDegreesRotated = tempDegrees;
            }

            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));

        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (mBitmap != null) {
            final Rect bitmapRect = ImageViewUtil.getBitmapRect(mBitmap, this, mScaleType);
            mCropOverlayView.setBitmapRect(bitmapRect);
        } else {
            mCropOverlayView.setBitmapRect(EMPTY_RECT);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (mBitmap != null) {

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            if (heightSize == 0) {
                heightSize = mBitmap.getHeight();
            }

            int desiredWidth;
            int desiredHeight;

            double viewToBitmapWidthRatio = Double.POSITIVE_INFINITY;
            double viewToBitmapHeightRatio = Double.POSITIVE_INFINITY;

            if (widthSize < mBitmap.getWidth()) {
                viewToBitmapWidthRatio = (double) widthSize / (double) mBitmap.getWidth();
            }
            if (heightSize < mBitmap.getHeight()) {
                viewToBitmapHeightRatio = (double) heightSize / (double) mBitmap.getHeight();
            }
            if (viewToBitmapWidthRatio != Double.POSITIVE_INFINITY || viewToBitmapHeightRatio != Double.POSITIVE_INFINITY) {
                if (viewToBitmapWidthRatio <= viewToBitmapHeightRatio) {
                    desiredWidth = widthSize;
                    desiredHeight = (int) (mBitmap.getHeight() * viewToBitmapWidthRatio);
                } else {
                    desiredHeight = heightSize;
                    desiredWidth = (int) (mBitmap.getWidth() * viewToBitmapHeightRatio);
                }
            }

            else {
                desiredWidth = mBitmap.getWidth();
                desiredHeight = mBitmap.getHeight();
            }

            int width = getOnMeasureSpec(widthMode, widthSize, desiredWidth);
            int height = getOnMeasureSpec(heightMode, heightSize, desiredHeight);

            mLayoutWidth = width;
            mLayoutHeight = height;

            final Rect bitmapRect = ImageViewUtil.getBitmapRect(mBitmap.getWidth(),
                    mBitmap.getHeight(),
                    mLayoutWidth,
                    mLayoutHeight,
                    mScaleType);
            mCropOverlayView.setBitmapRect(bitmapRect);

            setMeasuredDimension(mLayoutWidth, mLayoutHeight);

        } else {

            mCropOverlayView.setBitmapRect(EMPTY_RECT);
            setMeasuredDimension(widthSize, heightSize);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);

        if (mLayoutWidth > 0 && mLayoutHeight > 0) {
            final ViewGroup.LayoutParams origparams = this.getLayoutParams();
            origparams.width = mLayoutWidth;
            origparams.height = mLayoutHeight;
            setLayoutParams(origparams);
        }
    }

    private void init(Context context) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        final View v = inflater.inflate(R.layout.crop_image_view, this, true);

        mImageView = (ImageView) v.findViewById(R.id.ImageView_image);
        mImageView.setScaleType(mScaleType);

        setImageResource(mImageResource);

        mCropOverlayView = (CropOverlayView) v.findViewById(R.id.CropOverlayView);
        mCropOverlayView.setInitialAttributeValues(mGuidelines, mFixAspectRatio, mAspectRatioX, mAspectRatioY);
        mCropOverlayView.setCropShape(mCropShape);
    }

    private static int getOnMeasureSpec(int measureSpecMode, int measureSpecSize, int desiredSize) {

        int spec;
        if (measureSpecMode == MeasureSpec.EXACTLY) {
            spec = measureSpecSize;
        } else if (measureSpecMode == MeasureSpec.AT_MOST) {
            spec = Math.min(desiredSize, measureSpecSize);
        } else {
            spec = desiredSize;
        }

        return spec;
    }

    public static enum CropShape {
        RECTANGLE,
        OVAL
    }
}
