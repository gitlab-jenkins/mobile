package xyz.homapay.hampay.mobile.android.component.cropper.cropwindow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import xyz.homapay.hampay.mobile.android.component.cropper.CropImageView;
import xyz.homapay.hampay.mobile.android.component.cropper.cropwindow.edge.Edge;
import xyz.homapay.hampay.mobile.android.component.cropper.cropwindow.handle.Handle;
import xyz.homapay.hampay.mobile.android.component.cropper.util.AspectRatioUtil;
import xyz.homapay.hampay.mobile.android.component.cropper.util.HandleUtil;
import xyz.homapay.hampay.mobile.android.component.cropper.util.PaintUtil;


public class CropOverlayView extends View {

    private static final int SNAP_RADIUS_DP = 6;

    private static final float DEFAULT_SHOW_GUIDELINES_LIMIT = 100;

    private static final float DEFAULT_CORNER_THICKNESS_DP = PaintUtil.getCornerThickness();

    private static final float DEFAULT_LINE_THICKNESS_DP = PaintUtil.getLineThickness();

    private static final float DEFAULT_CORNER_OFFSET_DP = (DEFAULT_CORNER_THICKNESS_DP / 2) - (DEFAULT_LINE_THICKNESS_DP / 2);

    private static final float DEFAULT_CORNER_EXTENSION_DP = DEFAULT_CORNER_THICKNESS_DP / 2
            + DEFAULT_CORNER_OFFSET_DP;

    private static final float DEFAULT_CORNER_LENGTH_DP = 20;

    private static final int GUIDELINES_ON_TOUCH = 1;

    private static final int GUIDELINES_ON = 2;

    private static RectF mRectF = new RectF();

    private Paint mBorderPaint;

    private Paint mGuidelinePaint;

    private Paint mCornerPaint;

    private Paint mBackgroundPaint;

    private Rect mBitmapRect;

    private float mHandleRadius;

    private float mSnapRadius;

    private Pair<Float, Float> mTouchOffset;

    private Handle mPressedHandle;

    private boolean mFixAspectRatio = CropImageView.DEFAULT_FIXED_ASPECT_RATIO;

    private int mAspectRatioX = CropImageView.DEFAULT_ASPECT_RATIO_X;

    private int mAspectRatioY = CropImageView.DEFAULT_ASPECT_RATIO_Y;

    private float mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;

    private int mGuidelines;

    private CropImageView.CropShape mCropShape;

    private boolean initializedCropWindow = false;

    private float mCornerExtension;

    private float mCornerOffset;

    private float mCornerLength;

    public CropOverlayView(Context context) {
        super(context);
        init(context);
    }

    public CropOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setBitmapRect(Rect bitmapRect) {
        mBitmapRect = bitmapRect;
        initCropWindow(mBitmapRect);
    }

    public void resetCropOverlayView() {

        if (initializedCropWindow) {
            initCropWindow(mBitmapRect);
            invalidate();
        }
    }


    public void setCropShape(CropImageView.CropShape cropShape) {
        mCropShape = cropShape;
        invalidate();
    }


    public void setGuidelines(int guidelines) {
        if (guidelines < 0 || guidelines > 2)
            throw new IllegalArgumentException("Guideline value must be set between 0 and 2. See documentation.");
        else {
            mGuidelines = guidelines;

            if (initializedCropWindow) {
                initCropWindow(mBitmapRect);
                invalidate();
            }
        }
    }

    public void setFixedAspectRatio(boolean fixAspectRatio) {
        mFixAspectRatio = fixAspectRatio;

        if (initializedCropWindow) {
            initCropWindow(mBitmapRect);
            invalidate();
        }
    }

    public void setAspectRatioX(int aspectRatioX) {
        if (aspectRatioX <= 0)
            throw new IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.");
        else {
            mAspectRatioX = aspectRatioX;
            mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;

            if (initializedCropWindow) {
                initCropWindow(mBitmapRect);
                invalidate();
            }
        }
    }


