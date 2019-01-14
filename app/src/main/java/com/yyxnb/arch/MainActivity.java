package com.yyxnb.arch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yyxnb.yyxarch.utils.FragmentUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentUtils.add(getSupportFragmentManager(),TestFragment.newInstance(),R.id.mFrameLayout);

    }
}
