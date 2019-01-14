package com.yyxnb.yyxarch.base.mvvm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.yyxnb.yyxarch.AppUtils;
import com.yyxnb.yyxarch.base.BaseRepository;


/**
 * Description: BaseViewModel
 *
 * @author : yyx
 * @date ：2018/6/13
 */
public class BaseViewModel<T extends BaseRepository> extends AndroidViewModel implements DefaultLifecycleObserver {

    protected T mRepository;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppUtils.getNewInstance(this, 0);
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        owner.getLifecycle().addObserver(mRepository);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        owner.getLifecycle().removeObserver(mRepository);
    }
}
