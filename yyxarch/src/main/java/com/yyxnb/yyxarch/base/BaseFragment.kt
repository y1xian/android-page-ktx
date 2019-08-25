package com.yyxnb.yyxarch.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import butterknife.ButterKnife
import butterknife.Unbinder
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.ContainerActivity
import com.yyxnb.yyxarch.R
import com.yyxnb.yyxarch.common.AppConfig
import com.yyxnb.yyxarch.interfaces.*
import com.yyxnb.yyxarch.nav.LifecycleDelegate
import com.yyxnb.yyxarch.interfaces.BarStyle
import com.yyxnb.yyxarch.utils.StatusBarUtils
import com.yyxnb.yyxarch.utils.log.LogUtils
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*

/**
 * Description: 懒加载 BaseFragment
 *
 * @author : yyx
 * @date ：2016/10
 */
abstract class BaseFragment : Fragment(), CoroutineScope by MainScope() {

    protected lateinit var mActivity: AppCompatActivity
    protected lateinit var mContext: Context

    protected val TAG = javaClass.simpleName

    protected var rootView: View? = null
    //是否第一次加载
    private var mIsFirstVisible = true
    //标志位，View已经初始化完成
    private var isViewCreated = false
    //可见状态
    private var currentVisibleState = false

    private var layoutResId = 0
    private var statusBarTranslucent = AppConfig.statusBarTranslucent
    private var fitsSystemWindows = true
    private var statusBarHidden = AppConfig.statusBarHidden
    private var statusBarColor = AppConfig.statusBarColor
    private var statusBarDarkTheme = AppConfig.statusBarStyle
    private var navigationBarDarkTheme = AppConfig.navigationBarStyle
    private var navigationBarColor = AppConfig.navigationBarColor
    private var swipeBack = AppConfig.swipeBackEnabled

    private val lifecycleDelegate by lazy { LifecycleDelegate(this) }

    protected var unBinder: Unbinder? = null

    private var sceneId = UUID.randomUUID().toString()

    fun getSceneId(): String = sceneId

    init {
        lifecycle.addObserver(Java8Observer)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as AppCompatActivity
        mIsFirstVisible = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = initArguments()
        if (bundle.size() > 0) {
            initVariables(bundle)
        }
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
        super.onCreateView(inflater, container, savedInstanceState)
        initAttributes()
        if (initLayoutResId() != 0) {
            layoutResId = initLayoutResId()
        }
        if (null == rootView) {
            rootView = inflater.inflate(layoutResId, container, false)
        }
        rootView!!.setOnTouchListener { _, event ->
            mActivity.onTouchEvent(event)
            return@setOnTouchListener false
        }
        unBinder = ButterKnife.bind(this, rootView!!)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isViewCreated = true
        //当设备旋转时，fragment会随托管activity一起销毁并重建。
//        retainInstance = true
        initView(savedInstanceState)

        // !isHidden() 默认为 true  在调用 hide show 的时候可以使用
        if (!isHidden && userVisibleHint) {
            // 这里的限制只能限制 A - > B 两层嵌套
            dispatchUserVisibleHint(true)
        }

//        setNeedsStatusBarAppearanceUpdate()
//        setNeedsNavigationBarAppearanceUpdate()
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
        javaClass.getAnnotation(TagValue::class.java)?.let { sceneId = it.value }
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

    @CallSuper
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        // 对于默认 tab 和 间隔 checked tab 需要等到 isViewCreated = true 后才可以通过此通知用户可见
        // 这种情况下第一次可见不是在这里通知 因为 isViewCreated = false 成立,等从别的界面回到这里后会使用 onFragmentResume 通知可见
        // 对于非默认 tab mIsFirstVisible = true 会一直保持到选择则这个 tab 的时候，因为在 onActivityCreated 会返回 false
        if (isViewCreated) {
            if (isVisibleToUser && !currentVisibleState) {
                dispatchUserVisibleHint(true)
            } else if (!isVisibleToUser && currentVisibleState) {
                dispatchUserVisibleHint(false)
            }
        }
    }

