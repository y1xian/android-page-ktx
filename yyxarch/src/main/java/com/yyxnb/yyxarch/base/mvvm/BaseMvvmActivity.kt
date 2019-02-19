package com.yyxnb.yyxarch.base.mvvm

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.base.BaseActivity

abstract class BaseMvvmActivity<VM : BaseViewModel<*>> : BaseActivity() {

    /**
     * ViewModel
     */
    protected var mViewModel: VM? = null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mViewModel = initViewModel(this, AppUtils.getInstance(this, 0)!!)
        if (mViewModel != null) {
            lifecycle.addObserver(mViewModel!!)
        }
    }

    /**
     * 初始化ViewModel
     * create ViewModelProviders
     *
     * @return ViewModel
     */
    private fun initViewModel(activity: FragmentActivity, modelClass: Class<VM>): VM {
        return ViewModelProviders.of(activity).get(modelClass)
    }

    override fun onDestroy() {
        super.onDestroy()
        //移除LifecycleObserver
        if (mViewModel != null) {
            lifecycle.removeObserver(mViewModel!!)
        }
        this.mViewModel = null
    }
}
