package com.yyxnb.arch

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yanzhenjie.permission.AndPermission
import com.yyxnb.arch.frag.ContentViewFragment
import com.yyxnb.yyxarch.nav.FragmentHelper
import com.yyxnb.yyxarch.utils.StatusBarUtils

class MainActivity : AppCompatActivity() {

    internal var permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StatusBarUtils.setStatusBarTranslucent(window, true)

        FragmentHelper.addFragment(supportFragmentManager, R.id.mFrameLayout, ContentViewFragment())
        AndPermission.with(this)
                .runtime()
                .permission(permission)
                .start()

//        NetworkLiveData.getInstance(this).observe(this, { networkInfo -> Log.d(" main ", "onChanged: networkInfo=$networkInfo") })
    }

    //    @Override
    //    public int initLayoutResID() {
    //        return R.layout.activity_main;
    //    }
    //
    //    @Override
    //    public void initView(Bundle savedInstanceState) {
    //
    ////        startActivityRootFragment(ViewPageFragment.newInstance());
    //        startActivityRootFragment(ContentViewFragment.newInstance());
    //
    //        AndPermission.with(this)
    //                .runtime()
    //                .permission(permission)
    //                .start();
    //    }

}
