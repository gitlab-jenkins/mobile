package xyz.homapay.hampay.mobile.android.component.circleimageview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import xyz.homapay.hampay.mobile.android.R;

/**
 * Created by amir on 10/13/15.
 */
public class CircleImageView extends ImageView {

    private static final String TAG = CircleImageView.class.getSimpleName();


    private static final boolean SHADOW_ENABLED = false;
    private static final float SHADOW_RADIUS = 4f;
    private static final float SHADOW_DX = 0f;
    private static final float SHADOW_DY = 2f;
    private static final int SHADOW_COLOR = Color.BLACK;

    private boolean hasBorder;
    private boolean hasSelector;
    private boolean isSelected;
    private int borderWidth;
    private int canvasSize;
    private int selectorStrokeWidth;

    private boolean shadowEnabled;
    private float shadowRadius;
    private float shadowDx;
    private float shadowDy;
    private int shadowColor;

    private BitmapShader shader;
    private Bitmap image;
    private Paint paint;
    private Paint paintBorder;
    private Paint paintSelectorBorder;
    private ColorFilter selectorFilter;

    public CircleImageView(Context context) {
        this(context, null, R.styleable.CircularImageViewStyle_circularImageViewDefault);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.styleable.CircularImageViewStyle_circularImageViewDefault);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
        paintBorder.setStyle(Paint.Style.STROKE);
        paintSelectorBorder = new Paint();
        paintSelectorBorder.setAntiAlias(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            setLayerType(LAYER_TYPE_SOFTWARE, null);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView, defStyle, 0);

        hasBorder = attributes.getBoolean(R.styleable.CircularImageView_civ_border, false);
        hasSelector = attributes.getBoolean(R.styleable.CircularImageView_civ_selector, false);
        shadowEnabled = attributes.getBoolean(R.styleable.CircularImageView_civ_shadow, SHADOW_ENABLED);

        if(hasBorder) {
            int defaultBorderSize = (int) (2 * context.getResources().getDisplayMetrics().density + 0.5f);
            setBorderWidth(attributes.getDimensionPixelOffset(R.styleable.CircularImageView_civ_borderWidth, defaultBorderSize));
            setBorderColor(attributes.getColor(R.styleable.CircularImageView_civ_borderColor, Color.WHITE));
        }

        if(hasSelector) {
            int defaultSelectorSize = (int) (2 * context.getResources().getDisplayMetrics().density + 0.5f);
            setSelectorColor(attributes.getColor(R.styleable.CircularImageView_civ_selectorColor, Color.TRANSPARENT));
            setSelectorStrokeWidth(attributes.getDimensionPixelOffset(R.styleable.CircularImageView_civ_selectorStrokeWidth, defaultSelectorSize));
            setSelectorStrokeColor(attributes.getColor(R.styleable.CircularImageView_civ_selectorStrokeColor, Color.BLUE));
        }

        if(shadowEnabled) {
            shadowRadius = attributes.getFloat(R.styleable.CircularImageView_civ_shadowRadius, SHADOW_RADIUS);
            shadowDx = attributes.getFloat(R.styleable.CircularImageView_civ_shadowDx, SHADOW_DX);
            shadowDy = attributes.getFloat(R.styleable.CircularImageView_civ_shadowDy, SHADOW_DY);
            shadowColor = attributes.getColor(R.styleable.CircularImageView_civ_shadowColor, SHADOW_COLOR);
            setShadowEnabled(true);
        }

