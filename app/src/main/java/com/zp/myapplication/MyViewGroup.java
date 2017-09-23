package com.zp.myapplication;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


/**
 * 自定义ViewGroup
 */

public class MyViewGroup extends FrameLayout {

    //子view的个数
    private int count;

    //ViewGroup的宽
    private int width;

    //ViewGroup的高
    private int height;

    //定义一个ViewDragHelper（ViewDragHelper可以用来拖拽和设置子View的位置在ViewGroup范围内）
    private ViewDragHelper viewDragHelper;

    /**
     * ViewDragHelper.Callback 触摸回调
     */
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        /**
         * 判断是是否需要捕获这个事件
         * @param child 事件发生的对象（子view）
         * @param pointerId
         * @return true捕获事件
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == MyViewGroup.this.getChildAt(1);
        }

        /**
         * 计算child垂直方向的位置
         * @param child 子view
         * @param left 左边缘的位置（相对于ViewGroup）
         * @param dx 滑动的距离
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        /**
         * 计算child垂直方向的位置
         * @param child 子view
         * @param top 上边缘的位置（相对于ViewGroup）
         * @param dy 滑动的距离
         * @return
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }

        /**
         * 在子view位置发生改变时都会被调用
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == MyViewGroup.this.getChildAt(1)) {
                View view = MyViewGroup.this.getChildAt(0);
                view.layout(view.getLeft() - dx, view.getTop() - dy, view.getRight() - dx, view.getBottom() - dy);
            }

            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }


        /**
         * 在子view位置改变结束时都会被调用
         * @param releasedChild
         * @param xvel 水平移动速度
         * @param yvel 垂直移动速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            //让子view移动到指定的位置
            viewDragHelper.smoothSlideViewTo(MyViewGroup.this.getChildAt(1), width / 2, 0);
            ViewCompat.postInvalidateOnAnimation(MyViewGroup.this);
            super.onViewReleased(releasedChild, xvel, yvel);
        }
    };


    public MyViewGroup(@NonNull Context context) {
        super(context);
        CreatViewDragHelper();
    }

    public MyViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        CreatViewDragHelper();
    }

    public MyViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CreatViewDragHelper();
    }

    /**
     * 创建viewDragHelper实例
     * 创建需要三个参数
     * 第一个为当前的ViewGroup
     * 第二个为sensitivity，主要用于设置touchSlop
     * 第三个为ViewDragHelper.Callback，触摸过程中会回调相关方法。
     */
    private void CreatViewDragHelper() {
        viewDragHelper = ViewDragHelper.create(this, 1.0f, callback);
    }

    /**
     * viewgroup 的尾标签加载完毕时回调
     * 在这里我们获取子view的个数和子view对象
     */
    @Override
    protected void onFinishInflate() {
        count = this.getChildCount();
        if (count > 2) {
            throw new IllegalArgumentException("不好意思我只能装两个");
        }
        super.onFinishInflate();
    }

    /**
     * 控制控件的大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * onMeasure执行之后执行
     * 获取自身以及子View 的大小
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //获取自身宽
        width = getMeasuredWidth();
        //获取自身高
        height = getMeasuredHeight();
        super.onSizeChanged(w, h, oldw, oldh);
    }


    /**
     * 控制控件的位置
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        switch (count) {
            case 1:
                //只有一个子view时充满父容器
                View view = getChildAt(0);
                view.layout(0, 0, width, height);
                break;
            case 2:
                //两个子view时一个在左上 一个在右下
                View view1 = getChildAt(0);
                view1.layout(0, 0, width / 2, height / 2);
                View view2 = getChildAt(1);
                view2.layout(width / 2, height / 2, width, height);
                break;
            default:
                break;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 让viewDragHelper判断是否拦截触事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * 将触摸事件交个viewDragHelper
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }
}
