package com.yyxnb.yyxarch.base.mvvm

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.base.BaseFragment


/**
 * Description: mvvm
 *
 * @author : yyx
 * @date ：2018/6/10
 */
abstract class BaseMvvmFragment<VM : BaseViewModel<*>> : BaseFragment() {

    /**
     * ViewModel
     */
    protected lateinit var mViewModel: VM

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mViewModel = initViewModel(this, AppUtils.getInstance(this, 0)!!)
        lifecycle.addObserver(mViewModel)
        initViewObservable()
    }

    /**
     * 初始化ViewModel
     * create ViewModelProviders
     *
     * @return ViewModel
     */
    private fun initViewModel(targetFragment: BaseFragment, modelClass: Class<VM>): VM {
        return ViewModelProviders.of(targetFragment).get(modelClass)
    }

    override fun onDestroy() {
        super.onDestroy()
        //移除LifecycleObserver
        lifecycle.removeObserver(mViewModel)
        this.mViewModel to null
    }
}
