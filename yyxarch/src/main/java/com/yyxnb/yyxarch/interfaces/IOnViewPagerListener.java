package com.yyxnb.yyxarch.interfaces;

import android.view.View;

public interface IOnViewPagerListener {
    /**
     * 初始化
     */
    void onInitComplete(View view);

    /**
     * 释放
     */
    void onPageRelease(boolean isNext, int position, View view);

    /**
     * 选中
     */
    void onPageSelected(int position, boolean isSelected, boolean isBottom, View view);
}