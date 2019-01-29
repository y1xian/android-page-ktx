package com.yyxnb.yyxarch.utils.popup

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.RequiresApi
import android.support.annotation.StyleRes
import android.support.v4.widget.PopupWindowCompat
import android.transition.Transition
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroupOverlay
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.PopupWindow

import com.yyxnb.yyxarch.annotation.XGravity
import com.yyxnb.yyxarch.annotation.YGravity

/**
 * Description: BasePopup
 *
 * @author : yyx
 * @date ：2018/11/18
 */
abstract class BasePopup<T : BasePopup<T>> : PopupWindow.OnDismissListener {

    //PopupWindow对象
    /**
     * 获取PopupWindow对象
     *
     * @return
     */
    private lateinit var popupWindow: PopupWindow

    //context
    private lateinit var mContext: Context
    //contentView
    private lateinit var mContentView: View
    //布局id
    private var mLayoutId: Int = 0
    //获取焦点
    private var mFocusable = true
    //是否触摸之外dismiss
    private var mOutsideTouchable = true

    //宽高
    private var mWidth = DEFAULT_WH
    private var mHeight = DEFAULT_WH

    @StyleRes
    private var mAnimationStyle: Int = 0

    private lateinit var mOnDismissListener: PopupWindow.OnDismissListener

    //弹出pop时，背景是否变暗
    private var isBackgroundDim: Boolean = false

    //背景变暗时透明度
    @FloatRange(from = 0.0, to = 1.0)
    private var mDimValue = DEFAULT_DIM
    //背景变暗颜色
    @ColorInt
    private var mDimColor = Color.BLACK

    //背景变暗的view
    private lateinit var mDimView: ViewGroup

    private lateinit var mEnterTransition: Transition
    private lateinit var mExitTransition: Transition

    private var mFocusAndOutsideEnable = true

    private lateinit var mAnchorView: View
    @YGravity
    private var mYGravity = YGravity.BELOW
    @XGravity
    private var mXGravity = XGravity.LEFT
    private var mOffsetX: Int = 0
    private var mOffsetY: Int = 0

    private var mInputMethodMode = PopupWindow.INPUT_METHOD_FROM_FOCUSABLE
    private var mSoftInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED

    //是否重新测量宽高
    private var isNeedReMeasureWH = false
    //真实的宽高是否已经准备好
    /**
     * 是否精准的宽高获取完成
     *
     * @return
     */
    private var isRealWHAlready = false

    private var isAtAnchorViewMethod = false

    private lateinit var mOnRealWHAlreadyListener: OnRealWHAlreadyListener

    /**
     * 获取PopupWindow中加载的view
     *
     * @return
     */
    val contentView: View?
        get() = if (popupWindow != null) {
            popupWindow!!.contentView
        } else {
            null
        }

    /**
     * 是否正在显示
     *
     * @return
     */
    val isShowing: Boolean
        get() = popupWindow != null && popupWindow!!.isShowing

    protected fun self(): T {

        return this as T
    }

    fun apply(): T {
        if (popupWindow == null) {
            popupWindow = PopupWindow()
        }

        onPopupWindowCreated()

        initContentViewAndWH()

        onPopupWindowViewCreated(mContentView)

        if (mAnimationStyle != 0) {
            popupWindow!!.animationStyle = mAnimationStyle
        }

        initFocusAndBack()
        popupWindow!!.setOnDismissListener(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mEnterTransition != null) {
                popupWindow!!.enterTransition = mEnterTransition
            }

            if (mExitTransition != null) {
                popupWindow!!.exitTransition = mExitTransition
            }
        }