        attributes.recycle();
    }


    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        if(paintBorder != null)
            paintBorder.setStrokeWidth(borderWidth);
        requestLayout();
        invalidate();
    }

    public void setBorderColor(int borderColor) {
        if (paintBorder != null)
            paintBorder.setColor(borderColor);
        this.invalidate();
    }


    public void setSelectorColor(int selectorColor) {
        this.selectorFilter = new PorterDuffColorFilter(selectorColor, PorterDuff.Mode.SRC_ATOP);
        this.invalidate();
    }


    public void setSelectorStrokeWidth(int selectorStrokeWidth) {
        this.selectorStrokeWidth = selectorStrokeWidth;
        this.requestLayout();
        this.invalidate();
    }


    public void setSelectorStrokeColor(int selectorStrokeColor) {
        if (paintSelectorBorder != null)
            paintSelectorBorder.setColor(selectorStrokeColor);
        this.invalidate();
    }


    public void setShadowEnabled(boolean enabled) {
        shadowEnabled = enabled;
        updateShadow();
    }


    public void setShadow(float radius, float dx, float dy, int color) {
        shadowRadius = radius;
        shadowDx = dx;
        shadowDy = dy;
        shadowColor = color;
        updateShadow();
    }

    @Override
    public void onDraw(Canvas canvas) {

        if(image == null)
            return;

        if(image.getHeight() == 0 || image.getWidth() == 0)
            return;

        int oldCanvasSize = canvasSize;
        canvasSize = getWidth() < getHeight() ? getWidth() : getHeight();
        if(oldCanvasSize != canvasSize)
            updateBitmapShader();

        paint.setShader(shader);

        int outerWidth = 0;

        int center = canvasSize / 2;


        if(hasSelector && isSelected) {
            outerWidth = selectorStrokeWidth;
            center = (canvasSize - (outerWidth * 2)) / 2;

            paint.setColorFilter(selectorFilter);
            canvas.drawCircle(center + outerWidth, center + outerWidth, ((canvasSize - (outerWidth * 2)) / 2) + outerWidth - 4.0f, paintSelectorBorder);
        }
        else if(hasBorder) {
            outerWidth = borderWidth;
            center = (canvasSize - (outerWidth * 2)) / 2;

            paint.setColorFilter(null);
            RectF rekt = new RectF(0 + outerWidth / 2, 0 + outerWidth / 2, canvasSize - outerWidth / 2, canvasSize - outerWidth / 2);
            canvas.drawArc(rekt, 360, 360, false, paintBorder);
        }
        else
            paint.setColorFilter(null);

        canvas.drawCircle(center + outerWidth, center + outerWidth, ((canvasSize - (outerWidth * 2)) / 2), paint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if(!this.isClickable()) {
            this.isSelected = false;
            return super.onTouchEvent(event);
        }


        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.isSelected = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_SCROLL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                this.isSelected = false;
                break;
        }

        this.invalidate();
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);

        image = drawableToBitmap(getDrawable());
        if(canvasSize > 0)
            updateBitmapShader();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);

        image = drawableToBitmap(getDrawable());
        if(canvasSize > 0)
            updateBitmapShader();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        image = drawableToBitmap(getDrawable());
        if(canvasSize > 0)
            updateBitmapShader();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        image = bm;
        if(canvasSize > 0)
            updateBitmapShader();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {

            result = specSize;
        }
        else if (specMode == MeasureSpec.AT_MOST) {

            result = specSize;
        }
        else {

            result = canvasSize;
        }

        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {

            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {

            result = specSize;
        } else {

            result = canvasSize;
        }

        return (result + 2);
    }

    private void updateShadow() {
        float radius = shadowEnabled ? shadowRadius : 0;

        paintBorder.setShadowLayer(radius, shadowDx, shadowDy, shadowColor);
        paintSelectorBorder.setShadowLayer(radius, shadowDx, shadowDy, shadowColor);
    }


    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null)
            return null;
        else if (drawable instanceof BitmapDrawable) {
            Log.i(TAG, "Bitmap drawable!");
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();

        if (!(intrinsicWidth > 0 && intrinsicHeight > 0))
            return null;

        try {
            Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Encountered OutOfMemoryError while generating bitmap!");
            return null;
        }
    }

    public void setIconModeEnabled(boolean e) {}


    public void updateBitmapShader() {
        if (image == null)
            return;

        shader = new BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        if(canvasSize != image.getWidth() || canvasSize != image.getHeight()) {
            Matrix matrix = new Matrix();
            float scale = (float) canvasSize / (float) image.getWidth();
            matrix.setScale(scale, scale);
            shader.setLocalMatrix(matrix);
        }
    }


    public boolean isSelected() {
        return this.isSelected;
    }
}
