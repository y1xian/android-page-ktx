package com.yyxnb.yyxarch.ext

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.CompoundButton
import com.yyxnb.yyxarch.ext.ViewClickDelay.DELAY_TIME
import com.yyxnb.yyxarch.ext.ViewClickDelay.hash
import com.yyxnb.yyxarch.ext.ViewClickDelay.lastClickTime

/**
 * @author TuFei
 * @date 18-10-10.
 */

object ViewClickDelay {
    var hash: Int = 0
    var lastClickTime: Long = 0
    var DELAY_TIME: Long = 500L
}

/**
 * 防止多次点击
 */
fun View.clickDelay(delay: Long = DELAY_TIME, clickAction: () -> Unit) {
    this.setOnClickListener {
        if (this.hashCode() != hash) {
            hash = this.hashCode()
            lastClickTime = System.currentTimeMillis()
            clickAction()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > delay) {
                lastClickTime = System.currentTimeMillis()
                clickAction()
            }
        }
    }
}

/**
 * 1.activity、fragment等要先实现View.OnClickListener
 * 2.在activity、fragment里面直接调用该扩展方法，给多个控件设置点击事件
 *
 * 多个控件设置setOnClickListener
 */
fun View.OnClickListener.setOnClickListener(vararg ids: View?) {
    ids.filterNotNull().forEach {
        it.setOnClickListener(this)
    }
}

/**
 * 设置多个View的显示和隐藏
 *
 * @param visibility 显示或隐藏
 * @param ids View
 */
fun View.setVisibility(visibility: Int, vararg ids: View) {
    ids.forEach {
        it.visibility = visibility
    }
}

/**
 * 多个CheckBox设置setOnCheckedChangeListener
 */
fun CompoundButton.OnCheckedChangeListener.setOnCheckedChangeListener(vararg ids: CheckBox?) {
    ids.filterNotNull().forEach {
        it.setOnCheckedChangeListener(this)
    }
}

/**
 * 多个CheckBox设置setOnCheckedChangeListener
 */
fun CompoundButton.OnCheckedChangeListener.setOnCheckedChangeListener(ids: List<CheckBox?>) {
    ids.filterNotNull().forEach {
        it.setOnCheckedChangeListener(this)
    }
}

/**
 * 手动显示软键盘
 */
fun View.showKeyboard() {
    if (requestFocus()) {
        val imm = context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

/**
 * 手动隐藏软键盘
 */
fun View.hideKeyBoard() {
    val imm = context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Context.inflate(layoutRes: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false): View =
        LayoutInflater.from(this).inflate(layoutRes, parent, attachToRoot)

inline var View.isVisible: Boolean
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else GONE
    }

inline var View.isHidden: Boolean
    get() = visibility == GONE
    set(value) {
        visibility = if (value) GONE else VISIBLE
    }

inline var View.isInvisible: Boolean
    get() = visibility == INVISIBLE
    set(value) {
        visibility = if (value) INVISIBLE else VISIBLE
    }

fun View.show() {
    visibility = VISIBLE
}

fun View.hide() {
    visibility = GONE
}

fun View.invisible() {
    visibility = INVISIBLE
}