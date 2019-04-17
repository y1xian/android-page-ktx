package com.yyxnb.yyxarch.base

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.transition.TransitionListenerAdapter
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.nav.FragmentHelper
import com.yyxnb.yyxarch.nav.LifecycleDelegate
import com.yyxnb.yyxarch.nav.PresentAnimation
import com.yyxnb.yyxarch.utils.ActivityStack
import java.util.*


/**
 * Description: BaseActivity
 *
 * @author : yyx
 * @date : 2018/6/10
 */
abstract class BaseActivity : AppCompatActivity() {


    protected val TAG = javaClass.canonicalName

    protected lateinit var mContext: Context

    protected var fragmentContainer: FrameLayout? = null

    protected var delayToTransition = true

    private val lifecycleDelegate = LifecycleDelegate(this)

    private var hasFormerRoot: Boolean = false

    private var statusBarTranslucent: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        if (delayToTransition) {
            afterEnterTransition()
        }

        initViewObservable()

        ActivityStack.addActivity(this)
    }

    private var enterTransitionListener =
            object : TransitionListenerAdapter(), android.transition.Transition.TransitionListener {
                override fun onTransitionResume(transition: android.transition.Transition?) {}

                override fun onTransitionPause(transition: android.transition.Transition?) {}

                override fun onTransitionCancel(transition: android.transition.Transition?) {}

                override fun onTransitionStart(transition: android.transition.Transition?) {}

                override fun onTransitionEnd(transition: android.transition.Transition?) {
                    initViewData()
                }
            }

    private fun afterEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition.addListener(enterTransitionListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition.removeListener(enterTransitionListener)
        }
        ActivityStack.finishActivity(this)
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

    /**
     * 初始化界面观察者的监听
     * 接收数据结果
     */
    open fun initViewObservable() {}

    fun isStatusBarTranslucent(): Boolean {
        return statusBarTranslucent
    }

    //开启沉浸式
    fun setStatusBarTranslucent(translucent: Boolean) {
        if (statusBarTranslucent != translucent) {
            statusBarTranslucent = translucent
            AppUtils.setStatusBarTranslucent(window, translucent)
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
    protected fun scheduleTaskAtStarted(runnable: Runnable, deferred: Boolean = false) {
        lifecycleDelegate.scheduleTaskAtStarted(runnable, deferred)
    }

}
