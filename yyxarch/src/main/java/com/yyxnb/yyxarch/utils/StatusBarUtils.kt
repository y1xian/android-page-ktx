package com.yyxnb.yyxarch.utils

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v4.view.ViewCompat
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import com.yyxnb.yyxarch.base.BaseFragment
import java.io.Serializable

object StatusBarUtils : Serializable {

    fun toGrey(rgb: Int): Int {
        val blue = rgb and 0x000000FF
        val green = rgb and 0x0000FF00 shr 8
        val red = rgb and 0x00FF0000 shr 16
        return red * 38 + green * 75 + blue * 15 shr 7
    }

    //检测是否刘海平

    @Volatile
    private var sHasCheckCutout: Boolean = false
    @Volatile
    private var sIsCutout: Boolean = false

    private val NOTCH_IN_SCREEN_VOIO = 0x00000020//是否有凹槽
    private val ROUNDED_IN_SCREEN_VOIO = 0x00000008//是否有圆角

    private val MIUI_NOTCH = "ro.miui.notch"

    private val BRAND = Build.BRAND.toLowerCase()

    fun isHuawei(): Boolean = BRAND.contains("huawei") || BRAND.contains("honor")

    fun isXiaomi(): Boolean = Build.MANUFACTURER.toLowerCase() == "xiaomi"

    fun isVivo(): Boolean = BRAND.contains("vivo") || BRAND.contains("bbk")

    fun isOppo(): Boolean = BRAND.contains("oppo")


    // 是否刘海屏
    fun isCutout(activity: Activity): Boolean {
        if (sHasCheckCutout) {
            return sIsCutout
        }

        sHasCheckCutout = true

        // 低于 API 27 的，都不会是刘海屏、凹凸屏
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            sIsCutout = false
            return false
        }

        sIsCutout = isHuaweiCutout(activity) || isOppoCutout(activity) || isVivoCutout(activity) || isXiaomiCutout(activity)

        if (!isGoogleCutoutSupport()) {
            return sIsCutout
        }

        if (!sIsCutout) {
            val window = activity.window
                    ?: throw IllegalStateException("activity has not attach to window")
            val decorView = window.decorView
            sIsCutout = attachHasOfficialNotch(decorView)
        }

