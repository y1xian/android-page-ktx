package com.yyxnb.yyxarch.base

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.yyxnb.yyxarch.ContainerActivity
import com.yyxnb.yyxarch.common.Config.BUNDLE
import com.yyxnb.yyxarch.common.Config.FRAGMENT
import java.util.concurrent.TimeUnit


/**
 * Description:
 *
 * @author : yyx
 * @date ：2018/6/10
 */
abstract class BaseFragment : Fragment(), View.OnClickListener {

    protected var mActivity: FragmentActivity? = null

    private val TAG = javaClass.simpleName

    var rootView: View? = null
        private set
    //是否可见状态
    private var mIsVisible: Boolean = false
    //标志位，View已经初始化完成
    private var mIsPrepared: Boolean = false
    //是否第一次加载
    private var mIsFirstVisible = true

    val isBackPressed: Boolean
        get() = false

    init {
        lifecycle.addObserver(Java8Observer())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null && bundle.size() > 0) {
            initVariables(bundle)
        }
        mActivity = activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (null == rootView) {
            mIsFirstVisible = true
            rootView = inflater.inflate(initLayoutResID(), container, false)
            mIsPrepared = true
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewData(savedInstanceState)
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
    fun initVariables(bundle: Bundle) {}

    /**
     * 当界面可见时的操作
     */
    protected fun onVisible() {
        initLazyLoadView()
    }

    /**
     * 当界面不可见时的操作
     */
    protected fun onInVisible() {}

    /**
     * 数据懒加载
     */
    protected fun initLazyLoadView() {
        if (!mIsPrepared || !mIsVisible || mIsFirstVisible) {
            return
        }
        mIsFirstVisible = false
    }

    override fun onClick(v: View) {
        RxView.clicks(v).throttleFirst(1, TimeUnit.SECONDS).subscribe { o -> onClickWidget(v) }
    }

    /*
      防止快速点击
    */
    fun onClickWidget(v: View) {

    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    @JvmOverloads
    fun startActivity(clz: Class<*>, bundle: Bundle? = null) {
        val intent = Intent(mActivity, clz)
        if (null != bundle) {
            intent.putExtra(BUNDLE, bundle)
        }
        startActivity(intent)
    }

    /**
     * 跳转容器页面
     *
     * @param fragment 规范名 : Fragment.newInstance()
     * @param bundle   跳转所携带的信息
     */
    @JvmOverloads
    fun startContainerActivity(fragment: BaseFragment, bundle: Bundle? = null) {
        startContainerActivity(fragment.javaClass.canonicalName, bundle)
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : com.yyxnb.yyxarch.base.BaseFragment
     * @param bundle        跳转所携带的信息
     */
    @JvmOverloads
    fun startContainerActivity(canonicalName: String?, bundle: Bundle? = null) {
        val intent = Intent(mActivity, ContainerActivity::class.java)
        intent.putExtra(FRAGMENT, canonicalName)
        if (bundle != null) {
            intent.putExtra(BUNDLE, bundle)
        }
        startActivity(intent)
    }

    /**
     * 跳转容器页面
     *
     * @param requestCode requestCode
     * @param fragment    规范名 : Fragment.newInstance()
     */
    fun startContainerActivityForResult(requestCode: Int, fragment: BaseFragment) {
        startContainerActivityForResult(requestCode, fragment.javaClass.canonicalName, null)
    }

    /**
     * 跳转容器页面
     *
     * @param requestCode   requestCode
     * @param canonicalName 规范名 : com.yyxnb.yyxarch.base.BaseFragment
     * @param bundle        跳转所携带的信息
     */
    @JvmOverloads
    fun startContainerActivityForResult(requestCode: Int, canonicalName: String?, bundle: Bundle? = null) {
        val intent = Intent(mActivity, ContainerActivity::class.java)
        intent.putExtra(FRAGMENT, canonicalName)
        if (bundle != null) {
            intent.putExtra(BUNDLE, bundle)
        }
        startActivityForResult(intent, requestCode)
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
     * 初始化复杂数据
     */
    open fun initViewData(savedInstanceState: Bundle?) {}

    /**
     * 回调网络数据
     */
    open fun initViewObservable() {}

    protected fun <T> fv(@IdRes resid: Int): T {
        return rootView!!.findViewById<View>(resid) as T
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mIsPrepared = false
    }

}
/**
 * 跳转页面
 *
 * @param clz 所跳转的目的Activity类
 */
/**
 * 跳转容器页面
 *
 * @param canonicalName 规范名 : com.yyxnb.yyxarch.base.BaseFragment
 */
/**
 * 跳转容器页面
 *
 * @param fragment 规范名 : Fragment.newInstance()
 */
/**
 * 跳转容器页面
 *
 * @param requestCode   requestCode
 * @param canonicalName 规范名 : com.yyxnb.yyxarch.base.BaseFragment
 */
