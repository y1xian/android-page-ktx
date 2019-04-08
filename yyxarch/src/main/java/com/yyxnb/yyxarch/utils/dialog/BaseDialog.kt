package com.yyxnb.yyxarch.utils.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StyleRes
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatDialogFragment
import android.view.*
import com.yyxnb.yyxarch.R
import java.util.*

/**
 * Description: BaseDialog
 *
 * @author : yyx
 * @date ：2018/11/18
 */
abstract class BaseDialog : AppCompatDialogFragment() {

    @LayoutRes
    protected var mLayoutRes: Int = 0

    /**
     * 点击外部是否可取消
     */
    protected var mIsCancelOnTouchOutside = true

    protected var mTag = "BaseDialog"

    /**
     * 阴影透明度 默认0.5f
     */
//    @FloatRange(from = 0.0, to = 1.0)
    protected var mDimAmount = DEFAULT_DIM_AMOUNT

    protected var mHeight = DEFAULT_WH

    protected var mWidth = DEFAULT_WH

    @StyleRes
    protected var mAnimationStyle: Int = 0

    protected var mIsKeyboardEnable = true

    protected var mSoftInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING

    private var mGravity = Gravity.CENTER

    private var mOnCancelListener: DialogInterface.OnCancelListener? = null
    private var mOnDismissListener: DialogInterface.OnDismissListener? = null

    protected var mActivity: FragmentActivity? = null

