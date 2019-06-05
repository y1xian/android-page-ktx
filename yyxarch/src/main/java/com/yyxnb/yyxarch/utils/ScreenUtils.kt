package com.yyxnb.yyxarch.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import androidx.annotation.NonNull
import com.yyxnb.yyxarch.AppUtils
import java.io.Serializable


@SuppressLint("StaticFieldLeak")
object ScreenUtils : Serializable {

    private val mContext = AppUtils.context


    /**
     * 获取状态栏高度
     * @param context
     * @return 状态栏高度  默认为0
     */
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 获取toolbar的高度
     * @param context
     *
     * @return 默认为0
     */
    fun getToolbarHeight(context: Context): Int {
        val typedValue = TypedValue()

        return if (context.theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            TypedValue.complexToDimensionPixelSize(typedValue.data, context.resources.displayMetrics)
        } else 0

    }

    /**
     * 获得屏幕高度
     * @param context
     * @return
     */
    @JvmOverloads
    fun getScreenWidth(context: Context = mContext): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    /**
     * 获得屏幕宽度
     * @param context
     * @return
     */
    @JvmOverloads
    fun getScreenHeight(context: Context = mContext): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.heightPixels
    }

    /**
     * 根据手机分辨率将dp转为px单位
     */
    fun dp2px(dpValue: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, mContext.resources.displayMetrics)
    }

    /**
     * 根据手机分辨率将sp转为px单位
     */
    fun sp2px(pxValue: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, pxValue, mContext.resources.displayMetrics)
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dp(pxValue: Float): Int {
        val scale = mContext.resources
                .displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    fun px2sp(pxValue: Float): Int {
        val scale = mContext.resources.displayMetrics.scaledDensity
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 获取当前屏幕截图 包含状态栏
     *
     * @param activity
     *
     * @return
     */
    fun snapShotWithStatusBar(@NonNull activity: Activity): Bitmap? {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bmp = view.drawingCache
        val width = getScreenWidth(activity)
        val height = getScreenHeight(activity)
        var bp: Bitmap? = null
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height)
        view.destroyDrawingCache()
        return bp
    }

    /**
     * 获取当前屏幕截图 但不包含状态栏
     *
     * @param activity
     *
     * @return
     */
    fun snapShotWithoutStatusBar(@NonNull activity: Activity): Bitmap? {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bmp = view.drawingCache
        val frame = Rect()
        view.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top
        val width = getScreenWidth(activity)
        val height = getScreenHeight(activity)
        var bp: Bitmap? = null
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight)
        view.destroyDrawingCache()
        return bp
    }

}