    public void setAspectRatioY(int aspectRatioY) {
        if (aspectRatioY <= 0)
            throw new IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.");
        else {
            mAspectRatioY = aspectRatioY;
            mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;

            if (initializedCropWindow) {
                initCropWindow(mBitmapRect);
                invalidate();
            }
        }
    }

    public void setInitialAttributeValues(int guidelines, boolean fixAspectRatio, int aspectRatioX, int aspectRatioY) {
        if (guidelines < 0 || guidelines > 2)
            throw new IllegalArgumentException("Guideline value must be set between 0 and 2. See documentation.");
        else
            mGuidelines = guidelines;

        mFixAspectRatio = fixAspectRatio;

        if (aspectRatioX <= 0)
            throw new IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.");
        else {
            mAspectRatioX = aspectRatioX;
            mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;
        }

        if (aspectRatioY <= 0)
            throw new IllegalArgumentException("Cannot set aspect ratio value to a number less than or equal to 0.");
        else {
            mAspectRatioY = aspectRatioY;
            mTargetAspectRatio = ((float) mAspectRatioX) / mAspectRatioY;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        initCropWindow(mBitmapRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        drawBackground(canvas, mBitmapRect);

        if (showGuidelines()) {
            if (mGuidelines == GUIDELINES_ON) {
                drawRuleOfThirdsGuidelines(canvas);
            } else if (mGuidelines == GUIDELINES_ON_TOUCH) {
                if (mPressedHandle != null)
                    drawRuleOfThirdsGuidelines(canvas);
            }
        }

        float w = mBorderPaint.getStrokeWidth();
        float l = Edge.LEFT.getCoordinate() + w;
        float t = Edge.TOP.getCoordinate() + w;
        float r = Edge.RIGHT.getCoordinate() - w;
        float b = Edge.BOTTOM.getCoordinate() - w;
        if (mCropShape == CropImageView.CropShape.RECTANGLE) {
            canvas.drawRect(l, t, r, b, mBorderPaint);
            drawCorners(canvas);
        } else {
            mRectF.set(l, t, r, b);
            canvas.drawOval(mRectF, mBorderPaint);
        }
    }

    @Override
    public boolean onTouchEvent(@SuppressWarnings("NullableProblems") MotionEvent event) {

        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                onActionDown(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp();
                return true;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event.getX(), event.getY());
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;

            default:
                return false;
        }
    }

    private void init(Context context) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        mHandleRadius = HandleUtil.getTargetRadius(context);

        mSnapRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                SNAP_RADIUS_DP,
                displayMetrics);

        mBorderPaint = PaintUtil.newBorderPaint(context);
        mGuidelinePaint = PaintUtil.newGuidelinePaint();
        mBackgroundPaint = PaintUtil.newBackgroundPaint(context);
        mCornerPaint = PaintUtil.newCornerPaint(context);

        mCornerOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_CORNER_OFFSET_DP,
                displayMetrics);
        mCornerExtension = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_CORNER_EXTENSION_DP,
                displayMetrics);
        mCornerLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_CORNER_LENGTH_DP,
                displayMetrics);

        mGuidelines = CropImageView.DEFAULT_GUIDELINES;
    }

    private void initCropWindow(Rect bitmapRect) {

        if (bitmapRect.width() == 0 || bitmapRect.height() == 0) {
            return;
        }

        if (!initializedCropWindow) {
            initializedCropWindow = true;
        }

        if (mFixAspectRatio
                && (bitmapRect.left != 0 || bitmapRect.right != 0
                || bitmapRect.top != 0 || bitmapRect.bottom != 0)) {

            if (AspectRatioUtil.calculateAspectRatio(bitmapRect) > mTargetAspectRatio) {

                Edge.TOP.setCoordinate(bitmapRect.top);
                Edge.BOTTOM.setCoordinate(bitmapRect.bottom);

                final float centerX = getWidth() / 2f;

                mTargetAspectRatio = (float) mAspectRatioX / mAspectRatioY;

                final float cropWidth = Math.max(Edge.MIN_CROP_LENGTH_PX,
                        AspectRatioUtil.calculateWidth(Edge.TOP.getCoordinate(),
                                Edge.BOTTOM.getCoordinate(),
                                mTargetAspectRatio));

                if (cropWidth == Edge.MIN_CROP_LENGTH_PX) {
                    mTargetAspectRatio = (Edge.MIN_CROP_LENGTH_PX) / (Edge.BOTTOM.getCoordinate() - Edge.TOP.getCoordinate());
                }

                final float halfCropWidth = cropWidth / 2f;
                Edge.LEFT.setCoordinate(centerX - halfCropWidth);
                Edge.RIGHT.setCoordinate(centerX + halfCropWidth);

            } else {

                Edge.LEFT.setCoordinate(bitmapRect.left);
                Edge.RIGHT.setCoordinate(bitmapRect.right);

                final float centerY = getHeight() / 2f;

                final float cropHeight = Math.max(Edge.MIN_CROP_LENGTH_PX,
                        AspectRatioUtil.calculateHeight(Edge.LEFT.getCoordinate(),
                                Edge.RIGHT.getCoordinate(),
                                mTargetAspectRatio));


                if (cropHeight == Edge.MIN_CROP_LENGTH_PX) {
                    mTargetAspectRatio = (Edge.RIGHT.getCoordinate() - Edge.LEFT.getCoordinate()) / Edge.MIN_CROP_LENGTH_PX;
                }

                final float halfCropHeight = cropHeight / 2f;
                Edge.TOP.setCoordinate(centerY - halfCropHeight);
                Edge.BOTTOM.setCoordinate(centerY + halfCropHeight);
            }

        } else {

            final float horizontalPadding = 0.1f * bitmapRect.width();
            final float verticalPadding = 0.1f * bitmapRect.height();

            Edge.LEFT.setCoordinate(bitmapRect.left + horizontalPadding);
            Edge.TOP.setCoordinate(bitmapRect.top + verticalPadding);
            Edge.RIGHT.setCoordinate(bitmapRect.right - horizontalPadding);
            Edge.BOTTOM.setCoordinate(bitmapRect.bottom - verticalPadding);
        }
    }

    public static boolean showGuidelines() {
        if ((Math.abs(Edge.LEFT.getCoordinate() - Edge.RIGHT.getCoordinate()) < DEFAULT_SHOW_GUIDELINES_LIMIT)
                || (Math.abs(Edge.TOP.getCoordinate() - Edge.BOTTOM.getCoordinate()) < DEFAULT_SHOW_GUIDELINES_LIMIT)) {
            return false;
        } else {
            return true;
        }
    }

    private void drawRuleOfThirdsGuidelines(Canvas canvas) {

        float w = mBorderPaint.getStrokeWidth();
        float l = Edge.LEFT.getCoordinate() + w;
        float t = Edge.TOP.getCoordinate() + w;
        float r = Edge.RIGHT.getCoordinate() - w;
        float b = Edge.BOTTOM.getCoordinate() - w;

        if (mCropShape == CropImageView.CropShape.OVAL) {
            l += 15 * mGuidelinePaint.getStrokeWidth();
            t += 15 * mGuidelinePaint.getStrokeWidth();
            r -= 15 * mGuidelinePaint.getStrokeWidth();
            b -= 15 * mGuidelinePaint.getStrokeWidth();
        }


        final float oneThirdCropWidth = Edge.getWidth() / 3;

        final float x1 = l + oneThirdCropWidth;
        canvas.drawLine(x1, t, x1, b, mGuidelinePaint);
        final float x2 = r - oneThirdCropWidth;
        canvas.drawLine(x2, t, x2, b, mGuidelinePaint);

        final float oneThirdCropHeight = Edge.getHeight() / 3;

        final float y1 = t + oneThirdCropHeight;
        canvas.drawLine(l, y1, r, y1, mGuidelinePaint);
        final float y2 = b - oneThirdCropHeight;
        canvas.drawLine(l, y2, r, y2, mGuidelinePaint);
    }

    private void drawBackground(Canvas canvas, Rect bitmapRect) {

        final float l = Edge.LEFT.getCoordinate();
        final float t = Edge.TOP.getCoordinate();
        final float r = Edge.RIGHT.getCoordinate();
        final float b = Edge.BOTTOM.getCoordinate();

        if (mCropShape == CropImageView.CropShape.RECTANGLE) {
            canvas.drawRect(bitmapRect.left, bitmapRect.top, bitmapRect.right, t, mBackgroundPaint);
            canvas.drawRect(bitmapRect.left, b, bitmapRect.right, bitmapRect.bottom, mBackgroundPaint);
            canvas.drawRect(bitmapRect.left, t, l, b, mBackgroundPaint);
            canvas.drawRect(r, t, bitmapRect.right, b, mBackgroundPaint);
        } else {
            Path circleSelectionPath = new Path();
            mRectF.set(l, t, r, b);
            circleSelectionPath.addOval(mRectF, Path.Direction.CW);
            canvas.clipPath(circleSelectionPath, Region.Op.XOR);
            canvas.drawRect(bitmapRect.left, bitmapRect.top, bitmapRect.right, bitmapRect.bottom, mBackgroundPaint);
            canvas.restore();
        }
    }

    private void drawCorners(Canvas canvas) {

        float w = mBorderPaint.getStrokeWidth();
        final float l = Edge.LEFT.getCoordinate() + w;
        final float t = Edge.TOP.getCoordinate() + w;
        final float r = Edge.RIGHT.getCoordinate() - w;
        final float b = Edge.BOTTOM.getCoordinate() - w;

        canvas.drawLine(l - mCornerOffset, t - mCornerExtension, l - mCornerOffset, t + mCornerLength, mCornerPaint);
        canvas.drawLine(l, t - mCornerOffset, l + mCornerLength, t - mCornerOffset, mCornerPaint);

        canvas.drawLine(r + mCornerOffset, t - mCornerExtension, r + mCornerOffset, t + mCornerLength, mCornerPaint);
        canvas.drawLine(r, t - mCornerOffset, r - mCornerLength, t - mCornerOffset, mCornerPaint);

        canvas.drawLine(l - mCornerOffset, b + mCornerExtension, l - mCornerOffset, b - mCornerLength, mCornerPaint);
        canvas.drawLine(l, b + mCornerOffset, l + mCornerLength, b + mCornerOffset, mCornerPaint);

        canvas.drawLine(r + mCornerOffset, b + mCornerExtension, r + mCornerOffset, b - mCornerLength, mCornerPaint);
        canvas.drawLine(r, b + mCornerOffset, r - mCornerLength, b + mCornerOffset, mCornerPaint);
    }

    private void onActionDown(float x, float y) {

        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

        mPressedHandle = HandleUtil.getPressedHandle(x, y, left, top, right, bottom, mHandleRadius);

        if (mPressedHandle == null) {
            return;
        }

        mTouchOffset = HandleUtil.getOffset(mPressedHandle, x, y, left, top, right, bottom);

        invalidate();
    }

    private void onActionUp() {

        if (mPressedHandle == null) {
            return;
        }

        mPressedHandle = null;

        invalidate();
    }

    private void onActionMove(float x, float y) {

        if (mPressedHandle == null) {
            return;
        }
        x += mTouchOffset.first;
        y += mTouchOffset.second;

        if (mFixAspectRatio) {
            mPressedHandle.updateCropWindow(x, y, mTargetAspectRatio, mBitmapRect, mSnapRadius);
        } else {
            mPressedHandle.updateCropWindow(x, y, mBitmapRect, mSnapRadius);
        }
        invalidate();
    }
}
