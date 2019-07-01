package com.yyxnb.yyxarch.base

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.tencent.mmkv.MMKV
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.ContainerActivity
import com.yyxnb.yyxarch.R
import com.yyxnb.yyxarch.common.AppConfig
import com.yyxnb.yyxarch.nav.FragmentHelper
import com.yyxnb.yyxarch.nav.LifecycleDelegate
import com.yyxnb.yyxarch.nav.NavigationFragment
import com.yyxnb.yyxarch.nav.PresentAnimation
import com.yyxnb.yyxarch.utils.BarStyle
import com.yyxnb.yyxarch.utils.MainThread
import com.yyxnb.yyxarch.utils.StatusBarUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*
import kotlin.coroutines.CoroutineContext


/**
 * Description: BaseFragment
 *
 * @author : yyx
 * @date ：2016/10
 */
abstract class BaseFragment : Fragment(), CoroutineScope {

    companion object {

        const val ARGS_REQUEST_CODE = "nav_request_code"

        const val REQUEST_CODE_INVALID = -1
    }

    private lateinit var job: Job // 定义job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job // Fragment的协程


    protected lateinit var mActivity: AppCompatActivity

    protected val TAG = javaClass.canonicalName

    protected var rootView: View? = null

    //是否可见状态
    private var mIsVisible: Boolean = false
    //标志位，View已经初始化完成
    private var mIsPrepared: Boolean = false
    //是否第一次加载
    private var mIsFirstVisible = true
    //是否ViewPage
    private var mIsInViewPager: Boolean = false

    private val lifecycleDelegate = LifecycleDelegate(this)

    private val kv = MMKV.defaultMMKV()!!

    private var sceneId: String? = null

    fun getSceneId(): String {
        if (this.sceneId == null) {
            this.sceneId = UUID.randomUUID().toString()
        }
        return sceneId as String
    }

