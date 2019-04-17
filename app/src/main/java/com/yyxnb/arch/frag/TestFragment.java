package com.yyxnb.arch.frag;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yyxnb.arch.R;
import com.yyxnb.yyxarch.base.BaseFragment;
import com.yyxnb.yyxarch.nav.NavigationFragment;
import com.yyxnb.yyxarch.utils.ToastUtils;

import org.jetbrains.annotations.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends BaseFragment {

    private TextView tvShow;
    private Button button,button1,button2,button3;

    @Override
    public int initLayoutResID() {
        return R.layout.fragment_test;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        tvShow = fv(R.id.tvShow);
        button = fv(R.id.button);
        button1 = fv(R.id.button1);
        button2 = fv(R.id.button2);
        button3 = fv(R.id.button3);

    }

    @Override
    public void initViewData() {
        super.initViewData();

        tvShow.setText(getDebugTag());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivityRootFragment(new TestFragment());
            }
        });


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startFragment(new TestFragment());

            }
        });

        button2.setOnClickListener(v -> {

            startFragment(new TwoFragment(),0x111);
        });

        button3.setOnClickListener(v -> {

            NavigationFragment navigationFragment = getNavigationFragment();
            if (navigationFragment != null) {
                navigationFragment.popToRootFragment();
            }
        });


    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, @Nullable Bundle result) {
        super.onFragmentResult(requestCode, resultCode, result);
        if (requestCode == 0x111 && resultCode == 0x110){
            ToastUtils.INSTANCE.normal("0x111 " + result.getString("msg","null"));
        }
    }

    public static TestFragment newInstance() {

        Bundle args = new Bundle();

        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