    @CallSuper
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        LogUtils.e("$TAG  onHiddenChanged dispatchChildVisibleState  hidden " + hidden)
        dispatchUserVisibleHint(!hidden)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        if (!mIsFirstVisible) {
            if (!isHidden && !currentVisibleState && userVisibleHint) {
                dispatchUserVisibleHint(true)
            }
        }
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        // 当前 Fragment 包含子 Fragment 的时候 dispatchUserVisibleHint 内部本身就会通知子 Fragment 不可见
        // 子 fragment 走到这里的时候自身又会调用一遍 ？
        if (currentVisibleState && userVisibleHint) {
            dispatchUserVisibleHint(false)
        }
    }

    private fun isFragmentVisible(fragment: Fragment): Boolean {
        return !fragment.isHidden && fragment.userVisibleHint
    }


    /**
     * 统一处理 显示隐藏
     *
     * @param visible
     */
    private fun dispatchUserVisibleHint(visible: Boolean) {
        //当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment getUserVisibleHint = true
        //但当父 fragment 不可见所以 currentVisibleState = false 直接 return 掉
        //        LogUtils.e(getClass().getSimpleName() + "  dispatchUserVisibleHint isParentInvisible() " + isParentInvisible());
        // 这里限制则可以限制多层嵌套的时候子 Fragment 的分发
        if (visible && isParentInvisible()) return

        //此处是对子 Fragment 不可见的限制，因为 子 Fragment 先于父 Fragment回调本方法 currentVisibleState 置位 false
        // 当父 dispatchChildVisibleState 的时候第二次回调本方法 visible = false 所以此处 visible 将直接返回
        if (currentVisibleState == visible) {
            return
        }

        currentVisibleState = visible

        if (visible) {
            if (mIsFirstVisible) {
                mIsFirstVisible = false
                initViewData()
            }
            onVisible()
            dispatchChildVisibleState(true)
        } else {
            dispatchChildVisibleState(false)
            onInVisible()
        }
    }

    /**
     * 用于分发可见时间的时候父获取 fragment 是否隐藏
     *
     * @return true fragment 不可见， false 父 fragment 可见
     */
    private fun isParentInvisible(): Boolean {
        val fragment = parentFragment as BaseFragment?
        return fragment != null && !fragment.isSupportVisible()

    }

    private fun isSupportVisible(): Boolean {
        return currentVisibleState
    }

    /**
     * 当前 Fragment 是 child 时候 作为缓存 Fragment 的子 fragment 的唯一或者嵌套 VP 的第一 fragment 时 getUserVisibleHint = true
     * 但是由于父 Fragment 还进入可见状态所以自身也是不可见的， 这个方法可以存在是因为庆幸的是 父 fragment 的生命周期回调总是先于子 Fragment
     * 所以在父 fragment 设置完成当前不可见状态后，需要通知子 Fragment 我不可见，你也不可见，
     *
     * 因为 dispatchUserVisibleHint 中判断了 isParentInvisible 所以当 子 fragment 走到了 onActivityCreated 的时候直接 return 掉了
     *
     * 当真正的外部 Fragment 可见的时候，走 setVisibleHint (VP 中)或者 onActivityCreated (hide show) 的时候
     * 从对应的生命周期入口调用 dispatchChildVisibleState 通知子 Fragment 可见状态
     *
     * @param visible
     */
    private fun dispatchChildVisibleState(visible: Boolean) {
        val childFragmentManager = childFragmentManager
        val fragments = childFragmentManager.fragments
        if (fragments.isNotEmpty()) {
            for (child in fragments) {
                if (child is BaseFragment && !child.isHidden() && child.getUserVisibleHint()) {
                    (child).dispatchUserVisibleHint(visible)
                }
            }
        }
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        requireFragmentManager().unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks)
        unBinder?.unbind()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        mIsFirstVisible = true
        rootView = null
        cancel() // 关闭页面后，结束所有协程任务
    }

    private var fragmentLifecycleCallbacks: FragmentManager.FragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            if (fm === f.fragmentManager && targetFragment === f) {
                setTargetFragment(f.targetFragment, f.targetRequestCode)
            }
        }
    }

    /**
     * 被ViewPager移出的Fragment 下次显示时会从getArguments()中重新获取数据
     * 所以若需要刷新被移除Fragment内的数据需要重新put数据
     */
    open fun initVariables(bundle: Bundle) {
    }

    /**
     * 当界面可见时的操作
     */
    @CallSuper
    open fun onVisible() {
        setNeedsStatusBarAppearanceUpdate()
        setNeedsNavigationBarAppearanceUpdate()
        AppUtils.debugLog("onVisible $TAG")
    }

    /**
     * 当界面不可见时的操作
     */
    @CallSuper
    open fun onInVisible() {
        AppUtils.debugLog("onInVisible $TAG")
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    @LayoutRes
    open fun initLayoutResId(): Int = 0

    /**
     * 初始化控件
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化复杂数据 懒加载
     */
    open fun initViewData() {}

    fun <T> findViewById(@IdRes resId: Int): T {
        return rootView!!.findViewById<View>(resId) as T
    }

    /**
     * 返回.
     */
    fun finish() {
        mActivity.onBackPressed()
    }

    // ------ lifecycle arch -------

    @JvmOverloads
    fun scheduleTaskAtStarted(runnable: Runnable, deferred: Boolean = true, interval: Long = 100L) {
        lifecycleDelegate.scheduleTaskAtStarted(runnable, deferred, interval)
    }

    fun getDebugTag(): String? {
        if (activity == null) {
            return null
        }
        val parent = getParentBaseFragment()
        return if (parent == null) {
            "#" + getIndexAtAddedList() + "-" + TAG
        } else {
            parent.getDebugTag() + "#" + getIndexAtAddedList() + "-" + TAG
        }
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

    fun getWindow(): Window {
        return mActivity.window
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
        return instantiate(context, targetFragment.javaClass.canonicalName, bundle) as T
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

    /**
     * 跳转 fragment.
     *
     * @param targetFragment 目标fragment.
     * @param requestCode    请求码.
     * @param T          [BaseFragment].
     */
    fun <T : BaseFragment> startFragment(targetFragment: T) {
        onHiddenChanged(true)
        scheduleTaskAtStarted(Runnable {
            val intent = Intent(mActivity, ContainerActivity::class.java)
            intent.putExtra(AppConfig.FRAGMENT, targetFragment.javaClass.canonicalName)
            intent.putExtra(AppConfig.BUNDLE, initArguments())
            mActivity.startActivity(intent)
            mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }, true)
    }

    // ------- statusBar --------

    fun setNeedsNavigationBarAppearanceUpdate() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        setNavigationBarColor(navigationBarColor)
        setNavigationBarStyle(navigationBarDarkTheme)
    }

    fun setStatusBarTranslucent(translucent: Boolean, fitsSystemWindows: Boolean) = (mActivity as? BaseActivity)?.setStatusBarTranslucent(translucent, fitsSystemWindows)
            ?: let { StatusBarUtils.setStatusBarTranslucent(getWindow(), translucent, fitsSystemWindows) }

    fun setStatusBarColor(color: Int) = (mActivity as? BaseActivity)?.setStatusBarColor(color)
            ?: let { StatusBarUtils.setStatusBarColor(getWindow(), color) }

    fun setStatusBarStyle(barStyle: BarStyle) = (mActivity as? BaseActivity)?.setStatusBarStyle(barStyle)
            ?: let { StatusBarUtils.setStatusBarStyle(getWindow(), barStyle === BarStyle.DarkContent) }

    fun setStatusBarHidden(hidden: Boolean) = (mActivity as? BaseActivity)?.setStatusBarHidden(hidden)
            ?: let { StatusBarUtils.setStatusBarHidden(getWindow(), hidden) }

    fun setNavigationBarColor(color: Int) = (mActivity as? BaseActivity)?.setNavigationBarColor(color)
            ?: let { StatusBarUtils.setNavigationBarColor(getWindow(), color) }

    fun setNavigationBarStyle(barStyle: BarStyle) = (mActivity as? BaseActivity)?.setNavigationBarStyle(barStyle)
            ?: let { StatusBarUtils.setNavigationBarStyle(getWindow(), barStyle === BarStyle.DarkContent) }


    //更新状态栏样式
    fun setNeedsStatusBarAppearanceUpdate() {

        // statusBarHidden
        val hidden = statusBarHidden
        setStatusBarHidden(hidden)

        // statusBarStyle
        val statusBarStyle = statusBarDarkTheme
        setStatusBarStyle(statusBarStyle)

        // statusBarColor
        if (hidden) {
            setStatusBarColor(Color.TRANSPARENT)
        } else {
            var statusBarColor = statusBarColor
            var shouldAdjustForWhiteStatusBar = !StatusBarUtils.isBlackColor(statusBarColor, 176)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                shouldAdjustForWhiteStatusBar = shouldAdjustForWhiteStatusBar && statusBarStyle === BarStyle.LightContent
            }

            if (shouldAdjustForWhiteStatusBar) {
                statusBarColor = AppConfig.shouldAdjustForWhiteStatusBar
            }

            setStatusBarColor(statusBarColor)
        }
        setStatusBarTranslucent(statusBarTranslucent, fitsSystemWindows)
    }

}

