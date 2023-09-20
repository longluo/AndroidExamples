package com.maciekjanusz.draglayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;

import java.util.concurrent.atomic.AtomicBoolean;

public class DragLayout extends FrameLayout implements View.OnTouchListener, SpringListener, View.OnLongClickListener {

    private static final String TAG = DragLayout.class.getSimpleName();

    private static final float DEFAULT_SCALE = 0.25f;
    private static final float DEFAULT_TENSION = 800;
    private static final float DEFAULT_FRICTION = 20;
    private static final boolean DEFAULT_INTERCEPT_TOUCH_EVENT = true;

    private float initialX;
    private float initialY;
    private float initialTouchX;
    private float initialTouchY;

    private Spring pushSpring;

    private float tension = DEFAULT_TENSION;
    private float friction = DEFAULT_FRICTION;
    private float scale = DEFAULT_SCALE;
    private boolean intercepting = DEFAULT_INTERCEPT_TOUCH_EVENT;

    // drag helpers
    private DragListener dragListener;
    private volatile AtomicBoolean isDraggable = new AtomicBoolean(false);
    private MotionEvent lastTouchEvent;

    private boolean longClickDefined = false;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attrArray = context
                    .obtainStyledAttributes(attrs, R.styleable.DragLayout);

            for (int i = 0; i < attrArray.getIndexCount(); ++i) {
                int attr = attrArray.getIndex(i);
                if (attr == R.styleable.DragLayout_scale) {
                    scale = attrArray.getFloat(attr, DEFAULT_SCALE);

                } else if (attr == R.styleable.DragLayout_springFriction) {
                    friction = attrArray.getFloat(attr, DEFAULT_FRICTION);

                } else if (attr == R.styleable.DragLayout_springTension) {
                    tension = attrArray.getFloat(attr, DEFAULT_TENSION);

                } else if (attr == R.styleable.DragLayout_intercepting) {
                    intercepting = attrArray.getBoolean(attr, DEFAULT_INTERCEPT_TOUCH_EVENT);

                }
            }
            attrArray.recycle();
        }

        setClickable(true);
        setOnTouchListener(this);
        setOnLongClickListener(this);

        SpringSystem springSystem = SpringSystem.create();
        pushSpring = springSystem.createSpring();
        pushSpring.addListener(this);
        pushSpring.setSpringConfig(new SpringConfig(tension, friction));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return intercepting;
    }

    @Override
    public synchronized boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG, "onTouch()");
        lastTouchEvent = event;

        if (isDraggable.get()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = getX();
                    initialY = getY();
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    pushSpring.setEndValue(1f);
                    if (dragListener != null) {
                        dragListener.onDragStarted(initialX, initialY);
                    }
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float x = initialX + (int) (event.getRawX() - initialTouchX);
                    float y = initialY + (int) (event.getRawY() - initialTouchY);
                    setX(x);
                    setY(y);
                    if (dragListener != null) {
                        dragListener.onDrag(x, y);
                    }
                    return true;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    pushSpring.setEndValue(0f);
                    isDraggable.set(false);

                    if (dragListener != null) {
                        dragListener.onDragFinished(getX(), getY());
                    }
                    return true;
            }
        }

        return false;
    }

    @Override
    public void onSpringUpdate(Spring spring) {
        float value = (float) pushSpring.getCurrentValue();
        float sizeScale = 1 + (value * scale);
        setScaleX(sizeScale);
        setScaleY(sizeScale);
    }

    @Override
    public void onSpringAtRest(Spring spring) {

    }

    @Override
    public void onSpringActivate(Spring spring) {

    }

    @Override
    public void onSpringEndStateChange(Spring spring) {

    }

    public void setDragListener(DragListener listener) {
        this.dragListener = listener;
    }

    public float getTension() {
        return tension;
    }

    public void setTension(float tension) {
        this.tension = tension;
        pushSpring.getSpringConfig().tension = tension;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
        pushSpring.getSpringConfig().friction = friction;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isIntercepting() {
        return intercepting;
    }

    public void setIntercepting(boolean intercepting) {
        this.intercepting = intercepting;
    }

    @Override
    public boolean onLongClick(View v) {
        Log.i(TAG, "onLongClick()");
        isDraggable.set(true);
        lastTouchEvent.setAction(MotionEvent.ACTION_DOWN);
        onTouch(DragLayout.this, lastTouchEvent);
        return true;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        if (!longClickDefined) {
            super.setOnLongClickListener(l);
            longClickDefined = true;
        }
    }

    public interface DragListener {
        void onDragFinished(float x, float y);

        void onDrag(float x, float y);

        void onDragStarted(float x, float y);
    }
}
