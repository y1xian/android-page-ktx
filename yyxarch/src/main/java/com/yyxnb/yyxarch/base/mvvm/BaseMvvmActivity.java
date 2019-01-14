package com.yyxnb.yyxarch.base.mvvm;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.yyxnb.yyxarch.AppUtils;
import com.yyxnb.yyxarch.base.BaseActivity;

public abstract class BaseMvvmActivity<VM extends BaseViewModel> extends BaseActivity {

    /**
     * ViewModel
     */
    protected VM mViewModel;

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mViewModel = initViewModel(this, (AppUtils.getInstance(this, 0)));
        if (mViewModel != null) {
            getLifecycle().addObserver(mViewModel);
        }
    }

    /**
     * 初始化ViewModel
     * create ViewModelProviders
     *
     * @return ViewModel
     */
    private VM initViewModel(BaseActivity activity, @NonNull Class<VM> modelClass) {
        return ViewModelProviders.of(activity).get(modelClass);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除LifecycleObserver
        if (mViewModel != null) {
            getLifecycle().removeObserver(mViewModel);
        }
        this.mViewModel = null;
    }
}
