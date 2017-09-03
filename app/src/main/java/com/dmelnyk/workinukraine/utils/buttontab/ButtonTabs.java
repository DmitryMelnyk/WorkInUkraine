package com.dmelnyk.workinukraine.utils.buttontab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.dmelnyk.workinukraine.R;

import timber.log.Timber;

/**
 * Created by d264 on 7/26/17.
 */

public class ButtonTabs extends View {

    private static final float DEFAULT_HEIGHT = 50f;
    public static final int ANIMATION_SHOW_DURATION = 300;
    private int ANIMATION_HIDE_DURATION = 270;

    private int[][] resource;
    private OnTabClickListener mCallback;
    private GestureDetector mDetector;

    // Default height
    private float mHeight = DEFAULT_HEIGHT;
    private float mWidth;

    // Attributes
    private boolean mAnimationEnabled;
    private int mBackgroundColor = Color.parseColor("#675df7");;
    private final int mForegroundTabColor;

    private float mAnimatedRadius = 0f;
    private float mRadius;
    private boolean[] mButtonsStates;
    private Paint mCircleLightPaint;
    private Paint mCircleAnimationPaint;

    private Drawable mBackgroundDrawable;
    private boolean miSanimating = false;

    private int mGlobalAlpha = 255;
    private Bitmap[][] icons;
    private boolean mSizeIsInitialized;

    public ButtonTabs(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.ButtonTabs, 0, 0);

        try {
            mAnimationEnabled = a.getBoolean(R.styleable.ButtonTabs_animation, true);
            mBackgroundColor = a.getColor(R.styleable.ButtonTabs_backgroundColor,
                    ContextCompat.getColor(context, android.R.color.darker_gray));
            mForegroundTabColor = a.getColor(R.styleable.ButtonTabs_foregroundColor,
                    ContextCompat.getColor(context, android.R.color.white));

        } finally { a.recycle(); }

        init();
    }

    private void init() {
        mBackgroundDrawable = new BackgroundDrawable(mBackgroundColor);

        mCircleLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleLightPaint.setColor(mForegroundTabColor);
        mCircleLightPaint.setAntiAlias(true);
        mCircleLightPaint.setAlpha(mGlobalAlpha);

        mCircleAnimationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleAnimationPaint.setColor(mForegroundTabColor);
        mCircleAnimationPaint.setAlpha(100);

        mDetector = new GestureDetector(ButtonTabs.this.getContext(), new TapListener());
    }

    public void setBackgroundColor(int color) {
        this.mBackgroundColor = color;
        invalidate();
        requestLayout();
    }

    public void setData(int[][] resource) {
        this.resource = resource;

        // initialize buttons state
        if (mButtonsStates == null) {
            mButtonsStates = new boolean[resource.length];
            mButtonsStates[0] = true;
            for (int i = 1; i < resource.length; i++) {
                mButtonsStates[i] = false;
            }
        }
    }

    public void setAnimating(boolean isOn) {
        mAnimationEnabled = isOn;
    }

    private void onAnimate() {
        if (!mAnimationEnabled) return;

        ValueAnimator animator = new ValueAnimator().ofFloat(1f, 0);
        animator.setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimatedRadius = 0f;
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                mAnimatedRadius = (1.0f - currentValue) * mHeight / 2;
                invalidate();
            }
        });

        animator.start();
    }

    public void animHide() {
        if (miSanimating) return;
        miSanimating = true;

        ValueAnimator animatorHide = new ValueAnimator().ofFloat(1f, 0.1f);
        animatorHide.setDuration(ANIMATION_HIDE_DURATION);

        animatorHide.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                setAlpha(currentValue);
                invalidate();
            }
        });

        animatorHide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(View.GONE);
                miSanimating = false;
            }
        });

        animatorHide.start();
    }

    public void animShow() {
        if (miSanimating) return;
        miSanimating = true;

        setVisibility(View.VISIBLE);

        ValueAnimator animatorShow = new ValueAnimator().ofFloat(0, 1f);
        animatorShow.setDuration(ANIMATION_SHOW_DURATION);
        animatorShow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float) animation.getAnimatedValue();
                setAlpha((float) Math.sqrt(currentValue));
            }
        });

        animatorShow.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                miSanimating = false;
            }
        });

        animatorShow.start();
    }

    float mInitialHeight;
    float mInitialWidth;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mSizeIsInitialized) initializeNewWidth();

        int itemCount = resource.length;
        // Draw images
        if (icons != null) {
            for (int i = 0; i < itemCount; i++) {
                Bitmap image;
                float shift = i * 2 * mRadius;
                boolean isSelected = mButtonsStates[i];
                // activated state
                if (isSelected) {
                    image = icons[i][0];
                    if (mAnimatedRadius != 0f) {
                        canvas.drawCircle(shift + mRadius, mRadius, mAnimatedRadius, mCircleAnimationPaint);
                    } else {
                        canvas.drawCircle(shift + mRadius, mRadius, mRadius, mCircleLightPaint);
                    }
                    // default state
                } else {
                    image = icons[i][1];
                }
                canvas.drawBitmap(image, shift, 0, mCircleLightPaint);
            }
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("333", "onSizeChanged()");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ButtonTabOutline(w, h));
        }

        // saving Bitmap to array icons[][]
        int height = getHeight();
        icons = new Bitmap[resource.length][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < resource.length; j++) {
                Bitmap image = BitmapFactory.decodeResource(getResources(), resource[j][i]);
                Bitmap icon = scaleImage(image, height, true);
                icons[j][i] = icon;
            }
        }

