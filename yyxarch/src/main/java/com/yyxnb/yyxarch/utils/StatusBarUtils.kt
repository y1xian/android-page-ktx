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
import android.view.WindowManager
import com.yyxnb.yyxarch.interfaces.BarStyle


/**
 * 状态栏
 */
object StatusBarUtils : Serializable {

    fun toGrey(rgb: Int): Int {
        val blue = rgb and 0x000000FF
        val green = rgb and 0x0000FF00 shr 8
        val red = rgb and 0x00FF0000 shr 16
        return red * 38 + green * 75 + blue * 15 shr 7
    }

    private val BRAND = Build.BRAND.toLowerCase()

    fun isHuawei(): Boolean = BRAND.contains("huawei") || BRAND.contains("honor")

    fun isXiaomi(): Boolean = Build.MANUFACTURER.toLowerCase() == "xiaomi"

    fun isVivo(): Boolean = BRAND.contains("vivo") || BRAND.contains("bbk")

    fun isOppo(): Boolean = BRAND.contains("oppo")


    //安全区域
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
    @JvmOverloads
    fun setStatusBarTranslucent(window: Window, translucent: Boolean = true, fitsSystemWindows: Boolean = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setRenderContentInShortEdgeCutoutAreas(window, translucent)

            val decorView = window.decorView
            if (translucent) {
                decorView.setOnApplyWindowInsetsListener { v, insets ->
                    val defaultInsets = v.onApplyWindowInsets(insets)
                    defaultInsets.replaceSystemWindowInsets(
                            defaultInsets.systemWindowInsetLeft,
                            if (fitsSystemWindows) defaultInsets.systemWindowInsetTop else 0,
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
    @JvmOverloads
    fun setStatusBarColor(window: Window, color: Int, animated: Boolean = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (animated) {
                val curColor = window.statusBarColor
                val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), curColor, color)
                colorAnimation.addUpdateListener { animator -> window.statusBarColor = animator.animatedValue as Int }
                colorAnimation.setDuration(100).startDelay = 0
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
                    colorAnimation.setDuration(100).startDelay = 0
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
                    animator.setDuration(100)
                    animator.setStartDelay(100)
                    animator.start()
                } else {
                    statusBarView.isClickable = false
                    val animator = ObjectAnimator.ofFloat(statusBarView, "y", 0f)
                    animator.setDuration(100)
                    animator.start()
                }
            }
        }
    }

    /**
     * 增加View的paddingTop,增加的值为状态栏高度
     */
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

    /**
     * 删除View的paddingTop,增加的值为状态栏高度
     */
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

    /**
     * 获取状态栏高度
     */
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