package com.yyxnb.arch;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yanzhenjie.permission.AndPermission;
import com.yyxnb.yyxarch.utils.FragmentUtils;

public class MainActivity extends AppCompatActivity {

    String[] permission = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentUtils.add(getSupportFragmentManager(),ContentViewFragment.newInstance(),R.id.mFrameLayout);

        AndPermission.with(this)
                .runtime()
                .permission(permission)
                .start();
    }
}
