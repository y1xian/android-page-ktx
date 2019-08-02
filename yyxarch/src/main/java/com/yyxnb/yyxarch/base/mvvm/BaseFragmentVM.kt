package com.yyxnb.yyxarch.base.mvvm

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.base.BaseFragment
import com.yyxnb.yyxarch.bean.SharedData


/**
 * Description: mvvm
 *
 * @author : yyx
 * @date ：2018/6/10
 */
abstract class BaseFragmentVM<VM : BaseViewModel<*>> : BaseFragment() {

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
        mViewModel.sharedData.observe(this, observer)
        initObservable()
    }

    // 分发状态
    private val observer by lazy {
        Observer<SharedData> { sharedData ->
            sharedData?.run {
                when (type) {
//                    SharedType.SUCCESS -> showSuccess()
//                    SharedType.ERROR -> showError(msg)
//                    SharedType.LOADING -> showLoading()
//                    SharedType.TIPS -> showTips(strRes)
//                    SharedType.EMPTY -> showEmptyView()
                }
            }
        }
    }

    /**
     * 回调网络数据
     */
    open fun initObservable() {}

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
