package com.yyxnb.yyxarch.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import com.jakewharton.rxbinding2.view.RxView
import com.yyxnb.yyxarch.utils.ActivityStack
import com.yyxnb.yyxarch.utils.FragmentUtils
import java.util.concurrent.TimeUnit


/**
 * Description:
 *
 * @author : yyx
 * @date : 2018/6/10
 */
abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = javaClass.simpleName

    private val generateViewId = android.view.View.generateViewId()


//    protected val scopeProvider: AndroidLifecycleScopeProvider by lazy {
//        AndroidLifecycleScopeProvider.from(this)
//    }

    var fragmentContainer: FrameLayout? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(Java8Observer())

        if (initLayoutResID() == 0) {
            fragmentContainer = FrameLayout(this)
            fragmentContainer!!.id = generateViewId
            setContentView(fragmentContainer)
        } else {
            setContentView(initLayoutResID())
        }

        initView(savedInstanceState)

        initViewObservable()

        ActivityStack.instance.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityStack.instance.finishActivity(this)
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    protected abstract fun initLayoutResID(): Int

    /**
     * 初始化
     */
    open fun initView(savedInstanceState: Bundle?) {}

    /**
     * 初始化界面观察者的监听
     * 接收数据结果
     */
    fun initViewObservable() {}


    @SuppressLint("AutoDispose")
    override fun onClick(v: View) {
        RxView.clicks(v).throttleFirst(1, TimeUnit.SECONDS).subscribe { o -> onClickWidget(v) }
    }

    /*
      防止快速点击
    */
    fun onClickWidget(v: View) {}

    fun startFragment(fragment: BaseFragment, containerViewId: Int) {
        FragmentUtils.add(supportFragmentManager, fragment, containerViewId)
    }
}
