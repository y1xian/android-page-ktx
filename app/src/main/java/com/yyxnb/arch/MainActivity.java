package com.yyxnb.arch;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.yanzhenjie.permission.AndPermission;
import com.yyxnb.arch.frag.ContentViewFragment;
import com.yyxnb.yyxarch.livedata.NetworkLiveData;
import com.yyxnb.yyxarch.nav.FragmentHelper;
import com.yyxnb.yyxarch.utils.StatusBarUtils;

public class MainActivity extends AppCompatActivity {

    String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtils.INSTANCE.setStatusBarTranslucent(getWindow(), true);

        FragmentHelper.INSTANCE.addFragment(getSupportFragmentManager(), R.id.mFrameLayout, ContentViewFragment.newInstance());
        AndPermission.with(this)
                .runtime()
                .permission(permission)
                .start();

        NetworkLiveData.Companion.getInstance(this).observe(this, networkInfo ->
                Log.d(" main ", "onChanged: networkInfo=" +networkInfo));
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
