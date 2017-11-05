package com.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.graphics.Xfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ecommarceapp.R;

import java.util.HashSet;
import java.util.Set;

public class CircleLayout extends ViewGroup {
    /**
     * Default backoff multiplier for image requests
     */
    public static final float IMAGE_BACKOFF_MULT = 2f;
    public static final int LAYOUT_NORMAL = 1;
    public static final int LAYOUT_PIE = 2;
    private float mAngleOffset;
    private float mAngleRange;
    private RectF mBounds;
    private boolean mCached;
    private Canvas mCachedCanvas;
    private Paint mCirclePaint;
    private Set<View> mDirtyViews;
    private Paint mDividerPaint;
    private float mDividerWidth;
    private Bitmap mDrawingCache;
    private Bitmap mDst;
    private Canvas mDstCanvas;
    private Drawable mInnerCircle;
    private int mInnerRadius;
    private int mLayoutMode;
    private View mMotionTarget;
    private Bitmap mSrc;
    private Canvas mSrcCanvas;
    private Xfermode mXfer;
    private Paint mXferPaint;

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams {
        private float endAngle;
        private float startAngle;
        public float weight = 1.0f;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }

    public CircleLayout(Context context) {
        this(context, null);
    }

    @SuppressLint({"NewApi"})
    public CircleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mLayoutMode = 1;
        this.mBounds = new RectF();
        this.mDirtyViews = new HashSet();
        this.mCached = false;

        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleLayout, 0, 0);
        try {
            int dividerColor = a.getColor(R.styleable.CircleLayout_sliceDivider, ResourcesCompat.getColor(getResources(), android.R.color.darker_gray, null));
            this.mInnerCircle = a.getDrawable(R.styleable.CircleLayout_innerCircle);
            if (this.mInnerCircle instanceof ColorDrawable) {
                this.mCirclePaint.setColor(a.getColor(R.styleable.CircleLayout_innerCircle, ResourcesCompat.getColor(getResources(), android.R.color.white, null)));
            }
            this.mDividerPaint.setColor(dividerColor);
            this.mAngleOffset = a.getFloat(R.styleable.CircleLayout_angleOffset, 90.0f);
            this.mAngleRange = a.getFloat(R.styleable.CircleLayout_angleRange, 360.0f);
            this.mDividerWidth = (float) a.getDimensionPixelSize(R.styleable.CircleLayout_dividerWidth, 1);
            this.mInnerRadius = a.getDimensionPixelSize(R.styleable.CircleLayout_innerRadius, 80);
            this.mLayoutMode = a.getColor(R.styleable.CircleLayout_layoutMode, LAYOUT_PIE);
            this.mDividerPaint.setStrokeWidth(this.mDividerWidth);
            mXfer = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
            mXferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            if (VERSION.SDK_INT >= 11) {
                setLayerType(LAYER_TYPE_SOFTWARE, null);
            }
        } finally {
            a.recycle();
        }
    }

    public int getLayoutMode() {
        return this.mLayoutMode;
    }

    public void setLayoutMode(int mode) {
        this.mLayoutMode = mode;
        requestLayout();
        invalidate();
    }

    public int getRadius() {
        int width = getWidth();
        int height = getHeight();
        return (int) (((width > height ? (float) height : (float) width) - ((float) this.mInnerRadius)) / IMAGE_BACKOFF_MULT);
    }

    public void getCenter(PointF p) {
        p.set(((float) getWidth()) / IMAGE_BACKOFF_MULT, (float) (getHeight() / 2));
    }

    public float getAngleOffset() {
        return this.mAngleOffset;
    }

    public void setAngleOffset(float offset) {
        this.mAngleOffset = offset;
        requestLayout();
        invalidate();
    }

    public int getInnerRadius() {
        return this.mInnerRadius;
    }

    public void setInnerRadius(int radius) {
        this.mInnerRadius = radius;
        requestLayout();
        invalidate();
    }

    public void setInnerCircle(Drawable d) {
        this.mInnerCircle = d;
        requestLayout();
        invalidate();
    }

    public void setInnerCircleColor(int color) {
        this.mInnerCircle = new ColorDrawable(color);
        requestLayout();
        invalidate();
    }

    public Drawable getInnerCircle() {
        return this.mInnerCircle;
    }

    public void setInnerCircle(int res) {
        this.mInnerCircle = getContext().getResources().getDrawable(res);
        requestLayout();
        invalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int maxHeight = 0;
        int maxWidth = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
        }
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        int width = resolveSize(Math.max(maxWidth, getSuggestedMinimumWidth()), widthMeasureSpec);
        int height = resolveSize(maxHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
        if (!(this.mSrc == null || (this.mSrc.getWidth() == width && this.mSrc.getHeight() == height))) {
            this.mDst.recycle();
            this.mSrc.recycle();
            this.mDrawingCache.recycle();
            this.mDst = null;
            this.mSrc = null;
            this.mDrawingCache = null;
        }
        if (this.mSrc == null) {
            try {
                this.mSrc = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                this.mDst = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                this.mDrawingCache = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                this.mSrcCanvas = new Canvas(this.mSrc);
                this.mDstCanvas = new Canvas(this.mDst);
                this.mCachedCanvas = new Canvas(this.mDrawingCache);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private LayoutParams layoutParams(View child) {
        return (LayoutParams) child.getLayoutParams();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int i;
        float minDimen;
        int childs = getChildCount();
        float totalWeight = 0.0f;
        for (i = 0; i < childs; i++) {
            totalWeight += layoutParams(getChildAt(i)).weight;
        }
        int width = getWidth();
        int height = getHeight();
        if (width > height) {
            minDimen = (float) height;
        } else {
            minDimen = (float) width;
        }
        float radius = (minDimen - ((float) this.mInnerRadius)) / IMAGE_BACKOFF_MULT;
        this.mBounds.set(((float) (width / 2)) - (minDimen / IMAGE_BACKOFF_MULT), ((float) (height / 2)) - (minDimen / IMAGE_BACKOFF_MULT), ((float) (width / 2)) + (minDimen / IMAGE_BACKOFF_MULT), ((float) (height / 2)) + (minDimen / IMAGE_BACKOFF_MULT));
        float startAngle = this.mAngleOffset;
        for (i = 0; i < childs; i++) {
            int x;
            int y;
            int right;
            int bottom;
            View child = getChildAt(i);
            LayoutParams lp = layoutParams(child);
            float angle = (this.mAngleRange / totalWeight) * lp.weight;
            float centerAngle = startAngle + (angle / IMAGE_BACKOFF_MULT);
            if (childs > 1) {
                x = ((int) (((double) radius) * Math.cos(Math.toRadians((double) centerAngle)))) + (width / 2);
                y = ((int) (((double) radius) * Math.sin(Math.toRadians((double) centerAngle)))) + (height / 2);
            } else {
                x = width / 2;
                y = height / 2;
            }
            int halfChildWidth = child.getMeasuredWidth() / 2;
            int halfChildHeight = child.getMeasuredHeight() / 2;
            int left = lp.width != -1 ? x - halfChildWidth : 0;
            int top = lp.height != -1 ? y - halfChildHeight : 0;
            if (lp.width != -1) {
                right = x + halfChildWidth;
            } else {
                right = width;
            }
            if (lp.height != -1) {
                bottom = y + halfChildHeight;
            } else {
                bottom = height;
            }
            child.layout(left, top, right, bottom);
            if (left != child.getLeft() || top != child.getTop() || right != child.getRight() || bottom != child.getBottom() || lp.startAngle != startAngle || lp.endAngle != startAngle + angle) {
                this.mCached = false;
            }
            lp.startAngle = startAngle;
            startAngle += angle;
            lp.endAngle = startAngle;
        }
        invalidate();
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    protected LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        LayoutParams lp = new LayoutParams(p.width, p.height);
        if (p instanceof android.widget.LinearLayout.LayoutParams) {
            lp.weight = ((android.widget.LinearLayout.LayoutParams) p).weight;
        }
        return lp;
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /*public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mLayoutMode == LAYOUT_NORMAL) {
            return super.dispatchTouchEvent(ev);
        }
        int action = ev.getAction();
        float x = ev.getX() - (((float) getWidth()) / IMAGE_BACKOFF_MULT);
        float y = ev.getY() - (((float) getHeight()) / IMAGE_BACKOFF_MULT);
        if (action == 0) {
            if (this.mMotionTarget != null) {
                MotionEvent cancelEvent = MotionEvent.obtain(ev);
                cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                cancelEvent.offsetLocation((float) (-this.mMotionTarget.getLeft()), (float) (-this.mMotionTarget.getTop()));
                this.mMotionTarget.dispatchTouchEvent(cancelEvent);
                cancelEvent.recycle();
                this.mMotionTarget = null;
            }
            float radius = (float) Math.sqrt((double) ((x * x) + (y * y)));
            if (radius < ((float) this.mInnerRadius) || radius > ((float) getWidth()) / IMAGE_BACKOFF_MULT || radius > ((float) getHeight()) / IMAGE_BACKOFF_MULT) {
                return false;
            }
            float angle = (float) Math.toDegrees(Math.atan2((double) y, (double) x));
            if (angle < 0.0f) {
                angle += this.mAngleRange;
            }
            int childs = getChildCount();
            int i = 0;
            while (i < childs) {
                View child = getChildAt(i);
                LayoutParams lp = layoutParams(child);
                float startAngle = lp.startAngle % this.mAngleRange;
                float endAngle = lp.endAngle % this.mAngleRange;
                float touchAngle = angle;
                if (startAngle > endAngle) {
                    if (touchAngle < startAngle && touchAngle < endAngle) {
                        touchAngle += this.mAngleRange;
                    }
                    endAngle += this.mAngleRange;
                }
                if (startAngle > touchAngle || endAngle < touchAngle) {
                    i++;
                } else {
                    ev.offsetLocation((float) (-child.getLeft()), (float) (-child.getTop()));
                    if (child.dispatchTouchEvent(ev)) {
                        this.mMotionTarget = child;
                        return true;
                    }
                    ev.setLocation(0.0f, 0.0f);
                    return onTouchEvent(ev);
                }
            }
        } else if (this.mMotionTarget != null) {
            ev.offsetLocation((float) (-this.mMotionTarget.getLeft()), (float) (-this.mMotionTarget.getTop()));
            this.mMotionTarget.dispatchTouchEvent(ev);
            if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                this.mMotionTarget = null;
            }
        }
        return onTouchEvent(ev);
    }*/

    private void drawChild(Canvas canvas, View child, LayoutParams lp) {
        mSrcCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mDstCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        this.mSrcCanvas.save();
        int childLeft = child.getLeft();
        int childTop = child.getTop();
        this.mSrcCanvas.clipRect((float) childLeft, (float) childTop, (float) child.getRight(), (float) child.getBottom(), Op.REPLACE);
        this.mSrcCanvas.translate((float) childLeft, (float) childTop);
        child.draw(this.mSrcCanvas);
        this.mSrcCanvas.restore();
        this.mXferPaint.setXfermode(null);
        this.mXferPaint.setColor(Color.BLACK);
        this.mDstCanvas.drawArc(this.mBounds, lp.startAngle, (lp.endAngle - lp.startAngle) % 361.0f, true, this.mXferPaint);
        this.mXferPaint.setXfermode(this.mXfer);
        this.mDstCanvas.drawBitmap(this.mSrc, 0.0f, 0.0f, this.mXferPaint);
        canvas.drawBitmap(this.mDst, 0.0f, 0.0f, null);
    }

    private void redrawDirty(Canvas canvas) {
        for (View child : this.mDirtyViews) {
            drawChild(canvas, child, layoutParams(child));
        }
        if (this.mMotionTarget != null) {
            drawChild(canvas, this.mMotionTarget, layoutParams(this.mMotionTarget));
        }
    }

    private void drawDividers(Canvas canvas, float halfWidth, float halfHeight, float radius) {
        int childs = getChildCount();
        if (childs >= 2) {
            for (int i = 0; i < childs; i++) {
                LayoutParams lp = layoutParams(getChildAt(i));
                canvas.drawLine(halfWidth, halfHeight, (((float) Math.cos(Math.toRadians((double) lp.startAngle))) * radius) + halfWidth, (((float) Math.sin(Math.toRadians((double) lp.startAngle))) * radius) + halfHeight, this.mDividerPaint);
                if (i == childs - 1) {
                    canvas.drawLine(halfWidth, halfHeight, (((float) Math.cos(Math.toRadians((double) lp.endAngle))) * radius) + halfWidth, (((float) Math.sin(Math.toRadians((double) lp.endAngle))) * radius) + halfHeight, this.mDividerPaint);
                }
            }
        }
    }

    private void drawInnerCircle(Canvas canvas, float halfWidth, float halfHeight) {
        if (this.mInnerCircle == null) {
            return;
        }
        if (this.mInnerCircle instanceof ColorDrawable) {
            canvas.drawCircle(halfWidth, halfHeight, (float) this.mInnerRadius, this.mCirclePaint);
            return;
        }
        this.mInnerCircle.setBounds(((int) halfWidth) - this.mInnerRadius, ((int) halfHeight) - this.mInnerRadius, ((int) halfWidth) + this.mInnerRadius, ((int) halfHeight) + this.mInnerRadius);
        this.mInnerCircle.draw(canvas);
    }

    protected void dispatchDraw(Canvas canvas) {
        if (this.mLayoutMode == 1) {
            super.dispatchDraw(canvas);
        } else if (this.mSrc != null && this.mDst != null && !this.mSrc.isRecycled() && !this.mDst.isRecycled()) {
            float radius;
            int childs = getChildCount();
            float halfWidth = ((float) getWidth()) / IMAGE_BACKOFF_MULT;
            float halfHeight = ((float) getHeight()) / IMAGE_BACKOFF_MULT;
            if (halfWidth > halfHeight) {
                radius = halfHeight;
            } else {
                radius = halfWidth;
            }
            if (!this.mCached || this.mDrawingCache == null || this.mDrawingCache.isRecycled() || this.mDirtyViews.size() >= childs / 2) {
                this.mCached = false;
                Canvas sCanvas = null;
                if (this.mCachedCanvas != null) {
                    sCanvas = canvas;
                    canvas = this.mCachedCanvas;
                }
                Drawable bkg = getBackground();
                if (bkg != null) {
                    bkg.draw(canvas);
                }
                for (int i = 0; i < childs; i++) {
                    View child = getChildAt(i);
                    drawChild(canvas, child, layoutParams(child));
                }
//                drawDividers(canvas, halfWidth, halfHeight, radius);
                drawInnerCircle(canvas, halfWidth, halfHeight);
                if (this.mCachedCanvas != null) {
                    sCanvas.drawBitmap(this.mDrawingCache, 0.0f, 0.0f, null);
                    this.mDirtyViews.clear();
                    this.mCached = true;
                    return;
                }
                return;
            }
            canvas.drawBitmap(this.mDrawingCache, 0.0f, 0.0f, null);
            redrawDirty(canvas);
//            drawDividers(canvas, halfWidth, halfHeight, radius);
            drawInnerCircle(canvas, halfWidth, halfHeight);
        }
    }
}
