package com.loopeer.codereader.ui.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class DragView extends ViewGroup {
    private ViewDragHelper mDragHelper;
    private View mContentView;
    private View mActionView;
    private int mDragRange;
    private float mDragOffset;
    private int mActionViewWidth;

    public DragView(Context context) {
        this(context, null);
    }

    public DragView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = getChildAt(0);
        mActionView = getChildAt(1);
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            /*return child == mContentView;*/
            return true;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mDragOffset = (float) -left / mDragRange;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int left = 0;
            if (xvel < 0 || (xvel == 0 && mDragOffset > 0.5f)) {
                left -= mDragRange;
            }
            mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());
            invalidate();
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mDragRange;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 0;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
            /*Log.d("DragLayout", "clampViewPositionHorizontal " + left + "," + dx);
            final int leftBound = - mActionViewWidth;
            final int rightBound = 0;
            final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
            return newLeft;*/
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View childContent = mContentView;
        measureChildWithMargins(childContent, widthMeasureSpec, 0, heightMeasureSpec, 0);
        View childDragShowContent = mActionView;
        int heightSizeAndState = resolveSizeAndState(childContent.getMeasuredHeight(), heightMeasureSpec, 0);
        int widthSizeAndState = resolveSizeAndState(childContent.getMeasuredWidth(), heightMeasureSpec, 0);
        setMeasuredDimension(widthSizeAndState,
                heightSizeAndState);
        measureChildWithMargins(childDragShowContent, widthMeasureSpec, 0, heightMeasureSpec, 0);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        View childContent = mContentView;
        int childTop = getPaddingTop();
        int childLeft = getPaddingLeft();
        int childWidth = childContent.getMeasuredWidth();
        int childHeight = childContent.getMeasuredHeight();
        childContent.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        childContent.bringToFront();

        View childDragShowContent = mActionView;
        int childDragWidth = childDragShowContent.getMeasuredWidth();
        int childDragHeight = childDragShowContent.getMeasuredHeight();
        int childDragLeft = childWidth - childDragWidth;
        childDragShowContent.layout(childDragLeft, childTop, childWidth, childTop + childDragHeight);
        mDragRange = childDragWidth;
        mActionViewWidth = childDragWidth;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

}
