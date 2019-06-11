package com.yyxnb.yyxarch.base.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.yyxnb.yyxarch.AppUtils
import com.yyxnb.yyxarch.http.RetrofitManager


/**
 * 逻辑处理
 *
 * 负责数据处理和View层与Model层的交互。
 * ViewModel通过数据仓库Repository获取数据来源，处理来自View的事件命令，同时更新数据。
 * @author : yyx
 * @date ：2018/6/13
 */
abstract class BaseViewModel<T : BaseRepository<*>>(application: Application) : AndroidViewModel(application), DefaultLifecycleObserver {

    protected lateinit var mRepository: T

    init {
        mRepository = AppUtils.getNewInstance<T>(this, 0)!!
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        owner.lifecycle.addObserver(mRepository)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        owner.lifecycle.removeObserver(mRepository)
    }

    override fun onCleared() {
        super.onCleared()
        RetrofitManager.cancelAllRequest()
    }
}
