package com.yyxnb.yyxarch.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.NonNull
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.ContainerActivity
import com.yyxnb.yyxarch.base.BaseActivity.FragmentStackEntity
import com.yyxnb.yyxarch.common.AppConfig


/**
 * Description:
 *
 * @author : yyx
 * @date ：2018/6/10
 */
abstract class BaseFragment : Fragment() {

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

    private val REQUEST_CODE_INVALID = BaseActivity.REQUEST_CODE_INVALID

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
        val bundle = arguments
        if (bundle != null && bundle.size() > 0) {
            initVariables(bundle)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == rootView) {
            rootView = inflater.inflate(initLayoutResID(), container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mIsPrepared = true
        initView(savedInstanceState)
        if (isSingleFragment() && !mIsVisible) {
            if (userVisibleHint || isVisible || !isHidden) {
                onVisibleChanged(true)
            }
        }
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     */
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
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     * @param hidden  如果该Fragment对象已经被隐藏，那么它返回true。
     */
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
    }


    /**
     * 被ViewPager移出的Fragment 下次显示时会从getArguments()中重新获取数据
     * 所以若需要刷新被移除Fragment内的数据需要重新put数据 eg:
     * Bundle args = getArguments();
     * if (args != null) {
     * args.putParcelable(KEY, info);
     * }
     */
    open fun initVariables(bundle: Bundle) {}

    /**
     * 当界面可见时的操作
     */
    open fun onVisible() {
        AppUtils.debugLog("onVisible ${javaClass.simpleName}")
        initLazyLoadView()
    }

    /**
     * 当界面不可见时的操作
     */
    open fun onInVisible() {AppUtils.debugLog("onInVisible ${javaClass.simpleName}")}

    /**
     * 用户可见变化回调
     */
    private fun onVisibleChanged(isVisibleToUser: Boolean) {
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
    }

    /**
     * 数据懒加载
     */
    private fun initLazyLoadView() {
        if (mIsFirstVisible && mIsPrepared && mIsVisible) {
            mIsFirstVisible = false
            initViewData()
            initViewObservable()
            AppUtils.debugLog("initLazyLoadView ${javaClass.simpleName}")
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
    open fun initView(savedInstanceState: Bundle?) {}

    /**
     * 初始化复杂数据 懒加载
     */
    open fun initViewData() {}

    /**
     * 回调网络数据
     */
    open fun initViewObservable() {}

    open fun <T> fv(@IdRes resId: Int): T {
        return rootView!!.findViewById<View>(resId) as T
    }

    override fun onResume() {
        super.onResume()
        if (isAdded && isVisibleToUser(this)) {
            onVisibleChanged(userVisibleHint && !isHidden)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isAdded && !isVisibleToUser(this)) {
            onVisibleChanged(false)
        }
    }

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
        if (fragment.parentFragment != null ) {
            return if (fragment.parentFragment is BaseFragment){
                isVisibleToUser(fragment.parentFragment as BaseFragment) && if (fragment.isInViewPager()) fragment.userVisibleHint else fragment.isVisible
            }else if (fragment.parentFragment is DialogFragment){
                true
            }else{
                if (fragment.isInViewPager()) fragment.userVisibleHint else fragment.isVisible
            }
        }
        return if (fragment.isInViewPager()) fragment.userVisibleHint else fragment.isVisible
    }

    /**
     * 是否在ViewPager
     */
    open fun isInViewPager(): Boolean {
        return mIsInViewPager
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
    protected fun isSingleFragment(): Boolean {
        var size = 0
        val manager = fragmentManager
        if (manager != null && manager.fragments.isNotEmpty()) {
            size = manager.fragments.size
        }
        return size <= 1
    }

    //*******************跳转*************

    /**
     * 使用给定的类名创建Fragment的新实例。 这与调用其空构造函数相同。
     *
     * @param fragmentClass 目标 fragment.
     * @param T          [BaseFragment].
     * @return new instance.
     */
    fun <T : BaseFragment> fragment(fragmentClass: Class<T>): T {

        return Fragment.instantiate(context, fragmentClass.canonicalName, null) as T
    }

    /**
     * 使用给定的类名创建Fragment的新实例。 这与调用其空构造函数相同。
     *
     * @param fragmentClass 目标fragment class.
     * @param bundle        argument.
     * @param T           [BaseFragment].
     * @return new instance.
     */
    fun <T : BaseFragment> fragment(fragmentClass: Class<T>, bundle: Bundle): T {

        return Fragment.instantiate(context, fragmentClass.canonicalName, bundle) as T
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
     * 跳转 activity 并 finish.
     *
     * @param clazz 目标activity class.
     * @param <T>   [Activity].
    </T> */
    protected fun <T : Activity> startActivityFinish(clazz: Class<T>) {
        startActivity(Intent(mActivity, clazz))
        mActivity.finish()
    }


    /**
     * 跳转容器页面.
     *
     * @param targetFragment 目标fragment.
     * @param result   跳转所携带的信息
     */
    fun <T : BaseFragment> startContainerActivity(targetFragment: T) {
        startContainerActivity(targetFragment, null)
    }

    /**
     * 跳转容器页面.
     *
     * @param targetFragment 目标fragment.
     * @param result        跳转所携带的信息
     */
    fun <T : BaseFragment> startContainerActivity(targetFragment: T, result: Bundle?) {
        val intent = Intent(mActivity, ContainerActivity::class.java)
        intent.putExtra(AppConfig.FRAGMENT, targetFragment.javaClass.canonicalName)
        if (result != null) {
            intent.putExtra(AppConfig.BUNDLE, result)
        }
        startActivity(intent)
    }


    // ------------------------- Stack ------------------------- //

    /**
     * 堆栈信息.
     */
    private var mStackEntity = FragmentStackEntity()


    /**
     * 设置堆栈信息.
     */
    fun setStackEntity(@NonNull stackEntity: FragmentStackEntity) {
        this.mStackEntity = stackEntity
    }

    /**
     * 设置结果码.
     *
     * @param resultCode [RESULT_OK], [RESULT_CANCELED].
     */
    fun setResult(resultCode: Int) {
        mStackEntity.resultCode = resultCode
    }

    /**
     * 设置结果码和回传结果.
     *
     * @param resultCode 结果码.
     * @param result     跳转所携带的信息.
     */
    fun setResult(resultCode: Int, @NonNull result: Bundle) {
        mStackEntity.resultCode = resultCode
        mStackEntity.result = result
    }

    /**
     * 处理返回结果.
     * BaseActivity上才有效
     *
     * @param resultCode 结果码.
     * @param result     跳转所携带的信息.
     */
    open fun onFragmentResult(requestCode: Int, resultCode: Int, result: Bundle?) {}

    /**
     * 跳转 fragment.
     *
     * @param clazz 目标fragment class.
     * @param T   [BaseFragment].
     */
    fun <T : BaseFragment> startFragment(clazz: Class<T>) {
        try {
            val targetFragment = clazz.newInstance()
            startFragment<BaseFragment>(targetFragment, true, REQUEST_CODE_INVALID)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 跳转 fragment.
     *
     * @param clazz       目标fragment class.
     * @param stickyStack 是否加入堆栈.
     * @param T         [BaseFragment].
     */
    fun <T : BaseFragment> startFragment(clazz: Class<T>, stickyStack: Boolean) {
        try {
            val targetFragment = clazz.newInstance()
            startFragment<BaseFragment>(targetFragment, stickyStack, REQUEST_CODE_INVALID)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 跳转 fragment.
     *
     * @param targetFragment 目标fragment.
     * @param T            [BaseFragment].
     */
    fun <T : BaseFragment> startFragment(targetFragment: T) {
        startFragment(targetFragment, true, REQUEST_CODE_INVALID)
    }

    /**
     * 跳转 fragment.
     *
     * @param targetFragment 目标fragment.
     * @param stickyStack    是否加入堆栈.
     * @param T            [BaseFragment].
     */
    fun <T : BaseFragment> startFragment(targetFragment: T, stickyStack: Boolean) {
        startFragment(targetFragment, stickyStack, REQUEST_CODE_INVALID)
    }

    /**
     * 跳转 fragment 返回结果.
     *
     * @param clazz       目标fragment class.
     * @param requestCode 请求码.
     * @param T         [BaseFragment].
     */
    fun <T : BaseFragment> startFragmentForResult(clazz: Class<T>, requestCode: Int) {
        try {
            val targetFragment = clazz.newInstance()
            startFragment<BaseFragment>(targetFragment, true, requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 跳转 fragment 返回结果.
     *
     * @param targetFragment 目标fragment.
     * @param requestCode    请求码.
     * @param T           [BaseFragment].
     */
    fun <T : BaseFragment> startFragmentForResult(targetFragment: T, requestCode: Int) {
        startFragment(targetFragment, true, requestCode)
    }

    /**
     * 跳转 fragment.
     *
     * @param targetFragment 目标fragment.
     * @param stickyStack    是否加入堆栈.
     * @param requestCode    请求码.
     * @param T          [BaseFragment].
     */
    private fun <T : BaseFragment> startFragment(targetFragment: T, stickyStack: Boolean, requestCode: Int) {
        if (mActivity is BaseActivity) {
            (mActivity as? BaseActivity)?.startFragment(this, targetFragment, stickyStack, requestCode)
        } else {
            val intent = Intent(mActivity, ContainerActivity::class.java)
            intent.putExtra(AppConfig.FRAGMENT, targetFragment.javaClass.canonicalName)
            intent.putExtra(AppConfig.REQUEST_CODE, requestCode)
            mActivity.startActivity(intent)
        }
    }


}
