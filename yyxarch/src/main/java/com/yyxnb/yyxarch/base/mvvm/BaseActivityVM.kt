package com.yyxnb.yyxarch.base.mvvm

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.base.BaseActivity

abstract class BaseActivityVM<VM : BaseViewModel<*, *>> : BaseActivity() {

    /**
     * ViewModel
     */
    protected lateinit var mViewModel: VM

    @CallSuper
    override fun initView(savedInstanceState: Bundle?) {
        mViewModel = initViewModel(this, AppUtils.getInstance(this)!!)
        lifecycle.addObserver(mViewModel)
        mViewModel.state.observe(this, Observer {
            renderState()
        })
    }

    /**
     * 初始化界面观察者的监听
     * 接收数据结果
     */
    open fun renderState() {}

    /**
     * 初始化ViewModel
     * create ViewModelProviders
     *
     * @return ViewModel
     */
    private fun initViewModel(activity: AppCompatActivity, modelClass: Class<VM>): VM {
        return ViewModelProviders.of(activity).get(modelClass)
    }

    override fun onDestroy() {
        super.onDestroy()
        //移除LifecycleObserver
        lifecycle.removeObserver(mViewModel)
        this.mViewModel to null
    }
}
