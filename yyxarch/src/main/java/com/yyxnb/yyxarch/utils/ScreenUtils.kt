package com.yyxnb.yyxarch.utils

import com.yyxnb.yyxarch.AppUtils
import java.lang.reflect.Field

object ScreenUtils {

    private val mContext = AppUtils.context

    /**
     * 屏幕宽高
     *
     * @return
     */
    private val screenSize: IntArray
        get() {
            val dm = mContext
                    .resources.displayMetrics
            val screenWidth = dm.widthPixels
            val screenHeight = dm.heightPixels

            return intArrayOf(screenWidth, screenHeight)
        }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    val statusBarHeight: Int
        get() {
            var c: Class<*>? = null
            var obj: Any? = null
            var field: Field? = null
            var x = 0
            var statusBarHeight = 0
            try {
                c = Class.forName("com.android.internal.R\$dimen")
                obj = c!!.newInstance()
                field = c.getField("status_bar_height")
                x = Integer.parseInt(field!!.get(obj).toString())
                statusBarHeight = mContext.resources.getDimensionPixelSize(x)
            } catch (e1: Exception) {
                e1.printStackTrace()
            }

            return statusBarHeight
        }

    /**
     * 获取手机屏幕的宽度
     *
     * @return
     */
    val screenWidth: Int
        get() {
            val screen = screenSize
            return screen[0]
        }

    /**
     * 获取手机屏幕的高度
     *
     * @return
     */
    val screenHeight: Int
        get() {
            val screen = screenSize
            return screen[1]
        }

    /**
     * 根据手机分辨率将dp转为px单位
     */
    open fun dip2px(dpValue: Float): Int {
        val scale = mContext.resources
                .displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    open fun px2dip(pxValue: Float): Int {
        val scale = mContext.resources
                .displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}