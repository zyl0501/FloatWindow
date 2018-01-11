package com.yhao.floatwindow;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * @author zyl
 * @date Created on 2018/1/11
 */
class FloatDragLayout extends FrameLayout {
    int mTouchSlop;
    int mLastMotionX;
    int mLastMotionY;
    int mLastMotionX0;
    int mLastMotionY0;
    boolean mIsBeingDragged;
    boolean mIsSingleTap;
    DragListener listener;

    public FloatDragLayout(Context context) {
        this(context, null);
    }

    public FloatDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public FloatDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatDragLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
            return true;
        }

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final int y = (int) ev.getRawY();
                final int x = (int) ev.getRawX();
                mLastMotionY = y;
                mLastMotionX = x;
                mIsBeingDragged = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int y = (int) ev.getRawY();
                final int x = (int) ev.getRawX();
                final int yDiff = Math.abs(y - mLastMotionY);
                final int xDiff = Math.abs(x - mLastMotionX);
                if (!mIsBeingDragged && (yDiff > mTouchSlop || xDiff > mTouchSlop)) {
                    mIsBeingDragged = true;
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mLastMotionY = y;
                    mLastMotionX = x;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                break;
            default:
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int actionMasked = ev.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                final int y = (int) ev.getRawY();
                final int x = (int) ev.getRawX();
                mLastMotionY = y;
                mLastMotionX = x;
                mIsBeingDragged = false;
                mIsSingleTap = true;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int y = (int) ev.getRawY();
                final int x = (int) ev.getRawX();
                final int yDiff = y - mLastMotionY;
                final int xDiff = x - mLastMotionX;
                if (!mIsBeingDragged) {
                    if ((Math.abs(yDiff) > mTouchSlop || Math.abs(xDiff) > mTouchSlop)) {
                        mIsBeingDragged = true;
                        final ViewParent parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    } else {
                        break;
                    }
                }
                if (listener != null) {
                    listener.onDrag(xDiff, yDiff);
                }
                mLastMotionX = x;
                mLastMotionY = y;
                mIsSingleTap = false;
                break;
            }
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                if (isClickable() && mIsSingleTap) {
                    performClick();
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void setDragListener(DragListener listener) {
        this.listener = listener;
    }

    public interface DragListener {
        void onDrag(float deltaX, float deltaY);
    }
}
