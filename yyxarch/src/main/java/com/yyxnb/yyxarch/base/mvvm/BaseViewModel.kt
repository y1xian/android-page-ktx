package com.yyxnb.yyxarch.base.mvvm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.base.BaseRepository


/**
 * Description: BaseViewModel
 *
 * @author : yyx
 * @date ：2018/6/13
 */
open class BaseViewModel<T : BaseRepository<*>>(application: Application) : AndroidViewModel(application), DefaultLifecycleObserver {

    protected var mRepository: T? = null

    init {
        mRepository = AppUtils.getNewInstance<T>(this, 0)
    }

    override fun onCreate(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(mRepository!!)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(mRepository!!)
    }
}