    /**
     * 判断是否显示
     *
     * @return
     */
    val isShowing: Boolean
        get() = dialog != null && dialog.isShowing

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is FragmentActivity) {
            mActivity = context as FragmentActivity?
        }
    }

    override fun onDestroy() {
        mActivity = null
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置Style 透明背景，No Title
        setStyle(AppCompatDialogFragment.STYLE_NORMAL, R.style.BaseDialog)
        savedInstanceState?.let {
            mIsCancelOnTouchOutside = it.getBoolean(KEY_IS_CANCEL_ON_TOUCH_OUTSIDE, true)
            mDimAmount = it.getFloat(KEY_DIM_AMOUNT, DEFAULT_DIM_AMOUNT)
            mHeight = it.getInt(KEY_HEIGHT, 0)
            mWidth = it.getInt(KEY_WIDTH, 0)
            mAnimationStyle = it.getInt(KEY_ANIMATION_STYLE, 0)
            mIsKeyboardEnable = it.getBoolean(KEY_IS_KEYBOARD_ENABLE, true)
            mSoftInputMode = it.getInt(KEY_SOFT_INPUT_MODE, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            mGravity = it.getInt(KEY_GRAVITY, Gravity.CENTER)
        }

        initArguments(arguments)
        setLayoutRes(initLayoutId())
    }

    /**
     * 在此方法中获取参数
     *
     * @param arguments
     */
    protected fun initArguments(arguments: Bundle?) {
        //getArguments
    }

    /**
     * 屏幕旋转时回调
     *
     *
     * 修复屏幕旋转时各种回调等数据为null
     * http://www.yrom.net/blog/2014/11/02/dialogfragment-retaining-listener-after-screen-rotation/
     *
     * @param outState
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_IS_CANCEL_ON_TOUCH_OUTSIDE, mIsCancelOnTouchOutside)
        outState.putFloat(KEY_DIM_AMOUNT, mDimAmount)
        outState.putInt(KEY_HEIGHT, mHeight)
        outState.putInt(KEY_WIDTH, mWidth)
        outState.putInt(KEY_ANIMATION_STYLE, mAnimationStyle)
        outState.putBoolean(KEY_IS_KEYBOARD_ENABLE, mIsKeyboardEnable)
        outState.putInt(KEY_SOFT_INPUT_MODE, mSoftInputMode)
        outState.putInt(KEY_GRAVITY, mGravity)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(mLayoutRes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    override fun onStart() {
        super.onStart()

        if (dialog == null || dialog.window == null) {
            return
        }
        dialog.setCanceledOnTouchOutside(mIsCancelOnTouchOutside)
        val window = dialog.window
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
    protected open fun initWindowLayoutParams(window: Window, layoutParams: WindowManager.LayoutParams) {
        if (mIsKeyboardEnable) {
            window.setSoftInputMode(mSoftInputMode)
            Objects.requireNonNull<FragmentActivity>(activity).window.setSoftInputMode(mSoftInputMode)
        }

        if (mAnimationStyle > 0) {
            window.setWindowAnimations(mAnimationStyle)
        }

        if (mHeight > 0 || mHeight == ViewGroup.LayoutParams.MATCH_PARENT || mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutParams.height = mHeight
        }
        if (mWidth > 0 || mWidth == ViewGroup.LayoutParams.MATCH_PARENT || mWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutParams.width = mWidth
        }

        layoutParams.dimAmount = mDimAmount
        layoutParams.gravity = mGravity
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        if (mOnCancelListener != null) {
            mOnCancelListener!!.onCancel(dialog)
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        if (mOnDismissListener != null) {
            mOnDismissListener!!.onDismiss(dialog)
        }
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
    fun setLayoutRes(@LayoutRes layoutRes: Int) {
        this.mLayoutRes = layoutRes
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
    fun setCancelOnTouchOutside(isCancelOnTouchOutside: Boolean): BaseDialog {
        this.mIsCancelOnTouchOutside = isCancelOnTouchOutside
        return this
    }

    /**
     * 设置显示时的 Fragment Tag
     *
     * @param tag
     * @return
     */
    fun setFragmentTag(tag: String): BaseDialog {
        this.mTag = tag
        return this
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
    fun setDimAmount(/*@FloatRange(from = 0.0, to = 1.0)*/ dimAmount: Float): BaseDialog {
        this.mDimAmount = dimAmount
        return this
    }

    /**
     * 设置 Dialog 高度
     *
     * @param height
     * @return
     */
    fun setHeight(height: Int): BaseDialog {
        this.mHeight = height
        return this
    }

    /**
     * 设置 Dialog 宽度
     *
     * @param width
     * @return
     */
    fun setWidth(width: Int): BaseDialog {
        this.mWidth = width
        return this
    }

    /**
     * 设置 Dialog 显示动画
     *
     * @param animationStyle
     * @return
     */
    fun setAnimationStyle(@StyleRes animationStyle: Int): BaseDialog {
        this.mAnimationStyle = animationStyle
        return this
    }

    /**
     * 设置是否支持弹出键盘调整位置
     *
     * @param keyboardEnable
     * @return
     */
    fun setKeyboardEnable(keyboardEnable: Boolean): BaseDialog {
        this.mIsKeyboardEnable = keyboardEnable
        return this
    }

    /**
     * 设置弹出键盘时调整方式
     *
     * @param inputMode
     * @return
     */
    fun setSoftInputMode(inputMode: Int): BaseDialog {
        this.mSoftInputMode = inputMode
        return this
    }

    /**
     * 设置 Dialog 对齐方式
     *
     * @param gravity
     * @return
     */
    fun setGravity(gravity: Int): BaseDialog {
        this.mGravity = gravity
        return this
    }

    /**
     * 设置OnCancelListener
     *
     * @param listener
     * @return
     */
    fun setOnCancelListener(listener: DialogInterface.OnCancelListener?): BaseDialog {
        this.mOnCancelListener = listener
        return this
    }

    /**
     * 设置OnDismissListener
     *
     * @param listener
     * @return
     */
    fun setOnDismissListener(listener: DialogInterface.OnDismissListener?): BaseDialog {
        this.mOnDismissListener = listener
        return this
    }

    /**
     * 设置是否可以取消
     * （单纯的是为了链式调用）
     *
     * @param cancelable
     * @return
     */
    fun setDialogCancelable(cancelable: Boolean): BaseDialog {
        isCancelable = cancelable
        return this
    }

    /**
     * 清除匿名内部类 callback 对外部类的引用，避免可能导致的内存泄漏
     */
    fun clearRefOnDestroy() {
        //清除 onDismissListener 引用
        setOnDismissListener(null)
        //清除 onCancelListener 引用
        setOnCancelListener(null)
    }

    companion object {

        private val DEFAULT_DIM_AMOUNT = 0.5f
        private val DEFAULT_WH = ViewGroup.LayoutParams.WRAP_CONTENT

        private val KEY_IS_CANCEL_ON_TOUCH_OUTSIDE = "keyIsCancelOnTouchOutside"
        private val KEY_DIM_AMOUNT = "keyDimAmount"
        private val KEY_HEIGHT = "keyHeight"
        private val KEY_WIDTH = "keyWidth"
        private val KEY_ANIMATION_STYLE = "keyAnimationStyle"
        private val KEY_IS_KEYBOARD_ENABLE = "keyIsKeyboardEnable"
        private val KEY_SOFT_INPUT_MODE = "keySoftInputMode"
        private val KEY_GRAVITY = "keyGravity"
    }
}