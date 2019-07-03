package com.yyxnb.yyxarch.common

import android.graphics.Color
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.R
import com.yyxnb.yyxarch.utils.BarStyle
import com.yyxnb.yyxarch.utils.StatusBarUtils
import java.io.Serializable

/**
 * Description: 相关配置属性
 *
 * @author : yyx
 * @date ：2018/6/13
 */
object AppConfig : Serializable {

    const val FRAGMENT = "FRAGMENT"
    const val BUNDLE = "BUNDLE"
    const val REQUEST_CODE = "REQUEST_CODE"

    /**
     * 是否侧滑 fragment
     */
    var swipeBackEnabled: Boolean = true
    /**
     * 状态栏文字颜色
     */
    var statusBarStyle = BarStyle.LightContent
    /**
     * 状态栏是否隐藏
     */
    var statusBarHidden: Boolean = false
    /**
     * 状态栏颜色
     */
    var statusBarColor: Int = StatusBarUtils.fetchContextColor(AppUtils.context, R.attr.colorPrimaryDark)
    /**
     * 如果状态栏处于白色且状态栏文字也处于白色，避免看不见
     */
    var shouldAdjustForWhiteStatusBar: Int = Color.parseColor("#4A4A4A")
    /**
     * 虚拟键颜色
     */
    var navigationBarColor: Int = Color.TRANSPARENT


}
