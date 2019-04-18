package com.yyxnb.arch;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yanzhenjie.permission.AndPermission;
import com.yyxnb.arch.frag.ContentViewFragment;
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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//        }

        StatusBarUtils.INSTANCE.setStatusBarTranslucent(getWindow(), true);

        FragmentHelper.INSTANCE.addFragment(getSupportFragmentManager(), R.id.mFrameLayout, ContentViewFragment.newInstance());
        AndPermission.with(this)
                .runtime()
                .permission(permission)
                .start();
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
