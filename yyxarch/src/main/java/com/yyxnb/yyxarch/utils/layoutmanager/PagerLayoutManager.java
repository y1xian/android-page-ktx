package com.yyxnb.yyxarch.utils.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yyxnb.yyxarch.interfaces.IOnViewPagerListener;

/**
 * Description: 抖音列表
 *
 * @author : yyx
 * @date ：2018/11/18
 */
public class PagerLayoutManager extends LinearLayoutManager {
    private PagerSnapHelper mPagerSnapHelper;
    private IOnViewPagerListener mIOnViewPagerListener;
    private RecyclerView mRecyclerView;
    private int mDrift;//位移，用来判断移动方向
    private boolean isSelected;

    public PagerLayoutManager(Context context, int orientation) {
        super(context, orientation, false);
        init();
    }

    public PagerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }

    private void init() {
        mPagerSnapHelper = new PagerSnapHelper();
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mPagerSnapHelper.attachToRecyclerView(view);
        this.mRecyclerView = view;
        mRecyclerView.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }

    /**
     * 滑动状态的改变
     * 缓慢拖拽-> SCROLL_STATE_DRAGGING
     * 快速滚动-> SCROLL_STATE_SETTLING
     * 空闲状态-> SCROLL_STATE_IDLE
     *
     * @param state
     */
    @Override
    public void onScrollStateChanged(int state) {
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
                View viewIdle = mPagerSnapHelper.findSnapView(this);
                if (viewIdle != null) {
                    int positionIdle = getPosition(viewIdle);
                    if (mIOnViewPagerListener != null && getChildCount() == 1) {
                        mIOnViewPagerListener.onPageSelected(positionIdle, isSelected, positionIdle == getItemCount() - 1, viewIdle);
                    }
                }
                break;
        }
    }


    /**
     * 监听竖直方向的相对偏移量
     *
     * @param dy
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    /**
     * 监听水平方向的相对偏移量
     *
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dx;
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnViewPagerListener(IOnViewPagerListener listener) {
        this.mIOnViewPagerListener = listener;
    }

    private RecyclerView.OnChildAttachStateChangeListener mChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
        /**
         * itemView依赖Window
         * 播放视频操作 即将要播放的是上一个视频 还是下一个视频
         */
        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (mIOnViewPagerListener != null && getChildCount() == 1) {
                mIOnViewPagerListener.onInitComplete(view);
            }

            if (mDrift > 0) {
//            向上
                if (mIOnViewPagerListener != null) {
                    isSelected = true;
//                    mIOnViewPagerListener.onPageSelected(getPosition(android.support.v4.view), true,android.support.v4.view);
                }

            } else {
                if (mIOnViewPagerListener != null) {
                    isSelected = false;
//                    mIOnViewPagerListener.onPageSelected(getPosition(android.support.v4.view), false,android.support.v4.view);
                }
            }
        }

        /**
         *itemView脱离Window
         * 暂停操作
         */
        @Override
        public void onChildViewDetachedFromWindow(View view) {
            if (mDrift >= 0) {
                if (mIOnViewPagerListener != null) {
                    mIOnViewPagerListener.onPageRelease(true, getPosition(view), view);
                }
            } else {
                if (mIOnViewPagerListener != null) {
                    mIOnViewPagerListener.onPageRelease(false, getPosition(view), view);
                }
            }

        }
    };

    @Override
    public boolean canScrollVertically() {
        return true;
    }
}