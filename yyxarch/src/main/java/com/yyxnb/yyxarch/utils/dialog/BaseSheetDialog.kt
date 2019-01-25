package com.yyxnb.yyxarch.utils.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.annotation.FloatRange
import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout

import com.yyxnb.yyxarch.utils.ScreenUtils

/**
 * Description: BaseSheetDialog
 *
 * @author : yyx
 * @date ：2018/11/18
 */
abstract class BaseSheetDialog<T : BaseSheetDialog<T>> : BottomSheetDialogFragment() {

    private var mTag = "BaseSheetDialog"

    @LayoutRes
    private var mLayoutRes: Int = 0

    //点击外部是否可取消
    private var mIsCancelOnTouchOutside = true

    // 顶部向下偏移量
    private var mTopOffset = 0
    // 折叠的高度
    private var mPeekHeight = 0
    // 默认折叠高度 屏幕60%
    @FloatRange(from = 0.0, to = 1.0)
    private var mDefaultHeight = 0.6f

    //设置阴影透明度 默认0.5f
    @FloatRange(from = 0.0, to = 1.0)
    private var mDimAmount = DEFAULT_DIM

    private var mIsKeyboardEnable = true

    private var mIsTransparent = true

    private var mIsBehavior = true

    private var mHeight = DEFAULT_WH

    private var mWidth = DEFAULT_WH

    @StyleRes
    private var mAnimationStyle: Int = 0

    private var mSoftInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN

    // 初始为展开状态
    private var mState = ViewPagerBottomSheetBehavior.STATE_EXPANDED

    private lateinit var behavior: ViewPagerBottomSheetBehavior<FrameLayout>

    /**
     * 判断是否显示
     *
     * @return
     */
    val isShowing: Boolean
        get() = dialog != null && dialog.isShowing

    protected fun self(): T {

        return this as T
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //解决ViewPager + Fragment 无法滑动问题
        return ViewPagerBottomSheetDialog(context!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutRes(initLayoutId())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(mLayoutRes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
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
    protected fun initWindowLayoutParams(window: Window, layoutParams: WindowManager.LayoutParams) {
        if (mIsKeyboardEnable) {
            window.setSoftInputMode(mSoftInputMode)
        }

        if (mAnimationStyle > 0) {
            window.setWindowAnimations(mAnimationStyle)
        }

        //这样设置高度会发生布局错误
        //        if (mHeight > 0 || mHeight == ViewGroup.LayoutParams.MATCH_PARENT || mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
        //            layoutParams.height = mHeight;
        //        }

        if (mWidth > 0 || mWidth == ViewGroup.LayoutParams.MATCH_PARENT || mWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutParams.width = mWidth
        }

        layoutParams.dimAmount = mDimAmount
    }

    /**
     * 设置dialog布局
     *
     * @return
     */
    abstract fun initLayoutId(): Int

    /**
     * 初始化 Views
     *
     * @param view
     */
    abstract fun initViews(view: View)

    /**
     * 设置布局id
     *
     * @param layoutRes
     * @return
     */
    fun setLayoutRes(@LayoutRes layoutRes: Int): T {
        this.mLayoutRes = layoutRes
        return self()
    }

    /**
     * 设置背景是否透明
     *
     * @param mIsTransparent
     * @return
     */
    fun setIsTransparent(mIsTransparent: Boolean): T {
        this.mIsTransparent = mIsTransparent
        return self()
    }

    /**
     * 设置是否使用Behavior
     *
     * @param mIsBehavior
     * @return
     */
    fun setIsBehavior(mIsBehavior: Boolean): T {
        this.mIsBehavior = mIsBehavior
        return self()
    }

    /**
     * 显示Dialog
     *
     * @param fragmentManager
     */
    fun show(fragmentManager: FragmentManager) {
        if (!isAdded) {
            show(fragmentManager, mTag)
        }
    }

    /**
     * 设置点击 Dialog 之外的地方是否消失
     *
     * @param isCancelOnTouchOutside
     * @return
     */
    fun setCancelOnTouchOutside(isCancelOnTouchOutside: Boolean): T {
        this.mIsCancelOnTouchOutside = isCancelOnTouchOutside
        return self()
    }

    /**
     * 设置顶部向下偏移量
     *
     * @param mTopOffset
     * @return
     */
    fun setTopOffset(mTopOffset: Int): T {
        this.mTopOffset = mTopOffset
        return self()
    }

    /**
     * 设置高度
     *
     * @param mPeekHeight
     * @return
     */
    fun setPeekHeight(mPeekHeight: Int): T {
        this.mPeekHeight = mPeekHeight
        return self()
    }

    /**
     * 设置默认高度
     *
     * @param mDefaultHeight
     * @return
     */
    fun setDefaultHeight(@FloatRange(from = 0.0, to = 1.0) mDefaultHeight: Float): T {
        this.mDefaultHeight = mDefaultHeight
        return self()
    }

    /**
     * 设置状态
     *
     * @param mState
     * @return
     */
    fun setState(mState: Int): T {
        this.mState = mState
        return self()
    }

    /**
     * 设置显示时的 Fragment Tag
     *
     * @param tag
     * @return
     */
    fun setFragmentTag(tag: String): T {
        this.mTag = tag
        return self()
    }

    fun getFragmentTag(): String {
        return mTag
    }

    /**
     * 设置阴影透明度
     *
     * @param dimAmount
     * @return
     */
    fun setDimAmount(@FloatRange(from = 0.0, to = 1.0) dimAmount: Float): T {
        this.mDimAmount = dimAmount
        return self()
    }

    /**
     * 设置 Dialog 高度
     *
     * @param height
     * @return
     */
    fun setHeight(height: Int): T {
        this.mHeight = height
        return self()
    }

    /**
     * 设置 Dialog 宽度
     *
     * @param width
     * @return
     */
    fun setWidth(width: Int): T {
        this.mWidth = width
        return self()
    }

    /**
     * 设置 Dialog 显示动画
     *
     * @param animationStyle
     * @return
     */
    fun setAnimationStyle(@StyleRes animationStyle: Int): T {
        this.mAnimationStyle = animationStyle
        return self()
    }

    /**
     * 设置是否支持弹出键盘调整位置
     *
     * @param keyboardEnable
     * @return
     */
    fun setKeyboardEnable(keyboardEnable: Boolean): T {
        this.mIsKeyboardEnable = keyboardEnable
        return self()
    }

    /**
     * 设置弹出键盘时调整方式
     *
     * @param inputMode
     * @return
     */
    fun setSoftInputMode(inputMode: Int): T {
        this.mSoftInputMode = inputMode
        return self()
    }

    companion object {

        private val DEFAULT_DIM = 0.5f
        private val DEFAULT_WH = ViewGroup.LayoutParams.MATCH_PARENT
    }
}
