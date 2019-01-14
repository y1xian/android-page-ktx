package com.yyxnb.yyxarch.base.mvvm;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.yyxnb.yyxarch.AppUtils;
import com.yyxnb.yyxarch.base.BaseFragment;


/**
 * Description: mvvm
 *
 * @author : yyx
 * @date ：2018/6/10
 */
public abstract class BaseMvvmFragment<VM extends BaseViewModel> extends BaseFragment {

    /**
     * ViewModel
     */
    protected VM mViewModel;

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mViewModel = initViewModel(this, (AppUtils.getInstance(this, 0)));
        if (null != mViewModel) {
            getLifecycle().addObserver(mViewModel);
            initViewObservable();
        }
    }

    /**
     * 初始化ViewModel
     * create ViewModelProviders
     *
     * @return ViewModel
     */
    private VM initViewModel(BaseFragment fragment, @NonNull Class<VM> modelClass) {
        return ViewModelProviders.of(fragment).get(modelClass);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //移除LifecycleObserver
        if (mViewModel != null) {
            getLifecycle().removeObserver(mViewModel);
        }
        this.mViewModel = null;
    }
}
