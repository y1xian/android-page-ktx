package com.yyxnb.yyxarch.base.mvvm

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.base.BaseFragment


/**
 * Description: mvvm
 *
 * @author : yyx
 * @date ：2018/6/10
 */
abstract class BaseFragmentVM<VM : BaseViewModel<*, *>> : BaseFragment() {

    /**
     * ViewModel
     */
    protected lateinit var mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = initViewModel(AppUtils.getInstance(this, 0)!!)
        lifecycle.addObserver(mViewModel)
    }

    override fun initViewData() {
        super.initViewData()
        mViewModel.state.observe(this, Observer {
            initViewObservable()
        })
    }

    /**
     * 回调网络数据
     */
    open fun initViewObservable() {}

    /**
     * 初始化ViewModel
     * create ViewModelProviders
     *
     * @return ViewModel
     */
    private fun initViewModel(modelClass: Class<VM>): VM {
        return ViewModelProviders.of(mActivity).get(modelClass)
    }

    override fun onDestroy() {
        super.onDestroy()
        //移除LifecycleObserver
        lifecycle.removeObserver(mViewModel)
        this.mViewModel to null
    }
}
