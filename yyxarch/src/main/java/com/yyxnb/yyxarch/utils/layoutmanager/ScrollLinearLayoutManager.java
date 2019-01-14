package com.yyxnb.yyxarch.utils.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Description: 解决滑动recyclerView时有卡顿的问题
 *
 * @author : yyx
 * @date ：2018/7/5
 */
public class ScrollLinearLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = true;

        public ScrollLinearLayoutManager(Context context) {
            super(context);
        }

        public ScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public ScrollLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            return isScrollEnabled && super.canScrollVertically();
        }
    }