        return self()
    }

    private fun initContentViewAndWH() {
        if (mContentView == null) {
            if (mLayoutId != 0 && mContext != null) {
                mContentView = LayoutInflater.from(mContext).inflate(mLayoutId, null)
            } else {
                throw IllegalArgumentException("The content view is null,the layoutId=$mLayoutId,context=$mContext")
            }
        }
        popupWindow!!.contentView = mContentView

        if (mWidth > 0 || mWidth == ViewGroup.LayoutParams.WRAP_CONTENT || mWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
            popupWindow!!.width = mWidth
        } else {
            popupWindow!!.width = ViewGroup.LayoutParams.WRAP_CONTENT
        }

        if (mHeight > 0 || mHeight == ViewGroup.LayoutParams.WRAP_CONTENT || mHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            popupWindow!!.height = mHeight
        } else {
            popupWindow!!.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        //测量contentView大小
        //可能不准
        measureContentView()
        //获取contentView的精准大小
        registerOnGlobalLayoutListener()

        popupWindow!!.inputMethodMode = mInputMethodMode
        popupWindow!!.softInputMode = mSoftInputMode
    }

    private fun initFocusAndBack() {
        if (!mFocusAndOutsideEnable) {
            //from https://github.com/pinguo-zhouwei/CustomPopwindow
            popupWindow!!.isFocusable = true
            popupWindow!!.isOutsideTouchable = false
            popupWindow!!.setBackgroundDrawable(null)
            //注意下面这三个是contentView 不是PopupWindow，响应返回按钮事件
            popupWindow!!.contentView.isFocusable = true
            popupWindow!!.contentView.isFocusableInTouchMode = true
            popupWindow!!.contentView.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popupWindow!!.dismiss()

                    return@OnKeyListener true
                }
                false
            })
            //在Android 6.0以上 ，只能通过拦截事件来解决
            popupWindow!!.setTouchInterceptor(View.OnTouchListener { v, event ->
                val x = event.x.toInt()
                val y = event.y.toInt()

                if (event.action == MotionEvent.ACTION_DOWN && (x < 0 || x >= mWidth || y < 0 || y >= mHeight)) {
                    //outside
                    Log.d(mTag, "onTouch outside:mWidth=$mWidth,mHeight=$mHeight")
                    return@OnTouchListener true
                } else if (event.action == MotionEvent.ACTION_OUTSIDE) {
                    //outside
                    Log.d(mTag, "onTouch outside event:mWidth=$mWidth,mHeight=$mHeight")
                    return@OnTouchListener true
                }
                false
            })
        } else {
            popupWindow!!.isFocusable = mFocusable
            popupWindow!!.isOutsideTouchable = mOutsideTouchable
            popupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    /****自定义生命周期方法 */

    /**
     * PopupWindow对象创建完成
     */
    protected fun onPopupWindowCreated() {
        //执行设置PopupWindow属性也可以通过Builder中设置
        //        setContentView(x,x,x);
        //...
        initAttributes()
    }

    protected fun onPopupWindowViewCreated(contentView: View?) {
        initViews(contentView, self())
    }

    protected fun onPopupWindowDismiss() {}

    /**
     * 可以在此方法中设置PopupWindow需要的属性
     */
    protected abstract fun initAttributes()

    /**
     * 初始化view {@see getView()}
     *
     * @param view
     */
    protected abstract fun initViews(view: View?, popup: T)

    /**
     * 是否需要测量 contentView的大小
     * 如果需要重新测量并为宽高赋值
     * 注：此方法获取的宽高可能不准确 MATCH_PARENT时无法获取准确的宽高
     */
    private fun measureContentView() {
        val contentView = contentView
        if (mWidth <= 0 || mHeight <= 0) {
            //测量大小
            contentView!!.measure(0, View.MeasureSpec.UNSPECIFIED)
            if (mWidth <= 0) {
                mWidth = contentView.measuredWidth
            }
            if (mHeight <= 0) {
                mHeight = contentView.measuredHeight
            }
        }
    }

    /**
     * 注册GlobalLayoutListener 获取精准的宽高
     */
    private fun registerOnGlobalLayoutListener() {
        contentView!!.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                contentView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mWidth = contentView!!.width
                mHeight = contentView!!.height

                isRealWHAlready = true
                isNeedReMeasureWH = false

                if (mOnRealWHAlreadyListener != null) {
                    mOnRealWHAlreadyListener!!.onRealWHAlready(this@BasePopup, mWidth, mHeight,
                            if (mAnchorView == null) 0 else mAnchorView!!.width, if (mAnchorView == null) 0 else mAnchorView!!.height)
                }
                //                Log.d(mTag, "onGlobalLayout finished. isShowing=" + isShowing());
                if (isShowing && isAtAnchorViewMethod) {
                    updateLocation(mWidth, mHeight, mAnchorView!!, mYGravity, mXGravity, mOffsetX, mOffsetY)
                }
            }
        })
    }

    /**
     * 更新 PopupWindow 到精准的位置
     *
     * @param width
     * @param height
     * @param anchor
     * @param yGravity
     * @param xGravity
     * @param x
     * @param y
     */
    private fun updateLocation(width: Int, height: Int, anchor: View, @YGravity yGravity: Int, @XGravity xGravity: Int, x: Int, y: Int) {
        var x = x
        var y = y
        if (popupWindow == null) {
            return
        }
        x = calculateX(anchor, xGravity, width, x)
        y = calculateY(anchor, yGravity, height, y)
        popupWindow!!.update(anchor, x, y, width, height)
    }

    /****设置属性方法 */

    fun setContext(context: Context): T {
        this.mContext = context
        return self()
    }

    fun setContentView(contentView: View): T {
        this.mContentView = contentView
        this.mLayoutId = 0
        return self()
    }

    fun setContentView(@LayoutRes layoutId: Int): T {
        this.mContentView = null!!
        this.mLayoutId = layoutId
        return self()
    }

    fun setContentView(context: Context, @LayoutRes layoutId: Int): T {
        this.mContext = context
        this.mContentView = null!!
        this.mLayoutId = layoutId
        return self()
    }

    fun setContentView(contentView: View, width: Int, height: Int): T {
        this.mContentView = contentView
        this.mLayoutId = 0
        this.mWidth = width
        this.mHeight = height
        return self()
    }

    fun setContentView(@LayoutRes layoutId: Int, width: Int, height: Int): T {
        this.mContentView = null!!
        this.mLayoutId = layoutId
        this.mWidth = width
        this.mHeight = height
        return self()
    }

    fun setContentView(context: Context, @LayoutRes layoutId: Int, width: Int, height: Int): T {
        this.mContext = context
        this.mContentView = null!!
        this.mLayoutId = layoutId
        this.mWidth = width
        this.mHeight = height
        return self()
    }

    fun setWidth(width: Int): T {
        this.mWidth = width
        return self()
    }

    fun setHeight(height: Int): T {
        this.mHeight = height
        return self()
    }

    fun setAnchorView(view: View): T {
        this.mAnchorView = view
        return self()
    }

    fun setYGravity(@YGravity yGravity: Int): T {
        this.mYGravity = yGravity
        return self()
    }

    fun setXGravity(@XGravity xGravity: Int): T {
        this.mXGravity = xGravity
        return self()
    }

    fun setOffsetX(offsetX: Int): T {
        this.mOffsetX = offsetX
        return self()
    }

    fun setOffsetY(offsetY: Int): T {
        this.mOffsetY = offsetY
        return self()
    }

    fun setAnimationStyle(@StyleRes animationStyle: Int): T {
        this.mAnimationStyle = animationStyle
        return self()
    }

    fun setFocusable(focusable: Boolean): T {
        this.mFocusable = focusable
        return self()
    }

    fun setOutsideTouchable(outsideTouchable: Boolean): T {
        this.mOutsideTouchable = outsideTouchable
        return self()
    }

    /**
     * 是否可以点击PopupWindow之外的地方dismiss
     *
     * @param focusAndOutsideEnable
     * @return
     */
    fun setFocusAndOutsideEnable(focusAndOutsideEnable: Boolean): T {
        this.mFocusAndOutsideEnable = focusAndOutsideEnable
        return self()
    }

    /**
     * 背景变暗支持api>=18
     *
     * @param isDim
     * @return
     */
    fun setBackgroundDimEnable(isDim: Boolean): T {
        this.isBackgroundDim = isDim
        return self()
    }

    fun setDimValue(@FloatRange(from = 0.0, to = 1.0) dimValue: Float): T {
        this.mDimValue = dimValue
        return self()
    }

    fun setDimColor(@ColorInt color: Int): T {
        this.mDimColor = color
        return self()
    }

    fun setDimView(dimView: ViewGroup): T {
        this.mDimView = dimView
        return self()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun setEnterTransition(enterTransition: Transition): T {
        this.mEnterTransition = enterTransition
        return self()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun setExitTransition(exitTransition: Transition): T {
        this.mExitTransition = exitTransition
        return self()
    }

    fun setInputMethodMode(mode: Int): T {
        this.mInputMethodMode = mode
        return self()
    }

    fun setSoftInputMode(mode: Int): T {
        this.mSoftInputMode = mode
        return self()
    }

    /**
     * 是否需要重新获取宽高
     *
     * @param needReMeasureWH
     * @return
     */
    fun setNeedReMeasureWH(needReMeasureWH: Boolean): T {
        this.isNeedReMeasureWH = needReMeasureWH
        return self()
    }

    /**
     * 检查是否调用了 apply() 方法
     *
     * @param isAtAnchorView 是否是 showAt
     */
    private fun checkIsApply(isAtAnchorView: Boolean) {
        if (this.isAtAnchorViewMethod != isAtAnchorView) {
            this.isAtAnchorViewMethod = isAtAnchorView
        }
        if (popupWindow == null) {
            apply()
        }
    }

    /**
     * 使用此方法需要在创建的时候调用setAnchorView()等属性设置{@see setAnchorView()}
     */
    fun showAsDropDown() {
        if (mAnchorView == null) {
            return
        }
        showAsDropDown(mAnchorView!!, mOffsetX, mOffsetY)
    }

    /**
     * PopupWindow自带的显示方法
     *
     * @param anchor
     * @param offsetX
     * @param offsetY
     */
    fun showAsDropDown(anchor: View, offsetX: Int, offsetY: Int) {
        //防止忘记调用 apply() 方法
        checkIsApply(false)

        handleBackgroundDim()
        mAnchorView = anchor
        mOffsetX = offsetX
        mOffsetY = offsetY
        //是否重新获取宽高
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener()
        }
        popupWindow!!.showAsDropDown(anchor, mOffsetX, mOffsetY)
    }

    fun showAsDropDown(anchor: View) {
        //防止忘记调用 apply() 方法
        checkIsApply(false)

        handleBackgroundDim()
        mAnchorView = anchor
        //是否重新获取宽高
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener()
        }
        popupWindow!!.showAsDropDown(anchor)
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun showAsDropDown(anchor: View, offsetX: Int, offsetY: Int, gravity: Int) {
        //防止忘记调用 apply() 方法
        checkIsApply(false)

        handleBackgroundDim()
        mAnchorView = anchor
        mOffsetX = offsetX
        mOffsetY = offsetY
        //是否重新获取宽高
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener()
        }
        PopupWindowCompat.showAsDropDown(popupWindow!!, anchor, mOffsetX, mOffsetY, gravity)
    }

    fun showAtLocation(parent: View, gravity: Int, offsetX: Int, offsetY: Int) {
        //防止忘记调用 apply() 方法
        checkIsApply(false)

        handleBackgroundDim()
        mAnchorView = parent
        mOffsetX = offsetX
        mOffsetY = offsetY
        //是否重新获取宽高
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener()
        }
        popupWindow!!.showAtLocation(parent, gravity, mOffsetX, mOffsetY)
    }

    /**
     * 相对anchor view显示
     *
     *
     * 使用此方法需要在创建的时候调用setAnchorView()等属性设置{@see setAnchorView()}
     *
     *
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。
     */
    fun showAtAnchorView() {
        if (mAnchorView == null) {
            return
        }
        showAtAnchorView(mAnchorView!!, mYGravity, mXGravity)
    }

    /**
     * 相对anchor view显示，适用 宽高不为match_parent
     *
     *
     * 注意：如果使用 VerticalGravity 和 HorizontalGravity 时，请确保使用之后 PopupWindow 没有超出屏幕边界，
     * 如果超出屏幕边界，VerticalGravity 和 HorizontalGravity 可能无效，从而达不到你想要的效果。
     *
     * @param anchor
     * @param vertGravity  垂直方向的对齐方式
     * @param horizGravity 水平方向的对齐方式
     * @param x            水平方向的偏移
     * @param y            垂直方向的偏移
     */
    @JvmOverloads
    fun showAtAnchorView(anchor: View, @YGravity vertGravity: Int, @XGravity horizGravity: Int, x: Int = 0, y: Int = 0) {
        var x = x
        var y = y
        //防止忘记调用 apply() 方法
        checkIsApply(true)

        mAnchorView = anchor
        mOffsetX = x
        mOffsetY = y
        mYGravity = vertGravity
        mXGravity = horizGravity
        //处理背景变暗
        handleBackgroundDim()
        x = calculateX(anchor, horizGravity, mWidth, mOffsetX)
        y = calculateY(anchor, vertGravity, mHeight, mOffsetY)
        //是否重新获取宽高
        if (isNeedReMeasureWH) {
            registerOnGlobalLayoutListener()
        }
        //        Log.i(mTag, "showAtAnchorView: w=" + measuredW + ",y=" + measuredH);
        PopupWindowCompat.showAsDropDown(popupWindow!!, anchor, x, y, Gravity.NO_GRAVITY)

    }

    /**
     * 根据垂直gravity计算y偏移
     *
     * @param anchor
     * @param vertGravity
     * @param measuredH
     * @param y
     * @return
     */
    private fun calculateY(anchor: View, vertGravity: Int, measuredH: Int, y: Int): Int {
        var y = y
        when (vertGravity) {
            YGravity.ABOVE ->
                //anchor view之上
                y -= measuredH + anchor.height
            YGravity.ALIGN_BOTTOM ->
                //anchor view底部对齐
                y -= measuredH
            YGravity.CENTER ->
                //anchor view垂直居中
                y -= anchor.height / 2 + measuredH / 2
            YGravity.ALIGN_TOP ->
                //anchor view顶部对齐
                y -= anchor.height
            YGravity.BELOW -> {
            }
        }//anchor view之下
        // Default position.

        return y
    }

    /**
     * 根据水平gravity计算x偏移
     *
     * @param anchor
     * @param horizGravity
     * @param measuredW
     * @param x
     * @return
     */
    private fun calculateX(anchor: View, horizGravity: Int, measuredW: Int, x: Int): Int {
        var x = x
        when (horizGravity) {
            XGravity.LEFT ->
                //anchor view左侧
                x -= measuredW
            XGravity.ALIGN_RIGHT ->
                //与anchor view右边对齐
                x -= measuredW - anchor.width
            XGravity.CENTER ->
                //anchor view水平居中
                x += anchor.width / 2 - measuredW / 2
            XGravity.ALIGN_LEFT -> {
            }
            XGravity.RIGHT ->
                //anchor view右侧
                x += anchor.width
        }//与anchor view左边对齐
        // Default position.

        return x
    }

    /**
     * 设置监听器
     *
     * @param listener
     */
    fun setOnDismissListener(listener: PopupWindow.OnDismissListener): T {
        this.mOnDismissListener = listener
        return self()
    }

    fun setOnRealWHAlreadyListener(listener: OnRealWHAlreadyListener): T {
        this.mOnRealWHAlreadyListener = listener
        return self()
    }

    /**
     * 处理背景变暗
     * https://blog.nex3z.com/2016/12/04/%E5%BC%B9%E5%87%BApopupwindow%E5%90%8E%E8%AE%A9%E8%83%8C%E6%99%AF%E5%8F%98%E6%9A%97%E7%9A%84%E6%96%B9%E6%B3%95/
     */
    private fun handleBackgroundDim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!isBackgroundDim) {
                return
            }
            if (mDimView != null) {
                applyDim(mDimView)
            } else {
                if (contentView != null && contentView!!.context != null &&
                        contentView!!.context is Activity) {
                    val activity = contentView!!.context as Activity
                    applyDim(activity)
                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun applyDim(activity: Activity) {
        val parent = activity.window.decorView.rootView as ViewGroup
        //activity跟布局
        //        ViewGroup parent = (ViewGroup) parent1.getChildAt(0);
        val dimDrawable = ColorDrawable(mDimColor)
        dimDrawable.setBounds(0, 0, parent.width, parent.height)
        dimDrawable.alpha = (255 * mDimValue).toInt()
        val overlay = parent.overlay
        overlay.add(dimDrawable)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun applyDim(dimView: ViewGroup) {
        val dimDrawable = ColorDrawable(mDimColor)
        dimDrawable.setBounds(0, 0, dimView.width, dimView.height)
        dimDrawable.alpha = (255 * mDimValue).toInt()
        val overlay = dimView.overlay
        overlay.add(dimDrawable)
    }

    /**
     * 清除背景变暗
     */
    private fun clearBackgroundDim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (isBackgroundDim) {
                if (mDimView != null) {
                    clearDim(mDimView)
                } else {
                    if (contentView != null) {
                        val activity = contentView!!.context as Activity
                        if (activity != null) {
                            clearDim(activity)
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun clearDim(activity: Activity) {
        val parent = activity.window.decorView.rootView as ViewGroup
        //activity跟布局
        //        ViewGroup parent = (ViewGroup) parent1.getChildAt(0);
        val overlay = parent.overlay
        overlay.clear()
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun clearDim(dimView: ViewGroup) {
        val overlay = dimView.overlay
        overlay.clear()
    }

    /**
     * 获取PopupWindow 宽
     *
     * @return
     */
    fun getWidth(): Int {
        return mWidth
    }

    /**
     * 获取PopupWindow 高
     *
     * @return
     */
    fun getHeight(): Int {
        return mHeight
    }

    /**
     * 获取纵向Gravity
     *
     * @return
     */
    fun getXGravity(): Int {
        return mXGravity
    }

    /**
     * 获取横向Gravity
     *
     * @return
     */
    fun getYGravity(): Int {
        return mYGravity
    }

    /**
     * 获取x轴方向的偏移
     *
     * @return
     */
    fun getOffsetX(): Int {
        return mOffsetX
    }

    /**
     * 获取y轴方向的偏移
     *
     * @return
     */
    fun getOffsetY(): Int {
        return mOffsetY
    }

    /**
     * 获取view
     *
     * @param viewId
     * @param <T>
     * @return
    </T> */
    fun <T : View> findViewById(@IdRes viewId: Int): T? {
        var view: View? = null
        if (contentView != null) {
            view = contentView!!.findViewById(viewId)
        }
        return view as T?
    }

    /**
     * 消失
     */
    fun dismiss() {
        if (popupWindow != null) {
            popupWindow!!.dismiss()
        }
    }

    override fun onDismiss() {
        handleDismiss()
    }

    /**
     * PopupWindow消失后处理一些逻辑
     */
    private fun handleDismiss() {
        if (mOnDismissListener != null) {
            mOnDismissListener!!.onDismiss()
        }

        //清除背景变暗
        clearBackgroundDim()
        if (popupWindow != null && popupWindow!!.isShowing) {
            popupWindow!!.dismiss()
        }
        onPopupWindowDismiss()
    }

    /**
     * PopupWindow是否显示在window中
     * 用于获取准确的PopupWindow宽高，可以重新设置偏移量
     */
    interface OnRealWHAlreadyListener {

        /**
         * 在 show方法之后 updateLocation之前执行
         *
         * @param popWidth  PopupWindow准确的宽
         * @param popHeight PopupWindow准确的高
         * @param anchorW   锚点View宽
         * @param anchorH   锚点View高
         */
        fun onRealWHAlready(basePopup: BasePopup<*>, popWidth: Int, popHeight: Int, anchorW: Int, anchorH: Int)
    }

    companion object {

        private val mTag = "BasePopup"

        private val DEFAULT_DIM = 0.5f
        private val DEFAULT_WH = ViewGroup.LayoutParams.WRAP_CONTENT
    }

}
