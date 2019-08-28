package com.yyxnb.yyxarch.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import butterknife.ButterKnife
import com.github.anzewei.parallaxbacklayout.ParallaxBack
import com.yyxnb.yyxarch.R
import com.yyxnb.yyxarch.common.AppConfig
import com.yyxnb.yyxarch.ext.hideKeyBoard
import com.yyxnb.yyxarch.interfaces.*
import com.yyxnb.yyxarch.nav.LifecycleDelegate
import com.yyxnb.yyxarch.utils.ActivityManager
import com.yyxnb.yyxarch.utils.StatusBarUtils
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*

/**
 * Description: BaseActivity
 *
 * @author : yyx
 * @date : 2018/6/10
 */
@ParallaxBack
abstract class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    protected val TAG = javaClass.simpleName

    protected lateinit var mContext: Context

    protected var fragmentContainer: FrameLayout? = null

    private val lifecycleDelegate by lazy { LifecycleDelegate(this) }

    private var hasFormerRoot = false

    private var layoutResId = 0
    private var statusBarTranslucent = AppConfig.statusBarTranslucent
    private var fitsSystemWindows = true
    private var statusBarHidden = AppConfig.statusBarHidden
    private var statusBarColor = AppConfig.statusBarColor
    private var statusBarDarkTheme = AppConfig.statusBarStyle
    private var navigationBarDarkTheme = AppConfig.navigationBarStyle
    private var navigationBarColor = AppConfig.navigationBarColor
    private var swipeBack = AppConfig.swipeBackEnabled

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        lifecycle.addObserver(Java8Observer)

        initAttributes()

        if (layoutResId != 0 && initLayoutResId() == 0) {
            setContentView(layoutResId)
        } else if (layoutResId == 0 && initLayoutResId() != 0) {
            setContentView(initLayoutResId())
        } else {
            fragmentContainer = FrameLayout(this)
            fragmentContainer!!.id = android.R.id.content
            setContentView(fragmentContainer)
        }
        //绑定初始化ButterKnife
        ButterKnife.bind(this)
        initView(savedInstanceState)
        ActivityManager.pushActivity(this)
    }

    //加载注解设置
    private fun initAttributes() {
        javaClass.getAnnotation(LayoutResId::class.java)?.let { layoutResId = it.value }
        javaClass.getAnnotation(StatusBarTranslucent::class.java)?.let { statusBarTranslucent = it.value }
        javaClass.getAnnotation(StatusBarHidden::class.java)?.let { statusBarHidden = it.value }
        javaClass.getAnnotation(StatusBarColor::class.java)?.let { statusBarColor = it.color }
        javaClass.getAnnotation(StatusBarDarkTheme::class.java)?.let { statusBarDarkTheme = it.value }
        javaClass.getAnnotation(NavigationBarDarkTheme::class.java)?.let { navigationBarDarkTheme = it.value }
        javaClass.getAnnotation(NavigationBarColor::class.java)?.let { navigationBarColor = it.color }
        javaClass.getAnnotation(SwipeBack::class.java)?.let { swipeBack = it.value }
        javaClass.getAnnotation(FitsSystemWindows::class.java)?.let { fitsSystemWindows = it.value }

        //判断是否Color类下
        if (statusBarColor > 0) {
            statusBarColor = resources.getColor(statusBarColor)
        }
        if (navigationBarColor > 0) {
            navigationBarColor = resources.getColor(navigationBarColor)
        }

        setStatusBarTranslucent(statusBarTranslucent, fitsSystemWindows)
        setStatusBarColor(statusBarColor)
        setStatusBarStyle(statusBarDarkTheme)
        setNavigationBarColor(navigationBarColor)
        setNavigationBarStyle(navigationBarDarkTheme)
        setStatusBarHidden(statusBarHidden)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.deleteActivity(this)
        cancel() // 关闭页面后，结束所有协程任务
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    @LayoutRes
    open fun initLayoutResId(): Int = 0

    /**
     * 初始化
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化复杂数据 懒加载
     */
    open fun initViewData() {}

    //开启沉浸式
    fun setStatusBarTranslucent(translucent: Boolean, fitsSystemWindows: Boolean) {
        StatusBarUtils.setStatusBarTranslucent(window, translucent, fitsSystemWindows)
    }

    //状态栏颜色
    fun setStatusBarColor(color: Int) {
        StatusBarUtils.setStatusBarColor(window, color, true)
    }

    //状态栏字体
    fun setStatusBarStyle(barStyle: BarStyle) {
        StatusBarUtils.setStatusBarStyle(window, barStyle === BarStyle.DarkContent)
    }

    //隐藏状态栏
    fun setStatusBarHidden(hidden: Boolean) {
        StatusBarUtils.setStatusBarHidden(window, hidden)
    }

    //底部栏颜色
    fun setNavigationBarColor(color: Int) {
        StatusBarUtils.setNavigationBarColor(window, color)
    }

    //底部栏字体
    fun setNavigationBarStyle(barStyle: BarStyle) {
        StatusBarUtils.setNavigationBarStyle(window, barStyle === BarStyle.DarkContent)
    }

    fun getFragmentsAtAddedList(): List<BaseFragment> {
        val children = ArrayList<BaseFragment>()
        val fragments = supportFragmentManager.fragments
        var i = 0
        val size = fragments.size
        while (i < size) {
            val fragment = fragments[i]
            if (fragment is BaseFragment) {
                children.add(fragment)
            }
            i++
        }
        return children
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        val count = fragmentManager.backStackEntryCount
        if (count > 0) {
//            val entry = fragmentManager.getBackStackEntryAt(count - 1)
//            val fragment = fragmentManager.findFragmentByTag(entry.name) as BaseFragment?
//            if (fragment != null && fragment.isAdded && !fragment.dispatchBackPressed()) {
//                if (count == 1) {
//                    if (!handleBackPressed()) {
            ActivityCompat.finishAfterTransition(this)
//                    }
//                } else {
//                    dismissFragment(fragment)
//                }
//            }
        } else {
            super.onBackPressed()
        }
    }

    @JvmOverloads
    fun startActivityRootFragment(rootFragment: BaseFragment, containerId: Int = android.R.id.content) {
        scheduleTaskAtStarted(Runnable { setRootFragmentInternal(rootFragment, containerId) })
    }

    private fun setRootFragmentInternal(fragment: BaseFragment, containerId: Int) {
        val fragmentManager = supportFragmentManager
        val count = fragmentManager.backStackEntryCount
        if (count > 0) {
            val tag = fragmentManager.getBackStackEntryAt(0).name
            val former = fragmentManager.findFragmentByTag(tag) as BaseFragment?
            if (former != null && former.isAdded) {
//                former.setAnimation(PresentAnimation.Push)
                fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                hasFormerRoot = true
            }
        }

        val transaction = fragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//        fragment.setAnimation(PresentAnimation.None)
        transaction.replace(containerId, fragment, fragment.getSceneId())
        transaction.addToBackStack(fragment.getSceneId())
        transaction.commit()
    }

    fun clearFragments() {
        val fragmentManager = supportFragmentManager
        val count = fragmentManager.backStackEntryCount
        if (count > 0) {
            window.setBackgroundDrawable(ColorDrawable(Color.WHITE))

            val tag = fragmentManager.getBackStackEntryAt(0).name
            val former = fragmentManager.findFragmentByTag(tag) as BaseFragment?
            if (former != null && former.isAdded) {
                fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    @JvmOverloads
    protected fun scheduleTaskAtStarted(runnable: Runnable, interval: Long = 100L) {
        lifecycleDelegate.scheduleTaskAtStarted(runnable, interval)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATED_IDENTITY_EQUALS")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //把操作放在用户点击的时候
        if (event.action === MotionEvent.ACTION_DOWN) {
            val v = currentFocus      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isShouldHideKeyboard(v, event)) { //判断用户点击的是否是输入框以外的区域
                //收起键盘
                v.hideKeyBoard()
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {  //判断得到的焦点控件是否包含EditText
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            //得到输入框在屏幕中上下左右的位置
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        // 如果焦点不是EditText则忽略
        return false
    }


}
