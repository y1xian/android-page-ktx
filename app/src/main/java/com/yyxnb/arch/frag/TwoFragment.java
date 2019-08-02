package com.yyxnb.arch.frag;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.widget.TextView;

import com.yyxnb.arch.R;
import com.yyxnb.arch.vm.TestViewModel;
import com.yyxnb.yyxarch.base.mvvm.BaseFragmentVM;
import com.yyxnb.yyxarch.utils.BarStyle;

import org.jetbrains.annotations.NotNull;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends BaseFragmentVM<TestViewModel> {

    private TextView tvShow;
    private String hehe = "222";

    @Override
    public int initLayoutResID() {
        return R.layout.fragment_two;
    }

    @Override
    public void initVariables(@NotNull Bundle bundle) {
        super.initVariables(bundle);
        hehe = bundle.getString("hehe", "222222");
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        hehe = getArguments().getString("hehe","");

        tvShow = fv(R.id.tvShow);

    }

    @Override
    public void initViewData() {
        super.initViewData();
        tvShow.setOnClickListener(v -> {
//            startFragment(OneFragment.newInstance());
            Bundle bundle = initArguments();
            bundle.putString("msg", "哈哈哈 " + new Random().nextInt(100));
            setResult(0x110, bundle);

            finish();
        });

//        setStatusBarColor(Color.TRANSPARENT);
//        setStatusBarStyle(BarStyle.DarkContent);
//        setStatusBarHidden(true);
//        setStatusBarTranslucent(true);

        tvShow.setText(getDebugTag());

    }

    @Override
    public int preferredStatusBarColor() {
        return Color.TRANSPARENT;
    }

    @NotNull
    @Override
    public BarStyle preferredStatusBarStyle() {
        return BarStyle.LightContent;
    }


    @Override
    public boolean isSwipeBackEnabled() {
        return true;
    }

    public static TwoFragment newInstance() {

        Bundle args = new Bundle();
        TwoFragment fragment = new TwoFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
