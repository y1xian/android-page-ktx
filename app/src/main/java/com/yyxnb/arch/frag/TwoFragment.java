package com.yyxnb.arch.frag;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.widget.TextView;

import com.yyxnb.arch.R;
import com.yyxnb.arch.vm.TestViewModel;
import com.yyxnb.yyxarch.annotation.LceeStatus;
import com.yyxnb.yyxarch.base.mvvm.BaseFragmentVM;
import com.yyxnb.yyxarch.utils.BarStyle;
import com.yyxnb.yyxarch.utils.log.LogUtils;

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

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        mViewModel.getTeam2().observe(this, baseDataLcee -> {
            switch (baseDataLcee.getStatus()) {
                case LceeStatus.Content:
                    tvShow.setText(baseDataLcee.getData().getResult().get(0).getContent());
                    LogUtils.INSTANCE.i("two Content " + LceeStatus.Content);
                    break;
                case LceeStatus.Empty:
                    LogUtils.INSTANCE.i("two Empty");
                    break;
                case LceeStatus.Error:
                    LogUtils.INSTANCE.i("two Error");
                    break;
                case LceeStatus.Loading:
                    LogUtils.INSTANCE.e("two Loading " + LceeStatus.Loading);
                    break;
            }
        });
    }

    public static TwoFragment newInstance() {

        Bundle args = new Bundle();
        TwoFragment fragment = new TwoFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
