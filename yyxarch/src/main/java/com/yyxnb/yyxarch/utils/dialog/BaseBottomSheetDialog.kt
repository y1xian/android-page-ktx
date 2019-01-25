package com.yyxnb.yyxarch.utils.dialog

import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.CoordinatorLayout
import android.view.Gravity
import android.view.View
import android.view.ViewGroup

class BaseBottomSheetDialog : BottomSheetDialog {

    private var mContentView: View? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, theme: Int) : super(context, theme) {}

    override fun onStart() {
        super.onStart()
        fixHeight()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        this.mContentView = view
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
        this.mContentView = view
    }

    private fun fixHeight() {
        if (null == mContentView) {
            return
        }

        val parent = mContentView!!.parent as View
        val behavior = BottomSheetBehavior.from(parent)
        mContentView!!.measure(0, 0)
        behavior.peekHeight = mContentView!!.measuredHeight

        val params = parent.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        parent.layoutParams = params
    }
}