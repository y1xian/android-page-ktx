package com.yyxnb.yyxarch.utils.dialog

import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewPager
import android.view.View

object BottomSheetUtils {

    fun setupViewPager(viewPager: ViewPager) {
        val bottomSheetParent = findBottomSheetParent(viewPager)
        if (bottomSheetParent != null) {
            viewPager.addOnPageChangeListener(BottomSheetViewPagerListener(viewPager, bottomSheetParent))
        }
    }

    private class BottomSheetViewPagerListener (private val viewPager: ViewPager, bottomSheetParent: View) : ViewPager.SimpleOnPageChangeListener() {
        private val behavior: ViewPagerBottomSheetBehavior<View>

        init {
            this.behavior = ViewPagerBottomSheetBehavior.from(bottomSheetParent)
        }

        override fun onPageSelected(position: Int) {
            viewPager.post { behavior.invalidateScrollingChild() }
        }
    }

    private fun findBottomSheetParent(view: View): View? {
        var current: View? = view
        while (current != null) {
            val params = current.layoutParams
            if (params is CoordinatorLayout.LayoutParams && params.behavior is ViewPagerBottomSheetBehavior<*>) {
                return current
            }
            val parent = current.parent
            current = if (parent == null || parent !is View) null else parent
        }
        return null
    }

}
