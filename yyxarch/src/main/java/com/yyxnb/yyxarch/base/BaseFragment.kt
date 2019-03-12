package com.yyxnb.yyxarch.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yyxnb.yyxarch.ContainerActivity
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

    private val REQUEST_CODE_INVALID = BaseActivity.REQUEST_CODE_INVALID

    init {
        lifecycle.addObserver(Java8Observer)
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
            mIsFirstVisible = true
            rootView = inflater.inflate(initLayoutResID(), container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mIsPrepared = true
    }


    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (null == rootView) {
            return
        }
        if (userVisibleHint) {
            mIsVisible = true
            onVisible()
        } else {
            mIsVisible = false
            onInVisible()
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
        if (!hidden) {
            mIsVisible = true
            onVisible()
        } else {
            mIsVisible = false
            onInVisible()
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
        initLazyLoadView()
    }

    /**
     * 当界面不可见时的操作
     */
    open fun onInVisible() {}

    /**
     * 数据懒加载
     */
    private fun initLazyLoadView() {
        if (!mIsPrepared || !mIsVisible || !mIsFirstVisible) {
            return
        }
        initViewData()
        initViewObservable()
        mIsFirstVisible = false
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

    open fun <T> fv(@IdRes resid: Int): T {
        return rootView!!.findViewById<View>(resid) as T
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint) {
            userVisibleHint = true
        }
    }

    override fun onPause() {
        super.onPause()
        mIsVisible = false
        onInVisible()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mIsVisible = false
        mIsPrepared = false
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    /**
     * 返回.
     */
    fun finish() {
        mActivity.onBackPressed()
    }


    fun getIsVisible():Boolean = mIsVisible;

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
        val intent = Intent(mActivity, ContainerActivity::class.java)
        intent.putExtra(AppConfig.FRAGMENT, targetFragment.javaClass.canonicalName)
        startActivity(intent)
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
    private lateinit var mStackEntity: BaseActivity.FragmentStackEntity


    /**
     * 设置堆栈信息.
     */
    fun setStackEntity(@NonNull stackEntity: BaseActivity.FragmentStackEntity) {
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
    @Deprecated("use {@link #startFragmentForResult(Class, int)} instead.")
    fun <T : BaseFragment> startFragmentForResquest(clazz: Class<T>, requestCode: Int) {
        startFragmentForResult(clazz, requestCode)
    }

    /**
     * 跳转 fragment 返回结果.
     *
     * @param targetFragment 目标fragment.
     * @param requestCode    请求码.
     * @param T            [BaseFragment].
     */
    @Deprecated("use {@link #startFragmentForResult(Class, int)} instead.")
    fun <T : BaseFragment> startFragmentForResquest(targetFragment: T, requestCode: Int) {
        startFragmentForResult(targetFragment, requestCode)
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
        (mActivity as? BaseActivity)?.startFragment(this, targetFragment, stickyStack, requestCode)
    }


}
