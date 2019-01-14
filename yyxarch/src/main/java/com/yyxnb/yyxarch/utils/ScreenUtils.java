package com.yyxnb.yyxarch.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.yyxnb.yyxarch.AppUtils;

import java.lang.reflect.Field;

public class ScreenUtils {

    private static Context mContext = AppUtils.getContext();

    /**
     * 根据手机分辨率将dp转为px单位
     */
    public static int dip2px(float dpValue) {
        final float scale = mContext.getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = mContext.getResources()
                .getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 屏幕宽高
     *
     * @return
     */
    private static int[] getScreenSize() {
        DisplayMetrics dm = mContext
                .getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        return new int[]{screenWidth, screenHeight};
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = mContext.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取手机屏幕的宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        int screen[] = getScreenSize();
        return screen[0];
    }

    /**
     * 获取手机屏幕的高度
     *
     * @return
     */
    public static int getScreenHeight() {
        int screen[] = getScreenSize();
        return screen[1];
    }
}