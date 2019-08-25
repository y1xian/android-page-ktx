package com.yyxnb.arch;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yanzhenjie.permission.AndPermission;
import com.yyxnb.arch.frag.ContentViewFragment;
import com.yyxnb.yyxarch.base.BaseActivity;
import com.yyxnb.yyxarch.interfaces.LayoutResId;

@LayoutResId(value = R.layout.activity_main)
public class MainActivity extends BaseActivity {

    String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        StatusBarUtils.INSTANCE.setStatusBarTranslucent(getWindow(), true,0);
////        StatusBarUtils.INSTANCE.setStatusBarColor(getWindow(), Color.TRANSPARENT,true);
////        StatusBarUtils.INSTANCE.setStatusBarStyle(getWindow(),true);
////        NotchScreenManager.getInstance().setDisplayInNotch(this);
//
//        setContentView(R.layout.activity_main);
//
////        FragmentHelper.INSTANCE.addFragment(getSupportFragmentManager(), R.id.mFrameLayout, ContentViewFragment.newInstance());
//        FragmentUtils.INSTANCE.replaceFragment(getSupportFragmentManager(), new ContentViewFragment(), R.id.mFrameLayout,false);
//
//        AndPermission.with(this)
//                .runtime()
//                .permission(permission)
//                .start();
//
////        NetworkLiveData.Companion.getInstance(this).observe(this, networkInfo ->
////                Log.d(" main ", "onChanged: networkInfo=" +networkInfo));
//
//    }

//    @Override
//    public void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        Log.d("---" , "刘海屏 " + StatusBarUtils.INSTANCE.isCutout(this));
//    }

    //    @Override
//    public int initLayoutResId() {
//        return R.layout.activity_main;
//    }
//
    @Override
    public void initView(Bundle savedInstanceState) {

//        startActivityRootFragment(ViewPageFragment.newInstance());
        startActivityRootFragment(ContentViewFragment.newInstance());

        AndPermission.with(this)
                .runtime()
                .permission(permission)
                .start();
    }

}
