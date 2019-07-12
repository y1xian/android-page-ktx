package com.yyxnb.yyxarch.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import com.github.anzewei.parallaxbacklayout.ParallaxBack
import com.yyxnb.yyxarch.ext.closeSoftKeyboard
import com.yyxnb.yyxarch.nav.FragmentHelper
import com.yyxnb.yyxarch.nav.LifecycleDelegate
import com.yyxnb.yyxarch.nav.PresentAnimation
import com.yyxnb.yyxarch.utils.ActivityStack
import com.yyxnb.yyxarch.utils.StatusBarUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*
import kotlin.coroutines.CoroutineContext


/**
 * Description: BaseActivity
 *
 * @author : yyx
 * @date : 2018/6/10
 */
@ParallaxBack
abstract class BaseActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job // 定义job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job // Activity的协程

    protected val TAG = javaClass.canonicalName

    protected lateinit var mContext: Context

    protected var fragmentContainer: FrameLayout? = null

    private val lifecycleDelegate = LifecycleDelegate(this)

    private var hasFormerRoot: Boolean = false

    private var statusBarTranslucent: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        job = Job()

        setStatusBarTranslucent(true)

        mContext = this

        lifecycle.addObserver(Java8Observer)

        if (initLayoutResID() == 0) {
            fragmentContainer = FrameLayout(this)
            fragmentContainer!!.id = android.R.id.content
            setContentView(fragmentContainer)
        } else {
            setContentView(initLayoutResID())
        }

        initView(savedInstanceState)

        ActivityStack.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityStack.finishActivity(this)
        job.cancel() // 关闭页面后，结束所有协程任务
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    abstract fun initLayoutResID(): Int

    /**
     * 初始化
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化复杂数据 懒加载
     */
    open fun initViewData() {}

    fun isStatusBarTranslucent(): Boolean {
        return statusBarTranslucent
    }

    //开启沉浸式
    fun setStatusBarTranslucent(translucent: Boolean) {
        if (statusBarTranslucent != translucent) {
            statusBarTranslucent = translucent
            StatusBarUtils.setStatusBarTranslucent(window, translucent)
        }
    }

    fun getFragmentsAtAddedList(): List<BaseFragment> {
        val children = ArrayList<BaseFragment>()
        val fragments = supportFragmentManager.fragments
        var i = 0
        val size = fragments.size
        while (i < size) {
            val fragment = fragments[i]
            if (fragment is BaseFragment) {
                children.add(fragment as BaseFragment)
            }
            i++
        }
        return children
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        val count = fragmentManager.backStackEntryCount
        if (count > 0) {
            val entry = fragmentManager.getBackStackEntryAt(count - 1)
            val fragment = fragmentManager.findFragmentByTag(entry.name) as BaseFragment?
            if (fragment != null && fragment.isAdded && !fragment.dispatchBackPressed()) {
                if (count == 1) {
                    if (!handleBackPressed()) {
                        ActivityCompat.finishAfterTransition(this)
                    }
                } else {
                    dismissFragment(fragment)
                }
            }
        } else {
            super.onBackPressed()
        }
    }

    protected fun handleBackPressed(): Boolean {
        return false
    }

    fun presentFragment(fragment: BaseFragment) {
        scheduleTaskAtStarted(Runnable { presentFragmentInternal(fragment) }, true)
    }

    private fun presentFragmentInternal(fragment: BaseFragment) {
        FragmentHelper.addFragmentToBackStack(supportFragmentManager, android.R.id.content, fragment, PresentAnimation.Push)
    }

    fun dismissFragment(fragment: BaseFragment) {
        scheduleTaskAtStarted(Runnable { dismissFragmentInternal(fragment) }, true)
    }

    private fun dismissFragmentInternal(fragment: BaseFragment) {
        if (!fragment.isAdded) {
            return
        }
        val fragmentManager = supportFragmentManager
        FragmentHelper.executePendingTransactionsSafe(fragmentManager)

        val topFragment = fragmentManager.findFragmentById(android.R.id.content) as BaseFragment?
                ?: return
        topFragment.setAnimation(PresentAnimation.Push)
        val presented = getPresentedFragment(fragment)
        if (presented != null) {
            fragment.setAnimation(PresentAnimation.Push)
            topFragment.userVisibleHint = false
            topFragment.onHiddenChanged(true)
            supportFragmentManager.popBackStack(presented.getSceneId(), FragmentManager.POP_BACK_STACK_INCLUSIVE)
            FragmentHelper.executePendingTransactionsSafe(supportFragmentManager)
            fragment.onFragmentResult(topFragment.getRequestCode(), topFragment.getResultCode(), topFragment.getResultData())
        } else {
            val presenting = getPresentingFragment(fragment)
            presenting?.setAnimation(PresentAnimation.Push)
            fragment.userVisibleHint = false
            if (presenting == null) {
                ActivityCompat.finishAfterTransition(this)
            } else {
                fragmentManager.popBackStack(fragment.getSceneId(), FragmentManager.POP_BACK_STACK_INCLUSIVE)
                FragmentHelper.executePendingTransactionsSafe(fragmentManager)
                presenting.onFragmentResult(fragment.getRequestCode(), fragment.getResultCode(), fragment.getResultData())
            }
        }
    }

    fun getPresentedFragment(fragment: BaseFragment): BaseFragment? {
        return FragmentHelper.getLatterFragment(supportFragmentManager, fragment)
    }

    fun getPresentingFragment(fragment: BaseFragment): BaseFragment? {
        return FragmentHelper.getAheadFragment(supportFragmentManager, fragment)
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
                former.setAnimation(PresentAnimation.Push)
                fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                hasFormerRoot = true
            }
        }

        val transaction = fragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragment.setAnimation(PresentAnimation.None)
        transaction.add(containerId, fragment, fragment.getSceneId())
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
                former.setAnimation(PresentAnimation.Push)
                fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    fun activityHasFormerRoot(): Boolean {
        return hasFormerRoot
    }

    @JvmOverloads
    protected fun scheduleTaskAtStarted(runnable: Runnable, deferred: Boolean = false, interval: Long = 100L) {
        lifecycleDelegate.scheduleTaskAtStarted(runnable, deferred, interval)
    }


    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATED_IDENTITY_EQUALS")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //把操作放在用户点击的时候
        if (event.action === MotionEvent.ACTION_DOWN) {
            val v = currentFocus      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isShouldHideKeyboard(v, event)) { //判断用户点击的是否是输入框以外的区域
                //收起键盘
                v.closeSoftKeyboard()
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