        return sIsCutout
    }

    @TargetApi(28)
    private fun attachHasOfficialNotch(view: View): Boolean {
        val windowInsets = view.rootWindowInsets
        if (windowInsets != null) {
            val displayCutout = windowInsets.displayCutout
            return displayCutout != null
        } else {
            throw IllegalStateException("activity has not yet attach to window, you must call `isCutout` after `Activity#onAttachedToWindow` is called.")
        }
    }

    fun isHuaweiCutout(context: Context): Boolean {
        if (!isHuawei()) {
            return false
        }

        var ret = false
        try {
            val cl = context.classLoader
            val HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val get = HwNotchSizeUtil.getMethod("hasNotchInScreen")
            ret = get.invoke(HwNotchSizeUtil) as Boolean
        } catch (e: Exception) {
            // ignore
        }

        return ret
    }

    fun isOppoCutout(context: Context): Boolean {
        return if (!isOppo()) {
            false
        } else context.packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
    }


    fun isVivoCutout(context: Context): Boolean {
        if (!isVivo()) {
            return false
        }

        var ret = false

        try {
            val cl = context.classLoader
            val ftFeature = cl.loadClass("android.util.FtFeature")
            val methods = ftFeature.declaredMethods
            if (methods != null) {
                for (i in methods.indices) {
                    val method = methods[i]
                    if (method.name.equals("isFeatureSupport", ignoreCase = true)) {
                        ret = method.invoke(ftFeature, NOTCH_IN_SCREEN_VOIO) as Boolean
                        break
                    }
                }
            }
        } catch (e: Exception) {
            // ignore
        }

        return ret
    }


    @SuppressLint("PrivateApi")
    fun isXiaomiCutout(context: Context): Boolean {
        if (!isXiaomi()) {
            return false
        }
        try {
            val spClass = Class.forName("android.os.SystemProperties")
            val getMethod = spClass.getDeclaredMethod("getInt", String::class.java, Int::class.javaPrimitiveType)
            getMethod.isAccessible = true
            val hasNotch = getMethod.invoke(null, MIUI_NOTCH, 0) as Int
            return hasNotch == 1
        } catch (e: Exception) {
            // ignore
        }

        return false
    }

    fun isGoogleCutoutSupport(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P


    //

    fun setRenderContentInShortEdgeCutoutAreas(window: Window, shortEdges: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val layoutParams = window.attributes
            if (shortEdges) {
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            } else {
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
            }
            window.attributes = layoutParams
        }
    }

    //开启沉浸式
    fun setStatusBarTranslucent(window: Window, translucent: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setRenderContentInShortEdgeCutoutAreas(window, translucent)

            val decorView = window.decorView
            if (translucent) {
                decorView.setOnApplyWindowInsetsListener { v, insets ->
                    val defaultInsets = v.onApplyWindowInsets(insets)
                    defaultInsets.replaceSystemWindowInsets(
                            defaultInsets.systemWindowInsetLeft,
                            0,
                            defaultInsets.systemWindowInsetRight,
                            defaultInsets.systemWindowInsetBottom)
                }
            } else {
                decorView.setOnApplyWindowInsetsListener(null)
            }

            ViewCompat.requestApplyInsets(decorView)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (translucent) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
            ViewCompat.requestApplyInsets(window.decorView)
        }
    }

    fun shouldAdjustStatusBarColor(fragment: BaseFragment): Boolean {
        var shouldAdjustForWhiteStatusBar = !isBlackColor(fragment.preferredStatusBarColor(), 176)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shouldAdjustForWhiteStatusBar = shouldAdjustForWhiteStatusBar && fragment.preferredStatusBarStyle() === BarStyle.LightContent
        }
        return shouldAdjustForWhiteStatusBar
    }

    fun isBlackColor(color: Int, level: Int): Boolean {
        val grey = toGrey(color)
        return grey < level
    }

    //虚拟导航栏颜色
    fun setNavigationBarColor(window: Window, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isHuawei()) {
                setNavigationBarStyle(window, !isBlackColor(color, 176))
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.navigationBarColor = color
        }
    }

    //虚拟导航栏文字
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
     * 设置页面最外层布局 FitsSystemWindows 属性
     * @param activity
     * @param value
     */
    fun setFitsSystemWindows(activity: Activity, value: Boolean) {
        val contentFrameLayout = activity.findViewById<View>(android.R.id.content) as ViewGroup
        val parentView = contentFrameLayout.getChildAt(0)
        if (parentView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            parentView.fitsSystemWindows = value
        }
    }

    /**
     * 隐藏虚拟导航栏
     */
    fun setNavigationBarHidden(window: Window, hidden: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            var systemUi = decorView.systemUiVisibility
            if (hidden) {
                systemUi = systemUi or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                systemUi = systemUi or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            } else {
                systemUi = systemUi and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
                systemUi = systemUi and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
            }
            window.decorView.systemUiVisibility = systemUi
        }
    }

    /**
     * 状态栏颜色
     */
    fun setStatusBarColor(window: Window, color: Int, animated: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (animated) {
                val curColor = window.statusBarColor
                val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), curColor, color)
                colorAnimation.addUpdateListener { animator -> window.statusBarColor = animator.animatedValue as Int }
                colorAnimation.setDuration(200).startDelay = 0
                colorAnimation.start()
            } else {
                window.statusBarColor = color
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val decorViewGroup = window.decorView as ViewGroup
            var statusBarView: View? = decorViewGroup.findViewWithTag("custom_status_bar_tag")
            if (statusBarView == null) {
                statusBarView = View(window.context)
                statusBarView.tag = "custom_status_bar_tag"
                val params = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight(window.context))
                params.gravity = Gravity.TOP
                statusBarView.layoutParams = params
                decorViewGroup.addView(statusBarView)
            }


            if (animated) {
                val drawable = statusBarView.background
                var curColor = Integer.MAX_VALUE
                if (drawable is ColorDrawable) {
                    curColor = drawable.color
                }
                if (curColor != Integer.MAX_VALUE) {
                    val barView = statusBarView
                    val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), curColor, color)
                    colorAnimation.addUpdateListener { animator -> barView.background = ColorDrawable(animator.animatedValue as Int) }
                    colorAnimation.setDuration(200).startDelay = 0
                    colorAnimation.start()
                } else {
                    statusBarView.background = ColorDrawable(color)
                }
            } else {
                statusBarView.background = ColorDrawable(color)
            }
        }
    }

    /**
     * 状态栏字体颜色 6.0
     * @param dark true 为黑色 false 白色
     */
    fun setStatusBarStyle(window: Window, dark: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            var systemUi = decorView.systemUiVisibility
            if (dark) {
                systemUi = systemUi or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                systemUi = systemUi and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decorView.systemUiVisibility = systemUi
        }
    }

    /**
     * 隐藏状态栏
     */
    fun setStatusBarHidden(window: Window, hidden: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val decorView = window.decorView
            var systemUi = decorView.systemUiVisibility
            if (hidden) {
                systemUi = systemUi or View.SYSTEM_UI_FLAG_FULLSCREEN
                systemUi = systemUi or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            } else {
                systemUi = systemUi and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
                systemUi = systemUi and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
            }
            window.decorView.systemUiVisibility = systemUi
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            val decorViewGroup = window.decorView as ViewGroup
            val statusBarView = decorViewGroup.findViewWithTag<View>("custom_status_bar_tag")
            if (statusBarView != null) {
                val hiding = statusBarView.isClickable
                if (hiding == hidden) {
                    return
                }

                if (hidden) {
                    statusBarView.isClickable = true
                    val animator = ObjectAnimator.ofFloat(statusBarView, "y", -getStatusBarHeight(window.context) as Float)
                    animator.setDuration(200)
                    animator.setStartDelay(200)
                    animator.start()
                } else {
                    statusBarView.isClickable = false
                    val animator = ObjectAnimator.ofFloat(statusBarView, "y", 0f)
                    animator.setDuration(200)
                    animator.start()
                }
            }
        }
    }

    fun appendStatusBarPadding(view: View?, viewHeight: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (view != null) {
                val statusBarHeight = getStatusBarHeight(view.context)
                view.setPadding(view.paddingLeft, statusBarHeight, view.paddingRight, view.paddingBottom)
                if (viewHeight > 0) {
                    view.layoutParams.height = statusBarHeight + viewHeight
                } else {
                    view.layoutParams.height = viewHeight
                }
            }
        }
    }

    fun removeStatusBarPadding(view: View?, viewHeight: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (view != null) {
                view.setPadding(view.paddingLeft, 0, view.paddingRight,
                        view.paddingBottom)
                view.layoutParams.height = viewHeight
            }
        }
    }

    private var statusBarHeight = -1

    fun getStatusBarHeight(context: Context): Int {
        if (statusBarHeight != -1) {
            return statusBarHeight
        }

        //获取status_bar_height资源的ID
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

    fun fetchContextColor(context: Context, androidAttribute: Int): Int {
        val typedValue = TypedValue()

        val a = context.obtainStyledAttributes(typedValue.data, intArrayOf(androidAttribute))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

}