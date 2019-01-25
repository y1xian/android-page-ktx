package com.yyxnb.yyxarch.ext

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.CompoundButton

/**
 * @author TuFei
 * @date 18-10-10.
 */

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
fun View.showSoftKeyboard() {
    if (requestFocus()) {
        val imm = context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

/**
 * 手动隐藏软键盘
 */
fun View.closeSoftKeyboard() {
    val imm = context.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)

    /**
     * inflate view
     *
     * @param[layoutRes]    layout resource to inflate
     * @param[parent]       Optional view to be the parent of the generated hierarchy (if attachToRoot is true),
     *                      or else simply an object that provides a set of LayoutParams values for root of the returned hierarchy
     *                      (if attachToRoot is false.)
     *                      This value may be null.
     * @param[attachToRoot] Whether the inflated hierarchy should be attached to the root parameter?
     *                      If false, root is only used to create the correct subclass of LayoutParams for
     *                      the root view in the XML.
     * @return The root View of the inflated hierarchy.
     */
    fun Context.inflate(layoutRes: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false): View =
            LayoutInflater.from(this).inflate(layoutRes, parent, attachToRoot)
}