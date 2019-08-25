package com.yyxnb.yyxarch.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.IntRange
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import java.io.Serializable

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.regex.Pattern

/**
 * 状态栏工具类
 */
object StatusBarUtil : Serializable {

    private const val DEFAULT_ALPHA = 0

    /**
     * 设置纯色状态栏（自定义颜色，alpha）
     *
     * @param activity 目标activity
     * @param color    状态栏颜色值
     * @param alpha    状态栏透明度
     */
    @JvmOverloads
    fun setStatusBarColor(activity: Activity, color: Int,  alpha: Int = DEFAULT_ALPHA) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.statusBarColor = cipherColor(color, alpha)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            setTranslucentView(activity.window.decorView as ViewGroup, color, alpha)
            setRootView(activity, true)
        }
    }

    /**
     * 设置状态栏渐变颜色
     *
     * @param activity 目标activity
     * @param view     目标View
     */
    fun setGradientColor(activity: Activity, view: View) {
        val decorView = activity.window.decorView as ViewGroup
        val fakeStatusBarView = decorView.findViewById<View>(android.R.id.custom)
        if (fakeStatusBarView != null) {
            decorView.removeView(fakeStatusBarView)
        }
        setRootView(activity, false)
        setStatusBarTranslucent(activity)
        setPaddingTop(activity, view)
    }

    /**
     * 设置透明状态栏
     *
     * @param activity 目标界面
     */
    fun setStatusBarTranslucent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = Color.TRANSPARENT
            activity.window
                    .decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window
                    .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)
     *
     * @param context 目标Context
     * @param view    需要增高的View
     */
    fun setPaddingTop(context: Context, view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val lp = view.layoutParams
            if (lp != null && lp.height > 0 && view.paddingTop == 0) {
                lp.height += getStatusBarHeight(context)
                view.setPadding(view.paddingLeft, view.paddingTop + getStatusBarHeight(context),
                        view.paddingRight, view.paddingBottom)
            }
        }
    }

    /**
     * 状态栏字体颜色 6.0
     * @param dark true 为变黑 false 变亮
     */
    @TargetApi(Build.VERSION_CODES.M)
    fun setStatusBarStyle(window: Window, dark: Boolean) {
        darkModeForM(window, dark)
    }

    /**
     * android 6.0设置字体颜色
     *
     * @param window 目标window
     * @param dark   亮色 or 暗色
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun darkModeForM(window: Window, dark: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var systemUiVisibility = window.decorView.systemUiVisibility
            systemUiVisibility = if (dark) {
                systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            window.decorView.systemUiVisibility = systemUiVisibility
        }
    }

    fun isBlackColor(color: Int, level: Int): Boolean {
        val grey = StatusBarUtils.toGrey(color)
        return grey < level
    }

    //虚拟导航栏颜色
    fun setNavigationBarColor(window: Window, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!StatusBarUtils.isHuawei()) {
                setNavigationBarStyle(window, !isBlackColor(color, 176))
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = color
        }
    }

    /**
     * 虚拟导航栏文字
     * @param dark true 为黑色 false 白色
     */
    fun setNavigationBarStyle(window: Window, dark: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val decorView = window.decorView
            var systemUi = decorView.systemUiVisibility
            if (dark) {
                systemUi = systemUi or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                systemUi = systemUi and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            }
            decorView.systemUiVisibility = systemUi
        }
    }

    /**
     * 计算alpha色值
     *
     * @param color 状态栏颜色值
     * @param alpha 状态栏透明度
     */
    private fun cipherColor(@ColorInt color: Int, alpha: Int): Int {
        if (alpha == 0) {
            return color
        }
        val a = 1 - alpha / 255f
        var red = color shr 16 and 0xff
        var green = color shr 8 and 0xff
        var blue = color and 0xff
        red = (red * a + 0.5).toInt()
        green = (green * a + 0.5).toInt()
        blue = (blue * a + 0.5).toInt()
        return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
    }

    /**
     * 创建透明View
     *
     * @param viewGroup 目标视图
     * @param color     状态栏颜色值
     * @param alpha     状态栏透明度
     */
    private fun setTranslucentView(viewGroup: ViewGroup, @ColorInt color: Int, @IntRange(from = 0, to = 255) alpha: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val cipherColor = cipherColor(color, alpha)
            var translucentView: View? = viewGroup.findViewById(android.R.id.custom)
            if (translucentView == null && cipherColor != 0) {
                translucentView = View(viewGroup.context)
                translucentView.id = android.R.id.custom
                val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(viewGroup.context))
                viewGroup.addView(translucentView, params)
            }
            translucentView?.setBackgroundColor(cipherColor)
        }

    }

    /**
     * 设置根布局参数
     *
     * @param activity         目标activity
     * @param fitSystemWindows 是否预留toolbar的高度
     */
    private fun setRootView(activity: Activity, fitSystemWindows: Boolean) {
        val parent = activity.findViewById<ViewGroup>(android.R.id.content)
        var i = 0
        val count = parent.childCount
        while (i < count) {
            val childView = parent.getChildAt(i)
            if (childView is ViewGroup) {
                childView.setFitsSystemWindows(fitSystemWindows)
                childView.clipToPadding = fitSystemWindows
            }
            i++
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context 目标Context
     */
    private fun getStatusBarHeight(context: Context): Int {
        // 获得状态栏高度
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return context.resources.getDimensionPixelSize(resourceId)
    }
}