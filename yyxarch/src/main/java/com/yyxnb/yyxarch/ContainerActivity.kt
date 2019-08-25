package com.yyxnb.yyxarch

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import com.yyxnb.yyxarch.base.BaseActivity
import com.yyxnb.yyxarch.base.BaseFragment
import com.yyxnb.yyxarch.common.AppConfig
import com.yyxnb.yyxarch.ext.tryCatch
import com.yyxnb.yyxarch.utils.log.LogUtils


/**
 * Description: 盛装Fragment的一个容器(代理)Activity
 * 普通界面只需要编写Fragment,使用此Activity盛装,这样就不需要每个界面都在AndroidManifest中注册一遍
 *
 * @author : yyx
 * @date ：2018/6/9
 */
class ContainerActivity : BaseActivity() {

    override fun initView(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        tryCatch({
            if (null == intent) {
                throw RuntimeException("you must provide a page info to display")
            }
            val fragmentName = intent.getStringExtra(AppConfig.FRAGMENT)
            if (fragmentName.isEmpty()) {
                throw IllegalArgumentException("can not find page fragmentName")
            }
            val fragmentClass = Class.forName(fragmentName)
            val fragment = fragmentClass.newInstance() as BaseFragment
            intent.getBundleExtra(AppConfig.BUNDLE)?.let {
                fragment.arguments = it
            }
            startActivityRootFragment(fragment)
        }, {
            LogUtils.e(it.message.toString())
        })

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}