    init {
        lifecycle.addObserver(Java8Observer)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        mIsFirstVisible = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        val bundle = initArguments()
        if (bundle.size() > 0) {
            initVariables(bundle)
        }
        setResult(0, null)
        requireFragmentManager().registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, true)
    }

    fun initArguments(): Bundle {
        var args = arguments
        if (args == null) {
            args = Bundle()
            arguments = args
        }
        return args
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == rootView) {
            rootView = inflater.inflate(initLayoutResID(), container, false)
        }
        rootView!!.setOnTouchListener { _, event ->
            mActivity.onTouchEvent(event)
            return@setOnTouchListener false
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mIsPrepared = true
        retainInstance = true //当设备旋转时，fragment会随托管activity一起销毁并重建。
        initView(savedInstanceState)
        if (isSingleFragment() && !mIsVisible) {
            if (userVisibleHint || isVisible || !isHidden) {
                onVisibleChanged(true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        job.cancel() // 关闭页面后，结束所有协程任务
    }

    private var fragmentLifecycleCallbacks: FragmentManager.FragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            if (fm === f.fragmentManager && targetFragment === f) {
                setTargetFragment(f.targetFragment, f.targetRequestCode)
            }
        }
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     */
    @CallSuper
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (null == rootView) {
            return
        }
        mIsInViewPager = true
        if (!mIsPrepared) {
            userVisibleHint = isVisibleToUser
        } else {
            onVisibleChanged(isVisibleToUser)
        }

        val fragments = getChildFragmentsAtAddedList()
        for (fragment in fragments) {
            fragment.userVisibleHint = isVisibleToUser
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     * @param hidden  如果该Fragment对象已经被隐藏，那么它返回true。
     */
    @CallSuper
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (null == rootView) {
            return
        }
        mIsInViewPager = false
        if (!mIsPrepared) {
            onHiddenChanged(hidden)
        } else {
            onVisibleChanged(!hidden)
        }

        val fragments = getChildFragmentsAtAddedList()
        for (fragment in fragments) {
            fragment.onHiddenChanged(hidden)
        }
    }

    /**
     * 被ViewPager移出的Fragment 下次显示时会从getArguments()中重新获取数据
     * 所以若需要刷新被移除Fragment内的数据需要重新put数据 eg:
     */
    @CallSuper
    open fun initVariables(bundle: Bundle) {
    }

    /**
     * 当界面可见时的操作
     */
    @CallSuper
    open fun onVisible() {
        if (childFragmentForAppearance() == null) {
            setNeedsStatusBarAppearanceUpdate()
            setNeedsNavigationBarAppearanceUpdate()
        }
        AppUtils.debugLog("onVisible ${javaClass.simpleName}")
        initLazyLoadView()
    }

    /**
     * 当界面不可见时的操作
     */
    @CallSuper
    open fun onInVisible() {
        AppUtils.debugLog("onInVisible ${javaClass.simpleName}")
    }

    /**
     * 用户可见变化回调
     */
    private fun onVisibleChanged(isVisibleToUser: Boolean) {
        MainThread.post(Runnable {
            mIsVisible = true
            if (isVisibleToUser) {
                //避免因视图未加载子类刷新UI抛出异常
                if (!mIsPrepared) {
                    onVisibleChanged(isVisibleToUser)
                } else {
                    onVisible()
                }
            } else {
                onInVisible()
            }
        })
    }

    /**
     * 数据懒加载
     */
    private fun initLazyLoadView() {
        if (mIsFirstVisible && mIsPrepared && mIsVisible) {
            mIsFirstVisible = false
            initViewData()
            AppUtils.debugLog("initLazyLoadView ${getDebugTag()}")
        }
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    @LayoutRes
    abstract fun initLayoutResID(): Int

    /**
     * 初始化控件
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化复杂数据 懒加载
     */
    @CallSuper
    open fun initViewData() {
    }

    fun <T> fv(@IdRes resId: Int): T {
        return rootView!!.findViewById<View>(resId) as T
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        if (isAdded && isVisibleToUser(this) && this != getNavigationFragment() && isVisible) {
            onVisibleChanged(userVisibleHint && !isHidden)
        }
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        if (isAdded && isVisibleToUser(this)) {
            onVisibleChanged(false)
        }
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        mIsVisible = false
        mIsPrepared = false
        rootView = null
    }

    /**
     * @param fragment
     */
    private fun isVisibleToUser(fragment: BaseFragment?): Boolean {
        if (fragment == null) {
            return false
        }
        return if (mIsInViewPager) fragment.userVisibleHint else fragment.isVisible
    }

    /**
     * 返回.
     */
    fun finish() {
        mActivity.onBackPressed()
    }

    fun getIsVisible(): Boolean = mIsVisible

    /**
     * 检查Fragment或FragmentActivity承载的Fragment是否只有一个
     */
    fun isSingleFragment(): Boolean {
        var size = 0
        val manager = fragmentManager
        if (manager != null && manager.fragments.isNotEmpty()) {
            size = manager.fragments.size
        }
        return size <= 1
    }


    @CallSuper
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {

        val animation = getAnimation()

        val parent = getParentBaseFragment()
        if (parent != null && parent.isRemoving()) {
            return AnimationUtils.loadAnimation(context, R.anim.nav_delay)
        }

        if (transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {
            return if (enter) {
                AnimationUtils.loadAnimation(context, animation.enter)
            } else {
                AnimationUtils.loadAnimation(context, animation.exit)
            }
        } else if (transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE) {
            return if (enter) {
                AnimationUtils.loadAnimation(context, animation.popEnter)
            } else {
                AnimationUtils.loadAnimation(context, animation.popExit)
            }
        }

        return AnimationUtils.loadAnimation(context, animation.enter)
    }

    // ------ lifecycle arch -------

    @JvmOverloads
    fun scheduleTaskAtStarted(runnable: Runnable, deferred: Boolean = true, interval: Long = 100L) {
        lifecycleDelegate.scheduleTaskAtStarted(runnable, deferred, interval)
    }

    // ------- navigation ------

    private var definesPresentationContext: Boolean = false

    fun definesPresentationContext(): Boolean {
        return definesPresentationContext
    }

    fun setDefinesPresentationContext(defines: Boolean) {
        definesPresentationContext = defines
    }

    fun dispatchBackPressed(): Boolean {
        val fragmentManager = childFragmentManager
        val count = fragmentManager.backStackEntryCount
        val fragment = fragmentManager.primaryNavigationFragment

        if (fragment is BaseFragment && definesPresentationContext() && count > 0) {
            val backStackEntry = fragmentManager.getBackStackEntryAt(count - 1)
            val child = fragmentManager.findFragmentByTag(backStackEntry.name) as BaseFragment?
            if (child != null) {
                val processed = child.dispatchBackPressed() || onBackPressed()
                if (!processed) {
                    child.dismissFragment()
                }
                return true
            }
        }

        return when {
            fragment is BaseFragment -> {
                val child = fragment as BaseFragment?
                child!!.dispatchBackPressed() || onBackPressed()
            }
            count > 0 -> {
                val backStackEntry = fragmentManager.getBackStackEntryAt(count - 1)
                val child = fragmentManager.findFragmentByTag(backStackEntry.name) as BaseFragment?
                child != null && child.dispatchBackPressed() || onBackPressed()
            }
            else -> onBackPressed()
        }
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    fun presentFragment(targetFragment: BaseFragment) {
        scheduleTaskAtStarted(Runnable {
            val parent = getParentBaseFragment()
            if (parent != null) {
                if (definesPresentationContext()) {
                    presentFragmentInternal(this, targetFragment)
                } else {
                    parent.presentFragment(targetFragment)
                }
                return@Runnable
            }

            (mActivity as? BaseActivity)?.presentFragment(targetFragment)
        }, true)
    }

    private fun presentFragmentInternal(target: BaseFragment, fragment: BaseFragment) {
        fragment.setTargetFragment(target, requestCode)
        fragment.setDefinesPresentationContext(true)
        FragmentHelper.addFragmentToBackStack(target.requireFragmentManager(), target.id, fragment, PresentAnimation.Push)
    }

    fun dismissFragment() {
        scheduleTaskAtStarted(Runnable {
            val parent = getParentBaseFragment()
            if (parent != null) {
                if (definesPresentationContext()) {
                    val presented = getPresentedFragment()
                    if (presented != null) {
                        dismissFragmentInternal(null)
                        return@Runnable
                    }
                    val target = targetFragment as BaseFragment?
                    if (target != null) {
                        dismissFragmentInternal(target)
                    }
                } else {
                    parent.dismissFragment()
                }
                return@Runnable
            }

            (mActivity as? BaseActivity)?.dismissFragment(this)
        }, true)

    }

    private fun dismissFragmentInternal(target: BaseFragment?) {
        if (target == null) {
            val presented = getPresentedFragment()
            val count = requireFragmentManager().backStackEntryCount
            val backStackEntry = requireFragmentManager().getBackStackEntryAt(count - 1)
            val top = requireFragmentManager().findFragmentByTag(backStackEntry.name) as BaseFragment?
            if (top == null || presented == null) {
                return
            }
            setAnimation(PresentAnimation.Push)
            top.setAnimation(PresentAnimation.Push)
            top.onHiddenChanged(true)
            requireFragmentManager().popBackStack(presented.sceneId, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            FragmentHelper.executePendingTransactionsSafe(requireFragmentManager())
            onFragmentResult(top.getRequestCode(), top.getResultCode(), top.getResultData())
        } else {
            setAnimation(PresentAnimation.Push)
            target.setAnimation(PresentAnimation.Push)
            target.onHiddenChanged(true)
            requireFragmentManager().popBackStack(getSceneId(), FragmentManager.POP_BACK_STACK_INCLUSIVE)
            FragmentHelper.executePendingTransactionsSafe(requireFragmentManager())
            target.onFragmentResult(getRequestCode(), getResultCode(), getResultData())
        }
    }

    fun getPresentedFragment(): BaseFragment? {
        val parent = getParentBaseFragment()
        if (parent != null) {
            if (definesPresentationContext()) {
                return if (FragmentHelper.findIndexAtBackStack(requireFragmentManager(), this) == -1) {
                    if (parent.getChildFragmentCountAtBackStack() == 0) {
                        null
                    } else {
                        val backStackEntry = requireFragmentManager().getBackStackEntryAt(0)
                        val presented = requireFragmentManager().findFragmentByTag(backStackEntry.name) as BaseFragment?
                        if (presented != null && presented.isAdded) {
                            presented
                        } else null
                    }
                } else {
                    FragmentHelper.getLatterFragment(requireFragmentManager(), this)
                }
            } else {
                return parent.getPresentedFragment()
            }
        }

        return (mActivity as? BaseActivity)?.getPresentedFragment(this)

    }

    fun getPresentingFragment(): BaseFragment? {
        val parent = getParentBaseFragment()
        if (parent != null) {
            if (definesPresentationContext()) {
                val target = targetFragment as BaseFragment?
                return if (target != null && target.isAdded) {
                    target
                } else null
            } else {
                return parent.getPresentingFragment()
            }
        }

        return (mActivity as? BaseActivity)?.getPresentingFragment(this)

    }

    fun getDebugTag(): String? {
        if (activity == null) {
            return null
        }
        val parent = getParentBaseFragment()
        return if (parent == null) {
            "#" + getIndexAtAddedList() + "-" + javaClass.simpleName
        } else {
            parent.getDebugTag() + "#" + getIndexAtAddedList() + "-" + javaClass.simpleName
        }
    }

    // 可以重写这个方法来指定由那个子 Fragment 来决定系统 UI（状态栏）的样式，否则由容器本身决定
    open fun childFragmentForAppearance(): BaseFragment? {
        return null
    }

    fun getChildFragmentCountAtBackStack(): Int {
        val fragmentManager = childFragmentManager
        return fragmentManager.backStackEntryCount
    }

    fun getIndexAtBackStack(): Int {
        return FragmentHelper.findIndexAtBackStack(requireFragmentManager(), this)
    }

    fun getIndexAtAddedList(): Int {
        val fragments = requireFragmentManager().fragments
        return fragments.indexOf(this)
    }

    fun getChildFragmentsAtAddedList(): List<BaseFragment> {
        val children = ArrayList<BaseFragment>()
        if (isAdded) {
            val fragments = childFragmentManager.fragments
            var i = 0
            val size = fragments.size
            while (i < size) {
                val fragment = fragments[i]
                if (fragment is BaseFragment && fragment.isAdded()) {
                    children.add(fragment)
                }
                i++
            }
        }
        return children
    }

    fun getParentBaseFragment(): BaseFragment? {
        val fragment = parentFragment
        return if (fragment is BaseFragment) {
            fragment
        } else null
    }

    private var animation: PresentAnimation? = null

    fun setAnimation(animation: PresentAnimation) {
        this.animation = animation
    }

    fun getAnimation(): PresentAnimation {
        if (this.animation == null) {
            this.animation = PresentAnimation.None
        }
        return animation!!
    }

    open fun isParentFragment(): Boolean {
        return false
    }

    fun getWindow(): Window? {
        return if (activity != null) {
            activity!!.window
        } else null

    }

    // ------ NavigationFragment -----
    open fun getNavigationFragment(): NavigationFragment? {
        if (this is NavigationFragment) {
            return this
        }
        val parent = getParentBaseFragment()
        return parent?.getNavigationFragment()
    }

    fun isNavigationRoot(): Boolean {
        val navigationFragment = getNavigationFragment()
        if (navigationFragment != null) {
            val awesomeFragment = navigationFragment.getRootFragment()
            return awesomeFragment === this
        }
        return false
    }

    open fun isSwipeBackEnabled(): Boolean {
        return AppConfig.swipeBackEnabled
    }

    /**
     * 使用给定的类名创建Fragment的新实例。 这与调用其空构造函数相同。
     *
     * @param targetFragment 目标fragment.
     * @param bundle        argument.
     * @param T           [BaseFragment].
     * @return new instance.
     */
    @JvmOverloads
    fun <T : BaseFragment> fragment(targetFragment: T, bundle: Bundle? = null): T {
        return context?.let { instantiate(it, targetFragment.javaClass.canonicalName!!, bundle) } as T
    }

    fun rootFragment(targetFragment: BaseFragment): BaseFragment {
        val navigationFragment = NavigationFragment()
        navigationFragment.setRootFragment(targetFragment)
        return navigationFragment
    }

    /**
     * 跳转 activity.
     *
     * @param clazz 目标activity class.
     * @param <T>   [Activity].
    </T> */
    protected fun <T : Activity> startActivity(clazz: Class<T>) {
        startActivity(Intent(mActivity, clazz))
    }

    private var requestCode: Int = 0
    private var resultCode: Int = 0
    private var result: Bundle? = null

    /**
     * 设置结果码和回传结果.
     *
     * @param resultCode 结果码.
     * @param result     跳转所携带的信息.
     */
    @JvmOverloads
    fun setResult(resultCode: Int, result: Bundle? = null) {
        this.resultCode = resultCode
        this.result = result
        val parent = getParentBaseFragment()
        if (parent != null && !definesPresentationContext() /*&& !showsDialog*/) {
            parent.setResult(resultCode, result)
        }
    }

    /**
     * 设置请求码.
     *
     * @param requestCode 请求码.
     */
    fun setRequest(requestCode: Int) {
        this.requestCode = requestCode
        kv.encode(ARGS_REQUEST_CODE, requestCode)
    }

    fun getRequestCode(): Int {
        if (requestCode == 0) {
            requestCode = kv.getInt(ARGS_REQUEST_CODE, REQUEST_CODE_INVALID)
        }
        return requestCode
    }

    fun getResultCode(): Int {
        return resultCode
    }

    fun getResultData(): Bundle? {
        return result
    }

    /**
     * 处理返回结果.
     *
     * @param resultCode 结果码.
     * @param result     跳转所携带的信息.
     */
    @CallSuper
    open fun onFragmentResult(requestCode: Int, resultCode: Int, result: Bundle? = null) {

        if (this is NavigationFragment) {
            val child = this.getTopFragment()
            child?.onFragmentResult(requestCode, resultCode, result)

        } else {
            val fragments = getChildFragmentsAtAddedList()
            for (child in fragments) {
                child.onFragmentResult(requestCode, resultCode, result)
            }
        }
    }

    /**
     * 跳转 fragment.
     *
     * @param targetFragment 目标fragment.
     * @param requestCode    请求码.
     * @param T          [BaseFragment].
     */
    @JvmOverloads
    fun <T : BaseFragment> startFragment(targetFragment: T, requestCode: Int = REQUEST_CODE_INVALID) {

        kv.encode(ARGS_REQUEST_CODE, requestCode)

        this.onHiddenChanged(true)

        if (mActivity is BaseActivity && !mIsInViewPager && getNavigationFragment() != null) {
            getNavigationFragment()?.pushFragment(targetFragment)
        } else {
            startActivityRootFragment(targetFragment)
        }

    }

    @JvmOverloads
    fun <T : BaseFragment> startActivityRootFragment(rootFragment: T, newActivity: Boolean = false) {
        scheduleTaskAtStarted(Runnable {
            if (mActivity is BaseActivity && getNavigationFragment() != null && !mIsInViewPager && !newActivity) {
                (mActivity as BaseActivity).startActivityRootFragment(rootFragment(rootFragment))
            } else {
                val intent = Intent(mActivity, ContainerActivity::class.java)
                intent.putExtra(AppConfig.FRAGMENT, rootFragment.javaClass.canonicalName)
                intent.putExtra(AppConfig.BUNDLE, initArguments())
                mActivity.startActivity(intent)
            }
        }, true)
    }


    // ------- statusBar --------

    fun setNeedsNavigationBarAppearanceUpdate() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val parent = getParentBaseFragment()
        if (parent != null) {
            parent.setNeedsNavigationBarAppearanceUpdate()
        } else {
            val color = preferredNavigationBarColor()
            if (color != null) {
                setNavigationBarColor(color)
            } else {
                setNavigationBarColor(Color.WHITE)
            }
        }
    }

    //不推荐直接调用
    @JvmOverloads
    fun setStatusBarColor(color: Int, animated: Boolean = true) {
        StatusBarUtils.setStatusBarColor(getWindow()!!, color, animated)
    }

    fun setNavigationBarColor(color: Int) {
        StatusBarUtils.setNavigationBarColor(getWindow()!!, color)
    }

    fun setStatusBarStyle(barStyle: BarStyle) {
        StatusBarUtils.setStatusBarStyle(getWindow()!!, barStyle === BarStyle.DarkContent)
    }

    fun setStatusBarHidden(hidden: Boolean) {
        StatusBarUtils.setStatusBarHidden(getWindow()!!, hidden)
    }

    fun setStatusBarTranslucent(translucent: Boolean) {
        (mActivity as? BaseActivity)?.setStatusBarTranslucent(translucent)
    }

    fun appendStatusBarPadding(view: View, viewHeight: Int) {
        StatusBarUtils.appendStatusBarPadding(view, viewHeight)
    }

    fun removeStatusBarPadding(view: View, viewHeight: Int) {
        StatusBarUtils.removeStatusBarPadding(view, viewHeight)
    }

    fun isStatusBarTranslucent(): Boolean {
        if ((mActivity is BaseActivity)) {
            return (mActivity as? BaseActivity)?.isStatusBarTranslucent()!!
        }
        return true
    }

    //更新状态栏样式
    @JvmOverloads
    fun setNeedsStatusBarAppearanceUpdate(colorAnimated: Boolean = true) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }

        val parent = getParentBaseFragment()
        if (parent != null) {
            parent.setNeedsStatusBarAppearanceUpdate(colorAnimated)
            return
        }

        // statusBarHidden
        val hidden = preferredStatusBarHidden()
        setStatusBarHidden(hidden)

        // statusBarStyle
        val statusBarStyle = preferredStatusBarStyle()
        setStatusBarStyle(statusBarStyle)

        // statusBarColor
        val animated = preferredStatusBarColorAnimated() && colorAnimated
        if (hidden) {
            setStatusBarColor(Color.TRANSPARENT, animated)
        } else {
            var statusBarColor = preferredStatusBarColor()
            var shouldAdjustForWhiteStatusBar = !StatusBarUtils.isBlackColor(statusBarColor, 176)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                shouldAdjustForWhiteStatusBar = shouldAdjustForWhiteStatusBar && statusBarStyle === BarStyle.LightContent
            }

            if (shouldAdjustForWhiteStatusBar) {
                statusBarColor = AppConfig.shouldAdjustForWhiteStatusBar
            }

            if (isStatusBarTranslucent() && statusBarColor == Color.TRANSPARENT) {
                statusBarColor = Color.TRANSPARENT
            }

            setStatusBarColor(statusBarColor, animated)
        }
    }

    //状态栏颜色
    open fun preferredStatusBarColor(): Int {
        val childFragmentForStatusBarColor = childFragmentForAppearance()
        if (childFragmentForStatusBarColor != null) {
            return childFragmentForStatusBarColor.preferredStatusBarColor()
        }
        return AppConfig.statusBarColor
    }

    //虚拟键颜色
    @TargetApi(26)
    open fun preferredNavigationBarColor(): Int? {
        val childFragmentForAppearance = childFragmentForAppearance()
        return if (childFragmentForAppearance != null) {
            childFragmentForAppearance.preferredNavigationBarColor()
        } else AppConfig.navigationBarColor
    }

    //是否需要对颜色做过渡动画
    open fun preferredStatusBarColorAnimated(): Boolean {
        val childFragmentForStatusBarColor = childFragmentForAppearance()
        return childFragmentForStatusBarColor?.preferredStatusBarColorAnimated()
                ?: (getAnimation() !== PresentAnimation.None)
    }

    //状态栏文字颜色
    open fun preferredStatusBarStyle(): BarStyle {
        val childFragmentForStatusBarStyle = childFragmentForAppearance()
        if (childFragmentForStatusBarStyle != null) {
            return childFragmentForStatusBarStyle.preferredStatusBarStyle()
        }
        return AppConfig.statusBarStyle
    }

    //状态栏是否隐藏
    open fun preferredStatusBarHidden(): Boolean {
        val childFragmentForStatusBarHidden = childFragmentForAppearance()
        if (childFragmentForStatusBarHidden != null) {
            return childFragmentForStatusBarHidden.preferredStatusBarHidden()
        }
        return AppConfig.statusBarHidden
    }

}