//        // setting image size
//        if (mHeight == DEFAULT_HEIGHT) {
//            mHeight = getLayoutParams().height;
//            mInitialHeight = mHeight;
//            mInitialWidth = mHeight * resource.length;
//        }
//
//        mWidth = mHeight * resource.length;
//        mRadius = mHeight / 2;
//
//        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) getLayoutParams();
//        lp.width = (int) mWidth;
//        lp.setBehavior(new ButtonTabBehavior());
//        setLayoutParams(lp);

        setBackground(mBackgroundDrawable);
    }

    private void initializeNewWidth() {
        if (mHeight == DEFAULT_HEIGHT) {
            mHeight = getLayoutParams().height;
            mInitialHeight = mHeight;
            mInitialWidth = mHeight * resource.length;
        }

        mWidth = mHeight * resource.length;
        mRadius = mHeight / 2;

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) getLayoutParams();
        lp.width = (int) mWidth;
        lp.setBehavior(new ButtonTabBehavior());
        setLayoutParams(lp);
        mSizeIsInitialized = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mDetector.onTouchEvent(event);
        if (result) {
            Timber.d("Action code = " + event.getAction());
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float tapX = event.getX();
                int buttonPosition = (int) (tapX / mHeight);

                setButtonSelected(buttonPosition);
                onAnimate();
            }
        }
        return super.onTouchEvent(event);
    }

    private void setButtonSelected(int buttonPosition) {
        for (int i = 0; i < mButtonsStates.length; i++) {
            if (i == buttonPosition) {
                mButtonsStates[i] = true;
                mCallback.tabSelected(i);
            } else {
                mButtonsStates[i] = false;
            }
        }
    }

    private class TapListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Timber.d("onDown(MotionEvent)");
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Timber.d("onSingleTapConfirmed(MotionEvent e)");
            return super.onSingleTapConfirmed(e);
        }
    }

    private Bitmap scaleImage(Bitmap image, float scaledSize, Boolean filter) {
        float ratio = Math.min(
                scaledSize / image.getWidth(),
                scaledSize / image.getHeight());

        int width = Math.round(ratio * image.getWidth());
        int height = Math.round(ratio * image.getHeight());

        return Bitmap.createScaledBitmap(image, width, height, filter);
    }

    public void selectTab(int selectedTab) {
        for (int i = 0; i < mButtonsStates.length; i++) {
            if (i == selectedTab) {
                mButtonsStates[i] = true;
            } else {
                mButtonsStates[i] = false;
            }
        }
    }
    /**
     * Class that uses ButtonTabs must implement this callback interface
     */
    public interface OnTabClickListener {
        void tabSelected(int item);
    }

    /**
     * Selects callback for receiving ButtonTabs event
     * @param callback
     */
    public void setOnTabClickListener(@NonNull OnTabClickListener callback) {
        mCallback = callback;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.saveButtonsState(mButtonsStates);
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        initializeButtonsState(savedState.getButtonState());
    }

    private void initializeButtonsState(boolean[] buttonsStates) {
        mButtonsStates = buttonsStates;
    }

    static class SavedState extends BaseSavedState {
        private boolean[] buttonsStates;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel in) {
            super(in);
            in.readBooleanArray(buttonsStates);
        }

        public void saveButtonsState(boolean[] buttonsStates) {
            this.buttonsStates = buttonsStates;
        }

        public boolean[] getButtonState() {
            return buttonsStates;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeBooleanArray(buttonsStates);
        }
    }

}
