package com.yyxnb.yyxarch.utils.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatDialog
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import com.yyxnb.yyxarch.utils.ScreenUtils
import java.util.*

/**
 * Description: BaseSheetDialog
 *
 * @author : yyx
 * @date ：2018/11/18
 */
abstract class BaseSheetDialog : BaseDialog() {

    /**
     * 顶部向下偏移量
     */
    private var mTopOffset = 0
    /**
     * 折叠的高度
     */
    private var mPeekHeight = 0
    /**
     * 默认折叠高度 屏幕60%
     */
    private var mDefaultHeight = 0.6f

    private var mIsTransparent = true

    private var mIsBehavior = true

    /**
     * 初始为展开状态
     */
    private var mState = ViewPagerBottomSheetBehavior.STATE_EXPANDED

    private var behavior: ViewPagerBottomSheetBehavior<FrameLayout>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //解决ViewPager + Fragment 无法滑动问题
        return ViewPagerBottomSheetDialog(Objects.requireNonNull<Context>(context))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        //这样设置高度才会正确展示
        view.layoutParams = ViewGroup.LayoutParams(DEFAULT_WH, mHeight)
    }

    override fun onStart() {
        super.onStart()
        if (dialog == null || dialog.window == null) {
            return
        }
        val dialog = dialog as AppCompatDialog
        val bottomSheet = dialog.delegate.findViewById<FrameLayout>(android.support.design.R.id.design_bottom_sheet)
        if (bottomSheet != null && mIsBehavior) {
            val layoutParams = bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
            behavior = ViewPagerBottomSheetBehavior.from(bottomSheet)
            if (mHeight <= 0) {
                //高度 = 屏幕高度 - 顶部向下偏移量
                layoutParams.height = ScreenUtils.screenHeight - mTopOffset
                //如果顶部向下偏移量为0
                if (mTopOffset == 0) {
                    //如果默认高度为0
                    if (mPeekHeight == 0) {
                        //则 默认高度60% 且为折叠状态
                        mPeekHeight = (ScreenUtils.screenHeight * mDefaultHeight).toInt()
                        //折叠
                        mState = BottomSheetBehavior.STATE_COLLAPSED
                    }
                } else {
                    //如果顶部向下偏移量不为0 ，则为高度 = 屏幕高度 - 顶部向下偏移量
                    mPeekHeight = layoutParams.height
                }
            }
            behavior!!.peekHeight = if (mHeight > 0) mHeight else mPeekHeight
            // 初始为展开
            behavior!!.state = mState
        }
        if (mIsTransparent) {
            assert(bottomSheet != null)
            bottomSheet!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        getDialog().setCanceledOnTouchOutside(mIsCancelOnTouchOutside)
        val window = getDialog().window
        val layoutParams = window!!.attributes

        initWindowLayoutParams(window, layoutParams)
        window.attributes = layoutParams
    }

    /**
     * 初始化 Window 和 LayoutParams 参数
     *
     * @param window
     * @param layoutParams
     */
    @SuppressLint("ResourceType")
    override fun initWindowLayoutParams(window: Window, layoutParams: WindowManager.LayoutParams) {

        if (mIsKeyboardEnable) {
            window.setSoftInputMode(mSoftInputMode)
            Objects.requireNonNull<FragmentActivity>(activity).getWindow().setSoftInputMode(mSoftInputMode)
        }

        if (mAnimationStyle > 0) {
            window.setWindowAnimations(mAnimationStyle)
        }

        //这样设置高度会发生布局错误
        //        if (mHeight > 0 || mHeight == ViewGroup.LayoutParams.MATCH_PARENT || mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
        //            layoutParams.height = mHeight;
        //        }

        //        if (mWidth > 0 || mWidth == ViewGroup.LayoutParams.MATCH_PARENT || mWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
        //            layoutParams.width = mWidth;
        //        }
        if (mWidth > 0) {
            layoutParams.width = mWidth
        }

        layoutParams.dimAmount = mDimAmount
    }

    /**
     * 设置背景是否透明
     *
     * @param mIsTransparent
     * @return
     */
    fun setIsTransparent(mIsTransparent: Boolean): BaseSheetDialog {
        this.mIsTransparent = mIsTransparent
        return this
    }

    /**
     * 设置是否使用Behavior
     *
     * @param mIsBehavior
     * @return
     */
    fun setIsBehavior(mIsBehavior: Boolean): BaseSheetDialog {
        this.mIsBehavior = mIsBehavior
        return this
    }

    /**
     * 设置顶部向下偏移量
     *
     * @param mTopOffset
     * @return
     */
    fun setTopOffset(mTopOffset: Int): BaseSheetDialog {
        this.mTopOffset = mTopOffset
        return this
    }

    /**
     * 设置折叠高度
     *
     * @param mPeekHeight
     * @return
     */
    fun setPeekHeight(mPeekHeight: Int): BaseSheetDialog {
        this.mPeekHeight = mPeekHeight
        return this
    }

    /**
     * 设置默认高度
     *
     * @param mDefaultHeight
     * @return
     */
    fun setDefaultHeight(mDefaultHeight: Float): BaseSheetDialog {
        this.mDefaultHeight = mDefaultHeight
        return this
    }

    /**
     * 设置状态
     *
     * @param mState
     * @return
     */
    fun setState(mState: Int): BaseSheetDialog {
        this.mState = mState
        return this
    }

    companion object {

        private val DEFAULT_WH = ViewGroup.LayoutParams.MATCH_PARENT
    }

}
