package com.yyxnb.yyxarch.base.mvvm

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.base.BaseActivity

abstract class BaseMvvmActivity<VM : BaseViewModel<*>> : BaseActivity() {

    /**
     * ViewModel
     */
    protected lateinit var mViewModel: VM

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mViewModel = initViewModel(this, AppUtils.getInstance(this, 0)!!)
        lifecycle.addObserver(mViewModel)
    }

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
